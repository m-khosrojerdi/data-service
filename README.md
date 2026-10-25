# data-service

Telemetry query and topic-tree service. It exposes the historical/aggregated data
used by charts and exports, owns the `TopicTree` (signal hierarchy) API, serves
live data over WebSocket, and accepts offline data published back into the
platform.

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
└── com.mapnadigital.insights.insights.data (43 classes)
    ├── DataApplication                @EnableJpaRepositories
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
    ├── mapper                         7 MapStruct mappers
    ├── dto                            DataProjectionDTO, ExportDataProjectionDTO, ExportRequest,
    │                                  BaselineDTO, OfflineDataDTO, SignalDTO, TopicTreeDTO
    ├── view                           7 response views
    └── util/Constant                  queue/topic names
```

## HTTP API

| Method | Path | Handler | Description |
| --- | --- | --- | --- |
| `POST` | `/data` | `signalSelector` | Time-series query over selected signals (`SignalDTO` -> `DataProjectionDTO`) |
| `POST` | `/data/signal-selector/export` | `signalSelectorExport` | Asynchronous export of the same query (`ExportRequest`) |
| `POST` | `/baseline` | `get` | Baseline values for the requested devices/signals |
| `PUT` | `/baseline` | `updateOrDelete` | Create/update/remove baselines |
| `POST` | `/offline-data/publish` | `publish` | Persist data collected offline by a client (`OfflineDataDTO`) |
| `GET` | `/topic-tree/root-tree` | `rootTree` | Root topics with children |
| `GET` | `/topic-tree/children-tree/{parentId}` | `childrenTree` | Sub-tree of a node |
| `GET` | `/topic-tree/search` | `search` | Topic search |
| `GET` | `/topic-tree/get` | `getTopics` | Topic list |
| `GET` | `/topic-tree/get/{id}` | `getTopic` | Single topic |
| `GET` | `/topic-tree/root/get-all` | `getAllRoot` | All root topics |
| `GET` | `/topic-tree/date` | `date` | Latest data timestamp for the topics |
| `PUT` | `/topic-tree/update` | `update` | Update topic metadata |

`/topic-tree/get`, `/topic-tree/get/{id}` and `/topic-tree/root/get-all` are also
published to other services through `service.yml` as `topicTree-get-by-topic`,
`topicTree-getById` and `topicTree-get-all-root-topics`.

## Asynchronous export

Signal-selector exports are not produced inline:

```
POST /data/signal-selector/export
  -> DataService.signalSelectorExport()   builds ExportRequest, sends via JmsTemplate
  -> ActiveMQ queue "signal-selector-export"
  -> @JmsListener(destination = SIGNAL_SELECTOR_EXPORT_CONSUMER_QUEUE_NAME,
                  concurrency = SIGNAL_SELECTOR_EXPORT_CONSUMER_QUEUE_CONCURRENCY)
     DataService.exportSignalSelector()   reads data, writes the export file
```

Both the queue name (`signal-selector-export`) and the concurrency value are
constants in `com.mapnadigital.insights.insights.data.util.Constant`, so the
worker count is a build-time decision, not a YAML property.

## WebSocket live data

`WebSocketConfig` registers the handler at **`/ws`** with
`JwtHandshakeInterceptor` and `setAllowedOriginPatterns("*")`. The handler is
`WebSocketService` (`TextWebSocketHandler`), which pushes live telemetry to
connected dashboards.

## Data model (`map-common` entities)

| Entity | Table | Role |
| --- | --- | --- |
| `Data` | `Data` (no explicit `@Table`) | Stored telemetry samples |
| `Baseline` | `cmn.device_baseline_values` | Per-device/signal baseline used for comparisons |
| `TopicTree` | `cmn.topicTree` | Signal hierarchy, topic paths and metadata |

`OfflineDataRepository` is a hand-written implementation behind
`IOfflineDataRepository` (no JPA interface) used for the offline-ingestion path.

## Notes

- Queries are projection-based (`DataProjectionDTO`,
  `ExportDataProjectionDTO`) rather than entity-based, which keeps large result
  sets out of the persistence context.
- `TopicTreeService` is `@Transactional`; all controllers extend `map-web`'s
  `Base` and answer with the standard envelope.
- The WebSocket endpoint must be routed by the reverse proxy in front of the
gateway as well (the gateway route only covers the HTTP paths).

## Build and run

```bash
./dev build && ./dev run
```
