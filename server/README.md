# `:server` — CryptoTracker Backend

A Ktor (Netty) backend that serves the CMP client's coin, market, and
historical-candle data. It replaces direct CoinCap access from the client
with a backend the app now controls: Postgres (Supabase) for coin/market
snapshots and candle rollups, kept fresh by recurring jobs in a separate
data-platform repo.

Deployed on Google Cloud Run (`min-instances=0`); built and pushed from a
local Ubuntu box without Cloud Build, to keep costs near zero.

## Architecture

Layered, DDD-inspired structure — routes are thin controllers, business
logic lives in services, persistence is abstracted behind repository
interfaces, and wire-format DTOs are kept separate from domain models:

```
UI (client) ──► Route (controller) ──► Service (business logic) ──► Repository / DataSource ──► Postgres
                     ▲                                                                              │
                     └──────────────────────── DTO ◄──── domain model ◄────────────────────────────┘
```

- **`routes`** — parse/validate request params, call the service, map the
  typed result to an HTTP response. No business logic.
- **`service`** — orchestration (e.g. "does this coin exist before looking
  up its markets?"), returns a sealed-interface result type the route can
  exhaustively `when`-match into the right response.
- **`repository` / `DataSource`** — persistence abstraction, one interface
  per feature with a real Exposed-backed implementation and an in-memory
  fake for tests.
- **`domain`** — plain models (`Coin`, `Market`) distinct from the
  `*Response` DTOs returned over the wire. `Candle` (from `:core`) is a
  deliberate exception — it's the actual cross-module wire contract shared
  with the client, not an internal detail worth splitting further.

### Package layout

```
server/src/main/kotlin/com/cryptodanilo/project/server/
├─ Application.kt          # entry point, plugin installation, DI-by-hand wiring
├─ common/                  # cross-feature: paging envelope, error body, param parsing
├─ domain/                  # Coin, Market — plain domain models
├─ db/
│  ├─ DatabaseFactory.kt    # HikariCP DataSource + Exposed connection
│  └─ Tables.kt             # `coins` table (cross-feature: coin-existence checks, FKs)
├─ cube/
│  └─ CubeClient.kt         # dormant — see "Cube" below
└─ feature/
   ├─ coins/                # GET /api/v1/coins
   ├─ markets/               # GET /api/v1/coins/{coinId}/markets
   └─ history/                # GET /api/v1/coins/{coinId}/history
```

## Data flow: Lambda architecture for candle history

Historical candles are **not** queried live from BigQuery/Cube on each
request — that was tried and proved too slow/fragile for a fixed six-range
chart. Instead, two Postgres tables are kept fresh by independent
Cloud Run Jobs (in the backend data-platform repo) and read directly:

- **`candle_rollups_hourly`** (speed layer) — 10-day retention, refreshed
  every 5 minutes from BigQuery's `gold.hourly_candle_metrics`. Serves the
  1D/5D ranges directly.
- **`candle_rollups_daily`** (batch layer) — 13-month retention, refreshed
  once daily from `gold.daily_candle_metrics`. Serves 1M/6M/YTD/1Y, with
  today's bucket excluded (that table only updates once a day) and
  **synthesized on the fly** from today's hourly rows instead — so the
  long ranges still feel fresh between daily-sync runs.

This merge happens in `PostgresHistoryDataSource`, the sole
`HistoryDataSource` implementation currently wired in.

## Endpoints

All responses are JSON; paginated list endpoints share one envelope shape:

```json
{ "data": [ /* items */ ], "page": { "limit": 50, "offset": 0, "total": 2, "hasMore": false } }
```

Errors share one shape:

```json
{ "error": { "code": "COIN_NOT_FOUND", "message": "No coin with id 'xyz'" } }
```

| Method | Path | Query params | Notes |
|---|---|---|---|
| `GET` | `/api/v1/coins` | `limit` (1-200, default 50), `offset` (default 0) | Paginated coin list, ordered by rank. Only includes coins with a Binance USDT pair (`coins.binance_symbol IS NOT NULL`) — the candle pipeline is entirely Binance-kline-sourced, so any other coin would show in the list with a permanently empty chart on selection; filtered out here rather than left for the client to special-case |
| `GET` | `/api/v1/coins/{coinId}/markets` | `limit`, `offset` | 404 if `coinId` doesn't exist |
| `GET` | `/api/v1/coins/{coinId}/history` | `range` — one of `1d`/`5d`/`1m`/`6m`/`ytd`/`1y` (default `1y`) | Returns `List<Candle>`; 400 on invalid range, 404 on unknown coin, 502 if the data source fails |

`range` values are exactly the CMP client's `ChartTimeframe.label`
lowercased — no translation needed on either side.

## CORS

`anyHost()` is enabled because the web (wasmJs) target runs in a real
browser and enforces CORS on cross-origin `fetch()` (Android/iOS/Desktop
don't go through a browser sandbox, so this was invisible until the web
target was actually run). No credentials/cookies are involved since there's
no auth yet — revisit `anyHost()` if that changes. `Authorization` is
explicitly allowed in the preflight because the client's shared
`HttpClient` attaches that header to every request (a leftover from the
CoinCap-era config); the server ignores it, but the browser still requires
it to be allowed or the whole preflight is rejected.

## Cube

`cube/CubeClient.kt` is a thin, generic client for Cube Core's semantic
layer (HS256 JWT signing + a raw `/cubejs-api/v1/load` call) — not
currently used anywhere in this module. It's kept intentionally
unopinionated about any particular cube/measures for a future
analytics/chatbot use case, which is a better fit for Cube's actual
strength (ad-hoc analytical querying) than this app's fixed six-range chart
ever was. The Cube deployment itself lives in the backend data-platform
repo and is untouched.

## Authentication

None — the client has no login flow yet. Add auth in `configureServer`
(`Application.kt`) once a real account system exists.

## Local development

```bash
cp .env.example .env   # then fill in DATABASE_URL
./gradlew :server:run
./gradlew :server:test
```

`DATABASE_URL` must point at Supabase's **transaction-mode pooler** (port
`6543`, not the direct-connection port) — `DatabaseFactory` sets
`prepareThreshold=0` on the JDBC connection to work around that pooler
routing different transactions to different backend Postgres processes,
which breaks server-side prepared statements otherwise.

## Deployment

Built and pushed from the Ubuntu box (no Cloud Build, to stay near zero
cost), then deployed to Cloud Run from anywhere with the already-pushed
image:

```bash
# on the Ubuntu box
./gradlew :server:buildFatJar
docker build -t <artifact-registry-repo>/cryptotracker-server:latest .
docker push <artifact-registry-repo>/cryptotracker-server:latest

# from anywhere
gcloud run deploy cryptotracker-server \
  --image=<artifact-registry-repo>/cryptotracker-server:latest \
  --min-instances=0 \
  --allow-unauthenticated
```

Two dedicated service accounts, least-privilege: a pusher
(`artifactregistry.writer`, scoped to this one Artifact Registry repo) and a
runtime identity (`secretmanager.secretAccessor`, scoped to the
`supabase-database-url` secret only).

## Related repo

The recurring sync jobs that keep `candle_rollups_hourly`/`_daily` (and the
`coins`/`coin_snapshots`/`markets` tables this server also reads) fresh live
in a separate backend data-platform repo, along with the Cube deployment
and BigQuery pipeline. This repo only reads from Postgres — schema
ownership and the ETL jobs stay there.
