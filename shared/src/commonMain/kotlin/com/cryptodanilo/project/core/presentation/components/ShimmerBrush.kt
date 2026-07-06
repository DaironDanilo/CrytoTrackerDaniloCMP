package com.cryptodanilo.project.core.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalInspectionMode
import com.cryptodanilo.project.ui.theme.CryptoTrackerTheme

private const val SHIMMER_TRANSLATE_DISTANCE = 1000f
private const val SHIMMER_GRADIENT_WIDTH = 300f
private const val SHIMMER_DURATION_MS = 900
private const val SHIMMER_PEAK_ALPHA = 0.12f

// Shared by the stat boxes and chart overlays on the coin detail screen so a refresh
// plays one consistent sweep animation across both, instead of two separate effects.
@Composable
fun shimmerBrush(isShimmering: Boolean): Brush {
    if (!isShimmering || LocalInspectionMode.current) return SolidColor(Color.Transparent)

    val shimmerColor = CryptoTrackerTheme.colors.onSurface
    val shimmerColors =
        listOf(
            shimmerColor.copy(alpha = 0f),
            shimmerColor.copy(alpha = SHIMMER_PEAK_ALPHA),
            shimmerColor.copy(alpha = 0f),
        )
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = SHIMMER_TRANSLATE_DISTANCE,
        animationSpec =
            infiniteRepeatable(
                animation = tween(SHIMMER_DURATION_MS, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
        label = "shimmer_translate",
    )
    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - SHIMMER_GRADIENT_WIDTH, translateAnim - SHIMMER_GRADIENT_WIDTH),
        end = Offset(translateAnim, translateAnim),
    )
}

// Pairs [shimmerBrush] with a fade in/out so callers overlaying a refresh indicator on top of
// real, already-rendered content just supply the refreshing flag. Declared as a BoxScope
// receiver (rather than nested inline at each call site) so matchParentSize() and the plain,
// unscoped AnimatedVisibility overload always resolve unambiguously, regardless of what other
// scopes (Column, Row, ...) happen to be active further out at the call site.
@Composable
fun BoxScope.ShimmerOverlay(
    isShimmering: Boolean,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = isShimmering,
        enter = fadeIn(tween(200)),
        exit = fadeOut(tween(400)),
        modifier = modifier.matchParentSize(),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .clip(RectangleShape)
                    .background(shimmerBrush(isShimmering = isShimmering)),
        )
    }
}
