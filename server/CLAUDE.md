# `:server`

Ktor (Netty) backend. Full architecture, endpoints, deployment, and data
flow: `server/README.md` — read that first for anything beyond the
gotchas below, which is what actually earns a spot in a preloaded file.

## Layered architecture — keep logic in the right layer

`routes` (thin, parse/validate + call service) → `service` (orchestration,
returns a sealed result type) → `repository`/`DataSource` (persistence,
interface + real Exposed impl + in-memory fake for tests) → `domain` (plain
models, distinct from wire-format `*Response` DTOs). Don't put query logic
in a route, don't put HTTP concerns in a repository.

## Exposed gotcha (has caused a real compile error before)

`SqlExpressionBuilder` extensions like `.isNotNull()` require receiver scope
— they only resolve **inside** a `.where { }` lambda, not as a standalone
precomputed `val`:

```kotlin
// Fails: Unresolved reference 'isNotNull'
val hasChartData = Coins.binanceSymbol.isNotNull()

// Works: inline it in the .where { } lambda
.where { Coins.binanceSymbol.isNotNull() }
```

## Testing pattern

Every repository interface gets an in-memory fake (`server/src/test/kotlin/
.../FakeRepositories.kt`) so route tests run via Ktor's `testApplication`
DSL with zero live DB/network dependency. Follow this pattern for any new
repository rather than mocking.

## Local dev

```bash
cp .env.example .env   # fill in DATABASE_URL — must be Supabase's
                        # transaction-mode pooler (port 6543), not the
                        # direct-connection port
./gradlew :server:run
./gradlew :server:test
```

## Deployment

CI-driven (`deploy-server.yml`) on push to `main`, path-scoped to
`server/**`/`core/**` — see the CI/CD section in the root `CLAUDE.md`. Never
use `gcloud run deploy --source=`/Cloud Build; the Dockerfile is
multi-stage/distroless and its build context is the **repo root**, not this
directory.
