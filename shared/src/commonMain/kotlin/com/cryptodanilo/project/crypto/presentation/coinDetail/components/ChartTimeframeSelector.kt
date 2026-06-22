package com.cryptodanilo.project.crypto.presentation.coinDetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.cryptodanilo.project.crypto.presentation.coinDetail.ChartTimeframe
import com.cryptodanilo.project.ui.theme.CryptoTrackerTheme
import com.cryptodanilo.project.ui.theme.CryptoTrackerThemeProvider

@Composable
fun ChartTimeframeSelector(
    selectedTimeframe: ChartTimeframe,
    onTimeframeSelected: (ChartTimeframe) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(CryptoTrackerTheme.sizing.cornerMedium)
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .border(
                    width = CryptoTrackerTheme.sizing.borderThin,
                    color = CryptoTrackerTheme.colors.outlineVariant,
                    shape = shape,
                ).padding(
                    horizontal = CryptoTrackerTheme.spacing.medium,
                    vertical = CryptoTrackerTheme.spacing.small,
                ).horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ChartTimeframe.entries.forEachIndexed { index, timeframe ->
            if (index > 0) {
                Text(
                    text = "|",
                    color = CryptoTrackerTheme.colors.outlineVariant,
                    style = CryptoTrackerTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = CryptoTrackerTheme.spacing.extraSmall),
                )
            }

            val isSelected = timeframe == selectedTimeframe

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier =
                    Modifier
                        .clickable { onTimeframeSelected(timeframe) }
                        .padding(
                            horizontal = CryptoTrackerTheme.spacing.extraSmall,
                            vertical = CryptoTrackerTheme.spacing.extraSmall,
                        ),
            ) {
                Text(
                    text = timeframe.label,
                    style = CryptoTrackerTheme.typography.bodySmall,
                    color = if (isSelected) CryptoTrackerTheme.colors.primary else CryptoTrackerTheme.colors.onSurfaceVariant,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                )
                Spacer(modifier = Modifier.height(CryptoTrackerTheme.sizing.borderMedium))
                // Always reserved (transparent when unselected) so row height stays
                // constant across selection changes instead of jittering.
                Box(
                    modifier =
                        Modifier
                            .width(CryptoTrackerTheme.spacing.medium)
                            .height(CryptoTrackerTheme.sizing.borderMedium)
                            .background(if (isSelected) CryptoTrackerTheme.colors.primary else Color.Transparent),
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C1B1FL)
@Composable
private fun ChartTimeframeSelectorPreview() {
    CryptoTrackerThemeProvider(darkTheme = true) {
        val selected = remember { ChartTimeframe.ALL }
        ChartTimeframeSelector(
            selectedTimeframe = selected,
            onTimeframeSelected = {},
        )
    }
}
