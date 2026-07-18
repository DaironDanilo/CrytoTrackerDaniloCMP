# `app:shared`

Compose Multiplatform UI, ViewModels, and client-side data/domain logic —
depended on by all four client app shells (`androidApp`/`iosApp`/
`desktopApp`/`webApp`) and depends on `:core` for shared wire types like
`Candle`. See the module graph in the root `CLAUDE.md` for blast radius: a
change here can affect any/all four client targets, never `:server`.

## Design tokens — the rule that matters most here

Everything visual goes through `CryptoTrackerTheme`
(`src/commonMain/kotlin/com/cryptodanilo/project/ui/theme/Theme.kt`):

```kotlin
CryptoTrackerTheme.colors      // ColorScheme — MaterialTheme.colorScheme under the hood
CryptoTrackerTheme.spacing     // Spacing — extraSmall(4dp)/small(8dp)/medium(16dp)/large(24dp)/extraLarge(32dp)/huge(48dp)
CryptoTrackerTheme.sizing      // Sizing — see Sizing.kt for the current scale
CryptoTrackerTheme.typography  // Typography — MaterialTheme.typography under the hood
```

- **Never** write `Color(0x......)` inline in a composable. Add the color to
  `Color.kt`'s light/dark scheme instead, or reuse an existing
  `CryptoTrackerTheme.colors.*` role.
- **Never** write a bare `.dp`/`.sp` literal for spacing/sizing/text — use
  the scale above. If nothing fits, add a new named step to `Spacing.kt`/
  `Sizing.kt` rather than inlining a one-off value.
- Known, pre-existing violation — **do not copy this pattern**:
  `presentation/coinDetail/LineChart.kt:438-440` has three hardcoded
  `Color(0x...)` literals predating this convention being written down. Fix
  opportunistically if you're already touching that file; don't go out of
  your way to fix it otherwise.
- User-facing text goes in `composeResources/values/strings.xml`, referenced
  via the generated `Res.string.*` accessor — not inline string literals in
  a composable.

## Architecture pattern (follow this shape for any new screen)

- **State**: `data class XState(...)` + `ViewModel` holding
  `MutableStateFlow<XState>` (exposed as `StateFlow`) plus a
  `Channel<XEvent>`/`receiveAsFlow()` for one-off events (navigation,
  snackbars). `init { }` kicks off the initial load.
- **Actions**: a sealed `XAction` type, dispatched through a single
  `fun onAction(action: XAction)` — don't expose individual public methods
  per user interaction from the composable side.
- **DI**: register the ViewModel in `di/Modules.kt`'s `sharedModule` via
  Koin's `viewModelOf(::XViewModel)`; register data sources via `singleOf`/
  `single<Interface> { Impl() }.bind<Interface>()`.
- **Navigation**: add a `data object`/`data class` to the sealed
  `AppNavKey` interface in `core/navigation/NavKeys.kt`, and register it in
  **both** `polymorphic(NavKey::class)` and `polymorphic(AppNavKey::class)`
  blocks in `appNavSavedStateConfig` — missing either one breaks state
  restoration silently rather than failing to compile.
- **Previews**: `AnimatedPaneScope` (M3 Adaptive) is a sealed interface and
  cannot be faked for `@Preview` — see
  `memory/feedback_compose_preview_sealed_scope.md` for the established
  workaround (nullable `animatedPaneScope: AnimatedPaneScope? = null`
  parameter, guarded with a null check) before inventing a new one.

## Mock vs. real data

`BuildKonfig.USE_MOCK_DATA` switches `di/Modules.kt` between
`MockCoinDataSource` and `ServerCoinDataSource` (the real `:server`-backed
implementation) — check which one is active before concluding a bug is in
the network layer.
