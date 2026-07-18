# CryptoTracker — Kotlin Multiplatform

KMP app (Android/iOS/Desktop/Web, Compose Multiplatform) backed by a Ktor
server. Full architecture/setup/run instructions: `README.md`. Backend
detail: `server/README.md`. This file is agent-operating rules and the
module map — it does not duplicate what those two already document.

Nested `CLAUDE.md` files exist per module (`core/`, `server/`,
`app/shared/`) and load automatically the moment you read a file in that
directory — you don't need to open them manually, but know they exist so
you're not surprised by extra context appearing.

## Module graph

```
:core (leaf, no deps)
 ├─ app:shared (implementation(projects.core))
 │   ├─ app:androidApp   ─┐
 │   ├─ app:desktopApp    ├─ implementation(projects.app.shared)
 │   ├─ app:webApp       ─┘
 │   └─ app:iosApp  (Xcode project, not a Gradle module — links app:shared's
 │                    compiled KMP framework via Xcode's build phase)
 └─ server (implementation(projects.core), NOT app:shared)
```

Implication when changing something: a change to `:core` can affect `:server`
*and* every client. A change to `app:shared` can affect all four client
targets but never `:server`. A change to one client app module
(`app:androidApp` etc.) affects only that target.

## Design tokens — never hardcode colors, spacing, or type

Full system documented in `app/shared/CLAUDE.md` and enforced by
`.claude/rules/design-tokens.md`. One-line version: use
`CryptoTrackerTheme.{colors,spacing,sizing,typography}` from
`app/shared/src/commonMain/kotlin/com/cryptodanilo/project/ui/theme/Theme.kt`
for every color/dp/sp/font in Compose code — never `Color(0x...)` or a bare
`.dp`/`.sp` literal. (Known pre-existing violation to *not* copy as a
pattern: `LineChart.kt:438-440`.) User-facing text goes through
`composeResources/values/strings.xml`, not inline string literals.

## Git / commit rules (standing — do not ask, just follow)

- **Never** add Claude/Claude Code as a co-author or contributor in any
  commit.
- **Never** push unless explicitly asked in that turn — committing is not
  the same as authorization to push.
- **Never** commit `BACKEND_ARCHITECTURE_PLAN.md` (already gitignored —
  don't remove that line).
- Prefer many small, focused commits over one large one when doing
  multi-step work, unless told otherwise.

## Deployment — no Cloud Build, ever

`:server` images are built with plain `docker build`/`docker push` (locally
or on a GitHub Actions runner), never `gcloud run deploy --source=` or
`gcloud builds submit`. This is a deliberate cost-control choice, not an
oversight — don't "helpfully" switch to Cloud Build.

## CI/CD (path-scoped, GitHub Actions)

Two independent trigger groups, both also triggered by root
build-infrastructure files (`gradle/libs.versions.toml`, root
`build.gradle.kts`, `settings.gradle.kts`, `gradle.properties`, the Gradle
wrapper) since a path filter can't tell which side a catalog/plugin bump
affects:

- **Backend**: `deploy-server.yml` (test → build distroless image → deploy
  to Cloud Run) + `test-server.yml` (tests only, also on PR), both scoped to
  `server/**` + `core/**`.
- **Clients**: `build.yml` (all four targets build together as one unit —
  deliberately not further split, so a regression on any platform still
  fails CI even if that platform wasn't touched directly) + `test.yml`,
  both scoped to `app/**` + `core/**`.

Editing a workflow file itself always triggers that workflow (each filter
includes its own `.github/workflows/*.yml` path).

`server/Dockerfile` is multi-stage/distroless
(`gcr.io/distroless/java17-debian12:nonroot`) and its build context is the
**repo root**, not `server/` — it needs `core/` and the root Gradle files to
configure the multi-module build.

## Testing

- `./gradlew :server:test` — backend, Ktor `testApplication` + fake
  repositories (no live DB needed).
- `./gradlew :app:shared:desktopTest` — shared/client logic (what `test.yml`
  runs; desktop target used as the fast JVM test runner for common code).
- `ktlint` runs repo-wide via the root `subprojects {}` block — don't
  hand-format, run `./gradlew ktlintFormat` if unsure.

## Project-shared memory

`memory/MEMORY.md` is a **git-committed, repo-shared** knowledge base of
non-obvious gotchas — distinct from your own private auto-memory, which
only you (this session's model) will ever see again. Check it for known
traps before debugging something that feels like it's been hit before, and
add an entry (same frontmatter format as the existing one) when you learn
something the hard way that a future agent — any agent, any session — would
otherwise re-discover from scratch.

## Verification standard

For UI changes: actually run the affected target (desktop is fastest to
iterate on) and exercise the golden path before calling it done — type
checking is not feature correctness. For backend changes: prefer testing
against real infra when practical (this project has a real Supabase +
Cube.dev + Cloud Run backend, not just mocks) — several past bugs here only
showed up against real data.
