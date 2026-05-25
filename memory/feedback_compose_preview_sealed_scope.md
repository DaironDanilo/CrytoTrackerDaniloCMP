---
name: compose-preview-sealed-scope
description: How to preview composables that use sealed AnimatedPaneScope from M3 Adaptive in a CMP project
metadata:
  type: feedback
---

`AnimatedPaneScope` (from `androidx.compose.material3.adaptive.layout`) is a **sealed interface**. You cannot implement it in a different module to create a fake scope for previews.

**Solution**: Make `animatedPaneScope: AnimatedPaneScope? = null` (nullable with default null) and guard the `sharedElement` modifier with a null check:

```kotlin
.conditional(
    condition = shouldExistSharedElementTransition && animatedPaneScope != null,
    ifTrue = {
        sharedElement(
            animatedVisibilityScope = animatedPaneScope!!,
            ...
        )
    }
)
```

Preview functions then wrap in `SharedTransitionLayout { }` (still needed because the function is a `SharedTransitionScope` extension) and omit `animatedPaneScope` (defaults to null):

```kotlin
SharedTransitionLayout {
    CoinListItem(
        coin = previewCoin,
        shouldExistSharedElementTransition = false,
        onItemClick = {},
    )
}
```

**Why:** `AnimatedPaneScope` is sealed — can't be faked outside the library. `!! ` is safe because the condition guards it.

**How to apply:** Any time a composable extension on `SharedTransitionScope` needs `AnimatedPaneScope` as a parameter and also needs to be previewable, use nullable + default null.