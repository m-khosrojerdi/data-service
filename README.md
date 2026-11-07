# data-service

Telemetry query and topic-tree service. It exposes the historical/aggregated data
used by charts and exports, owns the `TopicTree` (signal hierarchy) API, serves
live data over WebSocket, and accepts offline data published back into the
platform.

## Naming convention (`dto` / `Dto`)

All wire contracts live in `com.mapnadigital.insights.data.dto` (was
`...data.view`) and every contract type ends with `Dto` (`BaselineView` ->
`BaselineDto`, `ExportRequestView` -> `ExportRequestDto`, ...), including the
mapper (`BaselineViewMapper` -> `BaselineDtoMapper`). The shared tree contract is
`common.dto.topicTree.TopicTreeDto`. Fields, methods, validation annotations and
JSON property names are unchanged.

## Runtime shape

| Item | Value |
| --- | --- |
| Port | `9005` |
| Spring application name | `data-service` |
| Config Server data | `data-service, common-config, datasource, service, activemq` |
| Extra dependencies | `spring-boot-starter-websocket`, `spring-boot-starter-activemq` |
| Gateway exposure | `/data/**, /topic-tree/**, /baseline/**, /offline-data/**` plus the raw WebSocket paths `/ws`, `/ws/**` (+ `/data/v3/api-docs`) |

```
data-service
└── com.mapnadigital.insights.data (36 classes)
    ├── DataApplication                @EnableJpaRepositories, @EntityScan
    ├── configuration
    │   ├── WebSocketConfig            @EnableWebSocket, WebSocketConfigurer
    │   └── JwtHandshakeInterceptor    authenticates the WebSocket handshake
    ├── controller                     DataController, OfflineDataController,
    │                                  BaselineController, TopicTreeController (extend Base)
    ├── service (4 interfaces)         IDataService, IBaselineService,
    │                                  IOfflineDataService, ITopicTreeService
    ├── service/impl                   + WebSocketService (TextWebSocketHandler)
    ├── repository                     DataRepository, BaselineRepository,
    │                                  TopicTreeRepository, IOfflineDataRepository
    │                                  (+ repository/impl/OfflineDataRepository)
    ├── entity                         Data, Baseline, TopicTree
    ├── client                         DeviceTypeClient (REST to device-service)
    ├── client/dto                     DeviceTypeName
    ├── mapper                         MapStruct mappers (BaselineDtoMapper, TopicTreeMapper)
    ├── dto                           9 request/response DTOs
    └── util/Constant                  queue/topic names
```

## Controller -> service -> dto flow

| Layer | Responsibility |
| --- | --- |
| Controller | Binds/validates the DTO (`@Valid`) or query parameters and returns the DTO produced by the service (`DataController`, `BaselineController`, `TopicTreeController`, `OfflineDataController`). No mapping. |
| Service | Entity-based internally, DTO-based on the contract. `BaselineService` maps `BaselineDto` <-> `Baseline`; `TopicTreeService` loads `TopicTree` entities, assembles the tree and returns `common.dto.topicTree.TopicTreeDto` (including `findByPath`, which used to leak the entity); `DataService.aggregate` returns `List<DataValue>` straight from the repository projection and `getTopicTree` returns `TopicTreeDto`; `OfflineDataService` forwards `OfflineDataDto` to the stored procedure. |
| Mapper | `BaselineDtoMapper` (entity <-> DTO, including the partial update) and the module's own `mapper.TopicTreeMapper` (entity <-> `common.dto.topicTree.TopicTreeDto`). `DataMapper` was deleted: the projection is already the response shape (`DataValue.x` = timestamp, `DataValue.y` = value). |
| DTO | `BaselineDto`, `OfflineDataDto`, `SignalDto`, `SignalSelectorDto`, `ExportDataProjectionDto`, `ExportRequestDto`, `DataValueDto`, `ValueDto`, `DateDto` plus the shared `TopicTreeDto`. |

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
| `dto.TopicTreeDTO` + `mapper/TopicTreeDTOMapper` / `TopicTreeDtoMapper` | `common.dto.topicTree.TopicTreeDto`, which now owns the tree helpers (`getChildren`, `setChildren`, `addChild`, `clone`). `level`, `logTime` and `priority` are `@JsonIgnore`d, matching the previous two-hop mapping. |
| `dto.DataProjectionDTO`, `dto.ExportDataProjectionDTO`, `dto.ExportRequest` | `dto.DataProjectionDto`, `dto.ExportDataProjectionDto`, `dto.ExportRequestDto` (the field `signalDTOs` became `signals`). |
| `dto.DataProjectionDto` + `mapper.DataMapper` | Nothing. `DataService.aggregate(...)` returns `List<DataValue>` (`x` = timestamp, `y` = decorated value) built directly from the `DataRepository` projection, and `DataController` puts that list into `ValueDto.data`. |
| `common.dto.device.DeviceDTO` / `common.dto.userAsset.UserAssetDTO` | `common.dto.device.DeviceDto` / `common.dto.userAsset.UserAssetDto`. |

`ExportRequestDto` is both the `/data/signal-selector/export` request body and the
ActiveMQ message consumed by `exportSignalSelector`; the queue name and the
concurrency constant are unchanged.

### Service contracts (no entity exposure)

| Service | Contract | DTO / Mapper |
| --- | --- | --- |
| `ITopicTreeService.findByPath(String)` | changed from `TopicTree findByPath(String)` to `TopicTreeDto findByPath(String)` | `common.dto.topicTree.TopicTreeDto` + shared `common.mapper.topicTree.TopicTreeMapper` |
| `IDataService.getTopicTree(String)` | changed from `TopicTree getTopicTree(String)` to `TopicTreeDto getTopicTree(String)` | same |

`BaselineService.save` is the only caller that needs the entity: it maps the
`TopicTreeDto` back with `TopicTreeMapper.targetToSource` before assigning it to
`Baseline.topicTree` (a `@ManyToOne`), so the persisted row is unchanged. All other
callers (`DataController`, `DataService.createExcel`, the baseline lookup) only read
`unit`, `topic`, `description` and `id` and use the DTO directly.

### Verification

- `mvn -o clean test-compile` succeeds (32 main sources, 1 MapStruct
  implementation).
- `DataService.aggregate` returns `List<DataValue>` with the same `x`/`y` keys the
  former `DataProjectionDto` produced, so the chart response is unchanged.
- `TopicTreeDto.getChildren()` keeps the former DTO's "never null" behaviour, so a
  leaf node still serialises an empty `children` array and the response shape is
  unchanged.
- The asynchronous export path, the WebSocket handler and every endpoint path are
  untouched.

## HTTP API

| Method | Path | Handler | Description |
| --- | --- | --- | --- |
| `POST` | `/data` | `signalSelector` | Time-series query over selected signals (`SignalDto` -> `DataValue[]`) |
| `POST` | `/data/signal-selector/export` | `signalSelectorExport` | Asynchronous export of the same query (`ExportRequestDto`) |
| `POST` | `/baseline` | `get` | Baseline values for the requested devices/signals |
| `PUT` | `/baseline` | `updateOrDelete` | Create/update/remove baselines |
| `POST` | `/offline-data/publish` | `publish` | Persist data collected offline by a client (`OfflineDataDto`) |
| `GET` | `/topic-tree/root-tree` | `rootTree` | Root topics with children |
| `GET` | `/topic-tree/children-tree/{parentId}` | `childrenTree` | Sub-tree of a node |
| `GET` | `/topic-tree/search` | `search` | Topic search |
| `GET` | `/topic-tree/get` | `getTopics` | Topic list |
| `GET` | `/topic-tree/get/{id}` | `getTopic` | Single topic |
| `GET` | `/topic-tree/get-all-by-ids` | `getTopicsByIds` | Topics of the given ids (`?ids=1,2,3`), used by `alarm-service` for batched look-ups |
| `GET` | `/topic-tree/root/get-all` | `getAllRoot` | All root topics |
| `GET` | `/topic-tree/date` | `date` | Latest data timestamp for the topics |
| `PUT` | `/topic-tree/update` | `update` | Update topic metadata |

`/topic-tree/get`, `/topic-tree/get/{id}`, `/topic-tree/get-all-by-ids` and
`/topic-tree/root/get-all` are also published to other services through
`service.yml` as `topicTree-get-by-topic`, `topicTree-getById`,
`topicTree-getAllByIds` and `topicTree-get-all-root-topics`.

`topicTree-getById` and `topicTree-getAllByIds` are what `alarm-service` uses: it
owns its entities, so `AlarmDefinition.signalId` / `AlarmEvent.signalId` are read
back through this API (`TopicInfo`) instead of a JPA relation to `cmn.topicTree`.

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

## WebSocket live data

`WebSocketConfig` registers the handler at **`/ws`** with
`JwtHandshakeInterceptor` and `setAllowedOriginPatterns("*")`. The handler is
`WebSocketService` (`TextWebSocketHandler`), which pushes live telemetry to
connected dashboards.

## Data model (owned by this module)

| Entity | Table | Role |
| --- | --- | --- |
| `Data` | `Data` (no explicit `@Table`) | Stored telemetry samples |
| `Baseline` | `cmn.device_baseline_values` | Per-device/signal baseline used for comparisons; the device is referenced by `deviceId` only |
| `TopicTree` | `cmn.topicTree` | Signal hierarchy, topic paths and metadata |

The three entities live in `com.mapnadigital.insights.data.entity` and are owned by this
module; they were moved out of `map-common` so the module no longer imports an entity of the shared
library. The tables and columns are unchanged (`Baseline.device` became `deviceId` over the same
`device_id` column). `DataApplication` declares
`@EntityScan(basePackages = "com.mapnadigital.insights.data.entity")`.

`OfflineDataRepository` is a hand-written implementation behind
`IOfflineDataRepository` (no JPA interface) used for the offline-ingestion path.

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
- The WebSocket endpoint must be routed by the reverse proxy in front of the
gateway as well (the gateway route only covers the HTTP paths).

## Build and run

```bash
./dev build && ./dev run
```
