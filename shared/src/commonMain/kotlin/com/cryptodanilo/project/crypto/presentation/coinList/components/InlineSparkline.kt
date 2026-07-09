package com.cryptodanilo.project.crypto.presentation.coinList.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.cryptodanilo.project.ui.theme.CryptoTrackerTheme
import cryptotrackerdanilo.shared.generated.resources.Res
import cryptotrackerdanilo.shared.generated.resources.trending
import cryptotrackerdanilo.shared.generated.resources.trending_down
import org.jetbrains.compose.resources.painterResource

@Composable
fun InlineSparkline(
    trendPoints: List<Double>,
    changePercent: Double,
    modifier: Modifier = Modifier,
) {
    val iconTint =
        when {
            changePercent > 0 -> CryptoTrackerTheme.colors.primary // positive
            changePercent < 0 -> CryptoTrackerTheme.colors.error // negative
            else -> CryptoTrackerTheme.colors.onSurfaceVariant
        }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (changePercent != 0.0) {
            // Same up/down trending-zigzag icon pair the detail screen's Change stat box
            // uses (CoinDetailScreen.kt) — swaps between two drawables per direction.
            Icon(
                painter =
                    painterResource(
                        if (changePercent > 0) Res.drawable.trending else Res.drawable.trending_down,
                    ),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(CryptoTrackerTheme.sizing.iconMedium),
            )
        } else {
            // No direction to show for an unchanged price — a neutral placeholder
            // instead of leaving the column visually empty.
            Text(
                text = "--",
                color = iconTint,
                style = CryptoTrackerTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
        }
    }
}
