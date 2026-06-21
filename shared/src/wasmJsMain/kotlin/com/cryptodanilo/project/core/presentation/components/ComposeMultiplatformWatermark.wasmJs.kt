package com.cryptodanilo.project.core.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.fromKeyword
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.cryptodanilo.project.ui.theme.CryptoTrackerTheme
import cryptotrackerdanilo.shared.generated.resources.Res
import cryptotrackerdanilo.shared.generated.resources.built_with
import cryptotrackerdanilo.shared.generated.resources.compose_multiplatform
import cryptotrackerdanilo.shared.generated.resources.compose_multiplatform_name
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun ComposeMultiplatformWatermark() {
    val uriHandler = LocalUriHandler.current

    // Position is an offset from the badge's default BottomEnd alignment, so it
    // intentionally resets to that corner on page refresh instead of being persisted.
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var badgeSize by remember { mutableStateOf(IntSize.Zero) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val maxOffsetX = (constraints.maxWidth - badgeSize.width).coerceAtLeast(0).toFloat()
        val maxOffsetY = (constraints.maxHeight - badgeSize.height).coerceAtLeast(0).toFloat()

        Row(
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                    .onSizeChanged { badgeSize = it }
                    .pointerHoverIcon(
                        if (isDragging) {
                            PointerIcon.fromKeyword("grabbing")
                        } else {
                            PointerIcon.fromKeyword("grab")
                        },
                    ).pointerInput(Unit) {
                        // detectDragGestures only calls onDragStart/onDragEnd once touch slop is
                        // exceeded, so a zero-movement tap never reaches them. awaitFirstDown +
                        // drag() fire for every gesture (including taps), letting us measure total
                        // movement ourselves and decide tap vs. drag at release time.
                        val dragThresholdPx = WATERMARK_DRAG_THRESHOLD.toPx()
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            var totalDragDistance = 0f
                            isDragging = false
                            drag(down.id) { change ->
                                val dragAmount = change.positionChange()
                                change.consume()
                                totalDragDistance += dragAmount.getDistance()
                                if (totalDragDistance > dragThresholdPx) {
                                    isDragging = true
                                }
                                offsetX = (offsetX + dragAmount.x).coerceIn(-maxOffsetX, 0f)
                                offsetY = (offsetY + dragAmount.y).coerceIn(-maxOffsetY, 0f)
                            }
                            if (!isDragging) {
                                uriHandler.openUri(COMPOSE_MULTIPLATFORM_URL)
                            }
                            isDragging = false
                        }
                    }.padding(CryptoTrackerTheme.spacing.medium)
                    .clip(RoundedCornerShape(CryptoTrackerTheme.sizing.cornerFull))
                    .background(CryptoTrackerTheme.colors.surfaceContainerHigh)
                    .border(
                        width = CryptoTrackerTheme.sizing.borderThin,
                        color = CryptoTrackerTheme.colors.outline,
                        shape = RoundedCornerShape(CryptoTrackerTheme.sizing.cornerFull),
                    ).padding(
                        horizontal = CryptoTrackerTheme.spacing.medium,
                        vertical = CryptoTrackerTheme.spacing.small,
                    ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(CryptoTrackerTheme.spacing.extraSmall),
        ) {
            Image(
                painter = painterResource(Res.drawable.compose_multiplatform),
                contentDescription = "Compose Multiplatform",
                modifier = Modifier.size(CryptoTrackerTheme.sizing.iconSmall),
                colorFilter = ColorFilter.tint(CryptoTrackerTheme.colors.primary),
            )

            Text(
                text = stringResource(Res.string.built_with),
                style = CryptoTrackerTheme.typography.bodySmall,
                color = CryptoTrackerTheme.colors.onSurfaceVariant,
            )

            Text(
                text = stringResource(Res.string.compose_multiplatform_name) + " " + EXTERNAL_LINK_ARROW,
                style =
                    CryptoTrackerTheme.typography.bodySmall.copy(
                        textDecoration = TextDecoration.Underline,
                    ),
                color = CryptoTrackerTheme.colors.primary,
            )
        }
    }
}
