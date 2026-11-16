# data-service

Telemetry query and topic-tree service. It exposes the historical/aggregated data
used by charts and exports, owns the `TopicTree` (signal hierarchy) API, and
accepts offline data published back into the platform. Live notifications (for
example the result of an asynchronous export) are pushed to clients through the
centralized WebSocket infrastructure of `notification-service`; this module no
longer runs a WebSocket server.

## Naming convention (`dto` / `Dto`)

All wire contracts live in `com.mapnadigital.insights.data.dto` (was
`...data.view`) and every contract type ends with `Dto` (`BaselineView` ->
`BaselineDto`, `ExportRequestView` -> `ExportRequestDto`, ...), including the
mapper (`BaselineViewMapper` -> `BaselineDtoMapper`). The tree contract is the
module's own `data.dto.topicTree.TopicTreeDto`. Fields, methods, validation
annotations and JSON property names are unchanged.

## Runtime shape

| Item | Value |
| --- | --- |
| Port | `9005` |
| Spring application name | `data-service` |
| Config Server data | `data-service, common-config, datasource, service, activemq` |
| Extra dependencies | `spring-boot-starter-activemq` |
| Database schema | `data_history` (tables and sequences) |
| Gateway exposure | `/data/**, /topic-tree/**, /baseline/**, /offline-data/**` (+ `/data/v3/api-docs`) |

```
data-service
└── com.mapnadigital.insights.data
    ├── DataApplication                @EnableJpaRepositories, @EntityScan
    ├── configuration                  SequenceSynchronizer (realigns the sequences with the tables)
    ├── controller                     DataController, OfflineDataController,
    │                                  BaselineController, TopicTreeController (extend Base)
    ├── documentation                  ApiDocumentation (shared Swagger texts and examples)
    ├── service (4 interfaces)         IDataService, IBaselineService,
    │                                  IOfflineDataService, ITopicTreeService
    ├── service/impl                   DataService, BaselineService, OfflineDataService,
    │                                  TopicTreeService
    ├── repository                     DataRepository, BaselineRepository,
    │                                  TopicTreeRepository, IOfflineDataRepository
    │                                  (+ repository/impl/OfflineDataRepository)
    ├── model/entity                   Data, Baseline, TopicTree
    ├── model/view                     DataProjection (native query projection)
    ├── api                            ApiClient, DeviceTypeClient (REST to device-service)
    ├── api/dto                        DeviceTypeName
    ├── mapper                         MapStruct mappers (BaselineDtoMapper, TopicTreeMapper)
    ├── dto                            9 request/response DTOs
    └── util/Constant                  queue/topic names and WebSocket message types
```

`src/main/resources` holds `bootstrap.yml` (Config Server), `application.yml` (`spring.sql.init.mode: always`),
`schema.sql` (the `data_history` schema and the three sequences, executed before the Hibernate schema update) and
the two message bundles.

## Controller -> service -> dto flow

| Layer | Responsibility |
| --- | --- |
| Controller | Binds/validates the DTO (`@Valid`) or query parameters and returns the DTO produced by the service (`DataController`, `BaselineController`, `TopicTreeController`, `OfflineDataController`). No mapping. |
| Service | Entity-based internally, DTO-based on the contract. `BaselineService` maps `BaselineDto` <-> `Baseline`; `TopicTreeService` loads `TopicTree` entities, assembles the tree and returns `data.dto.topicTree.TopicTreeDto` (including `findByPath`, which used to leak the entity); `DataService.aggregate` returns `List<DataValueDto>` straight from the repository projection and `getTopicTree` returns `TopicTreeDto`; `OfflineDataService` forwards `OfflineDataDto` to the stored procedure. |
| Mapper | `BaselineDtoMapper` (entity <-> DTO, including the partial update) and the module's own `mapper.TopicTreeMapper` (entity <-> `data.dto.topicTree.TopicTreeDto`). `DataMapper` was deleted: the projection is already the response shape (`DataValueDto.x` = timestamp, `DataValueDto.y` = value). |
| DTO | `BaselineDto`, `OfflineDataDto`, `SignalDto`, `SignalSelectorDto`, `ExportDataProjectionDto`, `ExportRequestDto`, `DataValueDto`, `ValueDto`, `DateDto`, `TopicTreeDto`. |


### Legacy contracts removed (`dto.*DTO` -> `dto.*Dto`)

The classes in the `Removed` column are gone; their names are written in the
post-rename `Dto` spelling. The `Replaced by` column names the current
`dto.*Dto` contracts (the types that were briefly called `*View` before the
project-wide `View` -> `Dto` rename).

| Removed | Replaced by |
| --- | --- |
| `dto.BaselineDTO` + `mapper/BaselineMapper` (identity) | `dto.BaselineDto`; `BaselineDtoMapper` maps the entity directly. |
| `dto.OfflineDataDTO` + `mapper/OfflineDataMapper` | `dto.OfflineDataDto`; the repository stores the DTO directly. |
| `dto.SignalDTO` + `mapper/SignalMapper` (identity) | `dto.SignalDto` (same fields plus validation). |
| `dto.TopicTreeDTO` + `mapper/TopicTreeDTOMapper` / `TopicTreeDtoMapper` | `data.dto.topicTree.TopicTreeDto`, which now owns the tree helpers (`getChildren`, `setChildren`, `addChild`, `clone`). `level`, `logTime` and `priority` are `@JsonIgnore`d, matching the previous two-hop mapping. |
| `dto.DataProjectionDTO`, `dto.ExportDataProjectionDTO`, `dto.ExportRequest` | `dto.DataProjectionDto`, `dto.ExportDataProjectionDto`, `dto.ExportRequestDto` (the field `signalDTOs` became `signals`). |
| `dto.DataProjectionDto` + `mapper.DataMapper` | Nothing. `DataService.aggregate(...)` returns `List<DataValueDto>` (`x` = timestamp, `y` = decorated value) built directly from the `DataRepository` projection, and `DataController` puts that list into `ValueDto.data`. |
| `common.dto.device.DeviceDTO` / `common.dto.userAsset.UserAssetDTO` | `common.dto.device.DeviceDto` / `common.dto.userAsset.UserAssetDto`. |

`ExportRequestDto` is both the `/data/signal-selector/export` request body and the
ActiveMQ message consumed by `exportSignalSelector`; the queue name and the
concurrency constant are unchanged.

### Service contracts (no entity exposure)

| Service | Contract | DTO / Mapper |
| --- | --- | --- |
| `ITopicTreeService.findByPath(String)` | changed from `TopicTree findByPath(String)` to `TopicTreeDto findByPath(String)` | `data.dto.topicTree.TopicTreeDto` + the module's `mapper.TopicTreeMapper` |
| `IDataService.getTopicTree(String)` | changed from `TopicTree getTopicTree(String)` to `TopicTreeDto getTopicTree(String)` | same |

`BaselineService.save` is the only caller that needs the entity: it maps the
`TopicTreeDto` back with `TopicTreeMapper.targetToSource` before assigning it to
`Baseline.topicTree` (a `@ManyToOne`), so the persisted row is unchanged. All other
callers (`DataController`, `DataService.createExcel`, the baseline lookup) only read
`unit`, `topic`, `description` and `id` and use the DTO directly.

### Verification of the DTO refactor

- `mvn -o clean test-compile` succeeds (43 main sources, 2 MapStruct
  implementations).
- `DataService.aggregate` returns `List<DataValueDto>` with the same `x`/`y` keys the
  former `DataProjectionDto` produced, so the chart response is unchanged.
- `TopicTreeDto.getChildren()` keeps the former DTO's "never null" behaviour, so a
  leaf node still serialises an empty `children` array and the response shape is
  unchanged.
- The asynchronous export path and every endpoint path are unchanged; the export
  result is now published through `notification-service` instead of a local
  WebSocket handler.

## HTTP API

| Method | Path | Handler | Description |
| --- | --- | --- | --- |
| `POST` | `/data/signal-selector` | `signalSelector` | Time-series query over selected signals (`SignalSelectorDto` -> `ValueDto[]`) |
| `POST` | `/data/get-data` | `signalSelector` | Same query addressed by `serial` + `param` instead of `topic` |
| `POST` | `/data/signal-selector/export` | `signalSelectorExport` | Asynchronous export of the same query (`SignalSelectorDto` -> `exportUuid`) |
| `POST` | `/baseline` | `get` | Baseline of the requested topic/interval |
| `PUT` | `/baseline` | `updateOrDelete` | Create/update/remove a baseline (`value == null` deletes it) |
| `POST` | `/offline-data/publish` | `publish` | Persist data collected offline by a client (`OfflineDataDto`) |
| `GET` | `/topic-tree/root-tree` | `rootTree` | Root topics with children |
| `GET` | `/topic-tree/children-tree/{parentId}` | `childrenTree` | Sub-tree of a node |
| `GET` | `/topic-tree/search` | `search` | Topic search (`deviceName`, `serialNumber`, `description`, `company`) |
| `GET` | `/topic-tree/get` | `getTopics` | Leaf topics of a device type (`?deviceTypeId=`) |
| `GET` | `/topic-tree/get/{id}` | `getTopic` | Single topic (internal, used by `alarm-service`) |
| `GET` | `/topic-tree/get-all-by-ids` | `getTopicsByIds` | Topics of the given ids (`?ids=1,2,3`), used by `alarm-service` for batched look-ups |
| `GET` | `/topic-tree/root/get-all` | `getAllRoot` | All root topics |
| `GET` | `/topic-tree/date` | `date` | Current date of the server, used to initialise the UI date pickers |
| `PUT` | `/topic-tree/update` | `update` | Update topic metadata |

`/topic-tree/get`, `/topic-tree/get/{id}`, `/topic-tree/get-all-by-ids` and
`/topic-tree/root/get-all` are also published to other services through
`service.yml` as `topicTree-get-by-topic`, `topicTree-getById`,
`topicTree-getAllByIds` and `topicTree-get-all-root-topics`.

`topicTree-getById` and `topicTree-getAllByIds` are what `alarm-service` uses: it
owns its entities, so `AlarmDefinition.signalId` / `AlarmEvent.signalId` are read
back through this API (`TopicInfo`) instead of a JPA relation to `data_history.topic_tree`.

## Swagger / OpenAPI

The documentation is generated by `springdoc-openapi` (`springdoc-openapi-starter-webmvc-ui`, inherited from
`map-web`); no extra dependency is needed.

| Item | Value |
| --- | --- |
| OpenAPI document | `GET /v3/api-docs` (JSON, OpenAPI 3.1) |
| Swagger UI | `GET /swagger-ui.html` |
| Gateway aggregation | `GET /data/v3/api-docs` (route `data-service-docs` -> `http://data-svc/v3/api-docs`) |
| Swagger UI entry | `data-service` in the `springdoc.swagger-ui.urls` list of `gateway-service.yml` |
| Security schemes | `JWT Token`, `Language`, `sys` — declared once by `map-web`'s `SwaggerConfig` |
| Public paths | `/swagger-ui/**`, `/v3/api-docs/**` and `/*/v3/api-docs/**` are `permitAll` in `map-web`'s `SecurityFilter` |

### What is documented

- **Tags** — one per controller, with a description of the domain: `Telemetry data` (`/data`), `Topic tree`
  (`/topic-tree`), `Baselines` (`/baseline`) and `Offline data` (`/offline-data`).
- **Operations** — every handler carries `@Operation(summary, description)`: what the endpoint returns, which
  business codes it can report, which role it requires and how the envelope behaves.
- **Parameters** — every path and query parameter carries `@Parameter` with its description, whether it is
  required and an example (`parentId`, `id`, `ids`, `deviceTypeId`, `deviceName`, `serialNumber`, `description`,
  `company`).
- **Request bodies** — every body carries `@io.swagger.v3.oas.annotations.parameters.RequestBody` with the schema
  of the DTO, the list of mandatory fields and one or more named examples.
- **Responses** — every operation declares `200`, `400` (bean validation), `401` (missing/invalid JWT), `403`
  (role not granted) and `500` (unexpected error), with the response schema and an example of the envelope.
- **DTOs** — `SignalSelectorDto`, `SignalDto`, `ValueDto`, `DataValueDto`, `BaselineDto`, `TopicTreeDto`,
  `DateDto`, `OfflineDataDto`, `ExportRequestDto` and `ExportDataProjectionDto` carry `@Schema` on the type and on
  every field: description, example, `requiredMode` for the mandatory fields and `accessMode = READ_ONLY` for the
  server-filled fields (`id`, `values`, `exportUuid`).
- **Authentication** — every operation declares `@SecurityRequirements` with the `JWT Token`, `Language` and
  (where the realm matters) `sys` schemes, so Swagger UI shows the *Authorize* button and sends the headers.

The shared texts (tag descriptions, the envelope note, the `400`/`401`/`403`/`500` descriptions and the examples)
live in `com.mapnadigital.insights.data.documentation.ApiDocumentation`, so a controller only references constants
instead of repeating prose.

`OpenApiDocumentationTest` boots the four controllers in a plain MVC context (services mocked) and asserts that
`/v3/api-docs` is generated with all fourteen paths, the four tags and the three security schemes.

## Asynchronous export

Signal-selector exports are not produced inline:

```
POST /data/signal-selector/export
  -> DataService.signalSelectorExport()   builds ExportRequestDto, sends via JmsTemplate
  -> ActiveMQ queue "signal-selector-export"
  -> @JmsListener(destination = SIGNAL_SELECTOR_EXPORT_CONSUMER_QUEUE_NAME,
                  concurrency = SIGNAL_SELECTOR_EXPORT_CONSUMER_QUEUE_CONCURRENCY)
     DataService.exportSignalSelector()   reads data, writes the export file
```

Both the queue name (`signal-selector-export`) and the concurrency value are
constants in `com.mapnadigital.insights.data.util.Constant`, so the
worker count is a build-time decision, not a YAML property.

## WebSocket notifications (delegated to notification-service)

This module no longer runs a WebSocket server. The asynchronous export consumer
pushes its result to the requesting client through the centralized WebSocket
infrastructure of `notification-service`:

```
POST /data/signal-selector/export
  -> DataService.export()               captures the JWT of the caller in ExportRequestDto
  -> ActiveMQ queue "signal-selector-export"
  -> @JmsListener consumeExportRequestView()
       -> NotificationApiClient.publish(WebSocketPublishRequest, token)
       -> POST http://notification-svc/notification/ws/publish
       -> notification-service delivers a WebSocketMessage to the user's sessions
```

The message is a generic `WebSocketMessage` (`map-common`) whose `type` is
`data.export` on success and `data.export.error` on failure (constants in
`util/Constant`), so the client can dispatch it to the matching handler. The
`token` field of `ExportRequestDto` carries the JWT captured in the request
thread, because the JMS listener has no request context to read it from.

Clients connect to the gateway (`ws(s)://<gateway>/ws`) and authenticate with
their usual JWT; see the `notification-service` README for the full contract.

## Data model (owned by this module)

All tables **and** all identifier sequences live in the **`data_history`** schema. The physical names are declared
explicitly on every `@Table` / `@Column` / `@JoinColumn`, so the generated schema does not depend on the Hibernate
naming strategy of the runtime and stays aligned with the native statements of `DataRepository`,
`OfflineDataRepository` and `TopicTreeRepository.findLeafTopics`.

| Entity | Table | Role |
| --- | --- | --- |
| `Data` | `data_history.data` | Stored telemetry samples (`topic`, `date_time`, `value`) |
| `Baseline` | `data_history.device_baseline_values` | Per-device/signal baseline used for comparisons; the device is referenced by `deviceId` only |
| `TopicTree` | `data_history.topic_tree` | Signal hierarchy, topic paths and metadata |

The three entities live in `com.mapnadigital.insights.data.model.entity` and are owned by this module; they were
moved out of `map-common` so the module no longer imports an entity of the shared library. `DataApplication`
declares `@EntityScan(basePackages = "com.mapnadigital.insights.data.model.entity")`.

`OfflineDataRepository` is a hand-written implementation behind `IOfflineDataRepository` (no JPA interface) used for
the offline-ingestion path.

### Entity relationships

```
TopicTree (data_history.topic_tree)
  ├── parent_id ──────────────► TopicTree.id          @ManyToOne(EAGER), self reference
  └── id ◄──────────────────── Baseline.topic_tree_id @ManyToOne(LAZY),  one topic tree node has many baselines

Baseline (data_history.device_baseline_values)
  └── device_id ──────────────► device.devices.id     plain Long, the device aggregate belongs to device-service

Data (data_history.data)
  └── no relation
```

`Baseline.deviceId` is deliberately **not** a JPA association: the device aggregate belongs to `device-service`, so
the module keeps the column (and therefore the schema) without depending on the device domain. The device is
resolved from the topic over REST (`device-get-by-serial`) and only its identifier is stored.

### Columns, keys and constraints

| Table | Primary key | Foreign keys | Unique constraints | Indexes |
| --- | --- | --- | --- | --- |
| `data_history.topic_tree` | `id` (sequence `data_history.topic_tree_id_seq`) | `fk_topic_tree_parent (parent_id -> data_history.topic_tree.id)` | `uk_topic_tree_topic_parent (topic, parent_id)` | `idx_topic_tree_parent (parent_id)`, `idx_topic_tree_path (path)` |
| `data_history.device_baseline_values` | `id` (sequence `data_history.device_baseline_values_id_seq`) | `fk_device_baseline_values_topic_tree (topic_tree_id -> data_history.topic_tree.id)` | `uk_device_baseline_values_device_topic_interval (device_id, topic_tree_id, interval)` | `idx_device_baseline_values_device (device_id)`, `idx_device_baseline_values_topic_tree (topic_tree_id)` |
| `data_history.data` | `id` (sequence `data_history.data_id_seq`) | — | — | `idx_data_topic (topic)`, `idx_data_date_time (date_time)` |

Column notes:

- `topic_tree.topic` and `topic_tree.log_time` are `not null`; `log_time` is a `timestamp(6)` column, which is why
  the entity maps it as `java.util.Date` (a `java.sql.Date` would generate a `date` column and lose the time).
- `topic_tree.is_visible` is a `boolean`; `is_property`, `level` and `priority` are `integer`.
- `device_baseline_values.device_id`, `topic_tree_id` and `value` are `not null`; `interval` is nullable because a
  baseline may be stored without an interval.
- `data.date_time` is a `timestamp(6)` and `data.value` is a `varchar(255)` because the raw MQTT payload is stored
  as received.
- `interval` and `value` are non-reserved keywords in PostgreSQL, so they are used unquoted, exactly like the
  columns of the existing database.

### Sequence-based identifier generation

Every primary key is generated by a database sequence, never by the application. The sequences live in
`data_history`, the same schema as the tables:

| Entity | Generator | Sequence | Column default |
| --- | --- | --- | --- |
| `TopicTree` | `topic_tree_seq` | `data_history.topic_tree_id_seq` | `nextval('data_history.topic_tree_id_seq')` |
| `Baseline` | `device_baseline_values_seq` | `data_history.device_baseline_values_id_seq` | `nextval('data_history.device_baseline_values_id_seq')` |
| `Data` | `data_seq` | `data_history.data_id_seq` | `nextval('data_history.data_id_seq')` |

The strategy is declared with `@GeneratedValue(strategy = GenerationType.SEQUENCE)` plus a `@SequenceGenerator`
that names the sequence, its schema, `initialValue = 1` and **`allocationSize = 1`**:

- `allocationSize = 1` makes Hibernate call `nextval` for every insert instead of reserving a block of
  identifiers. A block size larger than the increment of the database sequence is the classic cause of
  "sequence out of sync" errors, because the identifiers Hibernate hands out are not the ones the sequence
  actually produced. With an allocation size of one, the JPA inserts and the manual SQL inserts share the same
  counter.
- The database-level default is declared with `@DialectOverride.ColumnDefault(dialect = PostgreSQLDialect.class,
  override = @ColumnDefault("nextval('data_history.<table>_id_seq')"))`. It is a *dialect override*, so the
  PostgreSQL-specific `nextval` clause is only emitted for PostgreSQL: the DDL exported for H2, MySQL or Oracle
  stays portable and only declares the sequence. The default is what makes a row inserted by hand without `id`
  receive the next sequence value:

  ```sql
  INSERT INTO data_history.topic_tree (topic, log_time) VALUES ('lw_boiler', now());  -- id is generated
  ```

  The schema of the default **must** be the schema of the sequence (`data_history`). A default that points at
  another schema (`device.<seq>`) makes PostgreSQL reject the `CREATE TABLE` with
  `relation "device.<seq>" does not exist`, which is what prevented the module from starting.
- `id` is mapped with `nullable = false, updatable = false`, so a null primary key is rejected by the schema and
  the identifier is never part of an `UPDATE`.

### Schema generation and start-up order

`spring.jpa.hibernate.ddl-auto` is `update` in the Config Server (`datasource.yml`), so Hibernate creates the
missing tables, indexes and foreign keys at start-up. Because the `id` column default references a sequence, the
sequence has to exist **before** Hibernate issues the `CREATE TABLE`. Two mechanisms guarantee that:

1. **`src/main/resources/schema.sql`** is executed by Spring Boot (`spring.sql.init.mode: always` in
   `application.yml`) *before* the Hibernate schema update, because `spring.jpa.defer-datasource-initialization`
   stays `false`. It creates the `data_history` schema and the three sequences with `IF NOT EXISTS`, so it is
   idempotent: it never drops a table, truncates data or rewinds a sequence.
2. **Hibernate itself** creates the sequences before the tables when they are missing (its schema migrator applies
   namespaces, then sequences, then tables, then indexes, then foreign keys). `SchemaGenerationTest` asserts that
   order on the exported DDL.

`SequenceSynchronizer` (`configuration/SequenceSynchronizer`) runs once the context is up, i.e. after Hibernate has
created or updated the tables, and realigns every sequence with the rows already stored:

```sql
SELECT setval('data_history.topic_tree_id_seq',
              COALESCE((SELECT MAX(id) FROM data_history.topic_tree), 0) + 1,
              false);
```

This is what prevents duplicate identifiers when the tables already contain rows (restored dump, previous run,
rows written by another process): without it a sequence that is behind the data hands out identifiers that are
already used and the insert fails with a duplicate key. The synchronisation is idempotent, only moves the sequence
to the first free identifier and never deletes or rewrites a row. A table that does not exist yet is skipped with a
warning instead of failing the start-up.

### Database functions

Two PL/pgSQL functions are used by the module and must be deployed in the `data_history` schema together with the
tables (they are database objects, not JPA entities):

| Function | Used by | Purpose |
| --- | --- | --- |
| `data_history.fnc_get_aggr_data(topic, from, to, aggregation, interval, property)` | `DataRepository.aggregate` | Aggregates the telemetry of a topic over a period |
| `data_history.fnc_save_data(topic, value, datetime)` | `OfflineDataRepository.save` | Stores an offline sample exactly like a live MQTT sample |

Both calls used to be qualified with `cmn` and then with `device`; they follow the schema of the tables.


## Cross-module calls

`TopicTreeRepository.findAllRoot` used to sub-select `DeviceType.name` from `device-service`. The
names are now read over REST through `DeviceTypeApi` (`ServiceName.DEVICE_TYPE_GET_ALL_NAMES` ->
`GET /device-type/get-all-names`) and passed to the query as a parameter, so the module never joins
the device type table. `BaselineService` keeps using `ServiceName.DEVICE_FIND_BY_SERIAL` to resolve
the device of a topic and now stores only its identifier.

## Notes

- Queries are projection-based (`DataValueDto` on the chart path,
  `ExportDataProjectionDto` on the export path) rather than entity-based, which
  keeps large result sets out of the persistence context.
- `TopicTreeService` is `@Transactional`; all controllers extend `map-web`'s
  `Base` and answer with the standard envelope.
- The WebSocket endpoint is owned by `notification-service` and routed by the
  gateway (`/ws` -> `ws://notification-svc`); this module only publishes to it
  through `NotificationApiClient`.
- `/topic-tree/get/{id}` and `/topic-tree/get-all-by-ids` are internal
  service-to-service endpoints. They used to be `@Hidden`; they are now
  documented in Swagger with that note, so the OpenAPI document describes every
  endpoint the module exposes.

## Verification

| Check | How |
| --- | --- |
| Compilation | `mvn -o clean test-compile` (44 main sources, 2 MapStruct implementations) |
| Schema generation | `SchemaGenerationTest` — exports the PostgreSQL DDL and asserts the `data_history` schema, sequences, tables, keys, constraints, indexes and `nextval` defaults, and that every sequence is created before its table |
| Application start-up | `DataApiServiceTest` boots the real application with `SPRING_PROFILES_ACTIVE=dev` (Config Server + PostgreSQL): Tomcat starts on `9005`, Hibernate creates the three tables without a DDL error and `SequenceSynchronizer` realigns the three sequences |
| Tables and constraints | `data_history.topic_tree`, `data_history.device_baseline_values` and `data_history.data` exist with their primary keys, `uk_topic_tree_topic_parent`, `uk_device_baseline_values_device_topic_interval`, `fk_topic_tree_parent`, `fk_device_baseline_values_topic_tree` and the six indexes |
| Sequences | `data_history.topic_tree_id_seq`, `data_history.device_baseline_values_id_seq` and `data_history.data_id_seq` exist in `data_history`; no sequence is created in `device` |
| Direct SQL inserts | `INSERT INTO data_history.topic_tree (topic, log_time) VALUES (...)` without `id` returns `1`, `2`, … from the column default |
| JPA inserts | `TopicTreeRepository.save`, `BaselineRepository.save` and `DataRepository.save` return entities with a generated `id`; the JPA and the direct SQL inserts share the same sequence |
| OpenAPI | `OpenApiDocumentationTest` — `/v3/api-docs` returns the 14 paths, the 4 tags and the `JWT Token` / `Language` / `sys` security schemes |

```bash
# unit tests only (no infrastructure needed)
mvn -o test -Dtest='SchemaGenerationTest,OpenApiDocumentationTest'

# start-up test (needs the Config Server and the database)
SPRING_PROFILES_ACTIVE=dev mvn -o test -Dtest=DataApiServiceTest
```

## Build and run

```bash
./dev build && ./dev run
```

