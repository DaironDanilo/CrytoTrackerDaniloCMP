# Memory Index

- [Compose Preview with Sealed AnimatedPaneScope](feedback_compose_preview_sealed_scope.md) — Make `animatedPaneScope` nullable to enable previews; `AnimatedPaneScope` is sealed and can't be faked
- [wasmJs yarn.lock Dependabot alerts](project_wasmjs_yarn_dependabot.md) — Stale root `kotlin-js-store/yarn.lock` vs. live `wasm/` one; use `resolution("**/ws", ...)` (glob required) to force patched transitive npm deps