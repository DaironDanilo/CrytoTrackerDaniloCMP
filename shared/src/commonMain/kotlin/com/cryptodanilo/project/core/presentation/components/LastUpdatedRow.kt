package com.cryptodanilo.project.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.cryptodanilo.project.core.util.MILLIS_PER_HOUR
import com.cryptodanilo.project.core.util.MILLIS_PER_MINUTE
import com.cryptodanilo.project.core.util.getCurrentTimeMs
import com.cryptodanilo.project.ui.theme.CryptoTrackerTheme
import cryptotrackerdanilo.shared.generated.resources.Res
import cryptotrackerdanilo.shared.generated.resources.updated_hours_ago
import cryptotrackerdanilo.shared.generated.resources.updated_just_now
import cryptotrackerdanilo.shared.generated.resources.updated_minutes_ago
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration.Companion.milliseconds

internal const val MIN_TICKER_DELAY_MS = 100L
internal const val FRESH_THRESHOLD_MS = 5 * MILLIS_PER_MINUTE
internal const val STALE_THRESHOLD_MS = 10 * MILLIS_PER_MINUTE

@Composable
fun LastUpdatedRow(
    updatedAt: Long,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var tick by remember { mutableStateOf(0) }
    LaunchedEffect(updatedAt) {
        while (true) {
            val diffMs = getCurrentTimeMs() - updatedAt
            val delayMs =
                when {
                    diffMs < MILLIS_PER_MINUTE -> MILLIS_PER_MINUTE - diffMs
                    diffMs < MILLIS_PER_HOUR -> MILLIS_PER_MINUTE - (diffMs % MILLIS_PER_MINUTE)
                    else -> MILLIS_PER_HOUR - (diffMs % MILLIS_PER_HOUR)
                }
            delay(delayMs.coerceAtLeast(MIN_TICKER_DELAY_MS).milliseconds)
            tick++
        }
    }

    val diffMs = remember(tick, updatedAt) { getCurrentTimeMs() - updatedAt }
    val labelText =
        when {
            diffMs < MILLIS_PER_MINUTE -> stringResource(Res.string.updated_just_now)
            diffMs < MILLIS_PER_HOUR -> stringResource(Res.string.updated_minutes_ago, diffMs / MILLIS_PER_MINUTE)
            else -> stringResource(Res.string.updated_hours_ago, diffMs / MILLIS_PER_HOUR)
        }

    val primary = CryptoTrackerTheme.colors.primary
    val tertiary = CryptoTrackerTheme.colors.tertiary
    val error = CryptoTrackerTheme.colors.error
    val dotColor =
        when {
            diffMs < FRESH_THRESHOLD_MS -> primary
            diffMs < STALE_THRESHOLD_MS -> tertiary
            else -> error
        }

    val pillShape = RoundedCornerShape(CryptoTrackerTheme.sizing.cornerFull)
    val buttonShape = RoundedCornerShape(CryptoTrackerTheme.sizing.cornerMedium)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier =
                Modifier
                    .background(color = CryptoTrackerTheme.colors.surfaceContainerHigh, shape = pillShape)
                    .border(
                        width = CryptoTrackerTheme.sizing.borderThin,
                        color = CryptoTrackerTheme.colors.outlineVariant,
                        shape = pillShape,
                    ).padding(
                        horizontal = CryptoTrackerTheme.spacing.small,
                        vertical = CryptoTrackerTheme.spacing.extraSmall,
                    ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(CryptoTrackerTheme.spacing.extraSmall),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(CryptoTrackerTheme.sizing.statusDotSize)
                        .background(color = dotColor, shape = CircleShape),
            )
            Text(
                text = labelText,
                style = CryptoTrackerTheme.typography.bodySmall,
                color = CryptoTrackerTheme.colors.onSurface,
            )
        }

        Spacer(Modifier.width(CryptoTrackerTheme.spacing.small))

        Box(
            modifier =
                Modifier
                    .size(CryptoTrackerTheme.sizing.iconLarge)
                    .background(color = CryptoTrackerTheme.colors.surfaceContainerHighest, shape = buttonShape)
                    .border(
                        width = CryptoTrackerTheme.sizing.borderThin,
                        color = primary.copy(alpha = 0.35f),
                        shape = buttonShape,
                    ).clickable(enabled = !isLoading, onClick = onRefresh),
            contentAlignment = Alignment.Center,
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(CryptoTrackerTheme.sizing.iconSmall),
                    color = primary,
                    strokeWidth = CryptoTrackerTheme.sizing.borderThin,
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = primary,
                    modifier = Modifier.size(CryptoTrackerTheme.sizing.iconMedium),
                )
            }
        }
    }
}
