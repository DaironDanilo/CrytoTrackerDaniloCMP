# `:core`

The one module both `:server` and `app:shared` depend on. Keep it that way
on purpose: anything added here is implicitly a contract between the JVM
backend and every Compose Multiplatform client, so scope creep here has the
widest blast radius in the repo (see the module graph in the root
`CLAUDE.md`).

## What belongs here

Plain, `@Serializable` wire-format types shared verbatim between server and
client — e.g. `Candle`. Epoch-millis `Long` for timestamps and `Double` for
prices, matching the rest of the domain models' convention (a chart doesn't
need decimal-exact precision, so don't introduce `BigDecimal`/
`kotlinx.datetime.Instant` here just because the backend uses stricter types
internally).

## What does not belong here

- Business logic, formatting, or anything UI-specific — that's
  `app:shared`'s job.
- Anything Android/Compose/Ktor-specific — this module must stay buildable
  by both a plain JVM server and every KMP client target.
- DTOs that are only ever used on one side of the wire (e.g. a
  server-internal response shape, or a client-only view model) — those stay
  local to whichever module actually uses them, matching the existing
  pattern where `:server`'s `CoinResponse`/`Coin` domain models are NOT
  shared here even though the client also has its own `Coin`/`CoinUi`.

If you're about to add something here and can't articulate why *both* sides
genuinely need the exact same type, it probably belongs somewhere else.
