---
name: new-compose-screen
description: Scaffold a new Compose Multiplatform screen in app:shared following this repo's established conventions — ViewModel with StateFlow/Channel, sealed Action dispatch, Koin DI registration, nav key registration, design tokens, string resources, and a preview. Use when the user asks to add a new screen/tab/feature UI to the CryptoTracker app.
when_to_use: add a new screen, add a new tab, scaffold a screen, create a new feature UI
---

Scaffold a new screen for `app:shared`, matching the shape already used by
`crypto/presentation/coinList/` (reference implementation — read
`CoinListViewModel.kt`, `CoinListState.kt`/equivalent, and the screen
composable there if unsure about exact shape before generating new code).

## Steps

1. **Ask for the screen name and its package** if not already clear from
   the request (e.g. `crypto/presentation/coinAlerts` for a hypothetical
   alerts screen) — don't guess a package that doesn't fit the existing
   `presentation/<feature>/` layout.

2. **State + Action + Event** (`<Name>State.kt`, `<Name>Contract.kt` or
   inline in the ViewModel file, matching whatever the nearest existing
   feature does):
   - `data class <Name>State(...)` with sensible defaults (empty
     list/`false`/`null`), not implicit nulls everywhere.
   - `sealed interface <Name>Action` covering every user interaction.
   - `sealed interface <Name>Event` only if the screen needs one-off
     effects (navigation, snackbar) — skip it if state alone covers
     everything.

3. **ViewModel** (`<Name>ViewModel.kt`):
   ```kotlin
   class <Name>ViewModel(
       private val someDataSource: SomeDataSource,
   ) : ViewModel() {
       private val _state = MutableStateFlow(<Name>State())
       val state: StateFlow<<Name>State> = _state

       private val _events = Channel<<Name>Event>()
       val events = _events.receiveAsFlow()

       init { /* initial load */ }

       fun onAction(action: <Name>Action) {
           when (action) { /* exhaustive */ }
       }
   }
   ```

4. **Screen composable** (`<Name>Screen.kt`):
   - Root `@Composable fun <Name>Screen(state: <Name>State, onAction: (<Name>Action) -> Unit)`
     — stateless, takes state + action callback, no ViewModel reference
     inside (matches the rest of the codebase's testability/preview
     pattern).
   - A separate `@Composable fun <Name>Root(viewModel: <Name>ViewModel = koinViewModel())`
     wrapper that collects `state`/`events` and forwards to the stateless
     screen, if that's the pattern the nearest existing feature uses —
     check before assuming.
   - Every color/spacing/sizing/type value goes through
     `CryptoTrackerTheme.{colors,spacing,sizing,typography}` — see
     `app/shared/CLAUDE.md`/`.claude/rules/design-tokens.md`. Never inline
     a `Color(0x...)` or bare `.dp`/`.sp`.
   - Every user-facing string goes in
     `composeResources/values/strings.xml`, referenced via
     `stringResource(Res.string.*)`.

5. **Preview**: add `@Preview` function(s) for at least the loaded and
   loading/empty states. If the screen (or a child composable it uses)
   takes an `AnimatedPaneScope` parameter, use the nullable-parameter
   workaround — see `memory/feedback_compose_preview_sealed_scope.md` —
   rather than inventing a new one.

6. **DI registration** — add to `di/Modules.kt`'s `sharedModule`:
   ```kotlin
   viewModelOf(::<Name>ViewModel)
   ```
   Register any new data source there too (`singleOf(...).bind<Interface>()`
   or `single<Interface> { Impl() }`, matching the mock/real switch pattern
   already used for `CoinDataSource` if this screen needs one).

7. **Navigation registration** (only if this is a new top-level
   destination, not a sub-state of an existing screen) — in
   `core/navigation/NavKeys.kt`:
   - Add a `data object`/`data class` to the sealed `AppNavKey` interface.
   - Register it in **both** `polymorphic(NavKey::class)` and
     `polymorphic(AppNavKey::class)` blocks inside `appNavSavedStateConfig`
     — missing either breaks state restoration silently, not a compile
     error, so double-check both got added.
   - Wire the actual route → composable mapping in whichever
     `*NavDisplay.kt` file matches this feature's tab (`CryptoNavDisplay.kt`
     for the crypto tab).

8. **Verify**: run the desktop target (`./gradlew :app:desktopApp:run`) and
   actually exercise the new screen — golden path plus at least one error/
   empty state — before calling this done. Type-checking is not feature
   correctness.

After scaffolding, consider invoking the `compose-ui-reviewer` subagent on
the new files to catch any token/string-resource/preview convention misses
before you report the work as finished.
