package com.cryptodanilo.project.crypto.presentation.coinDetail.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.cryptodanilo.project.ui.theme.CryptoTrackerTheme
import com.cryptodanilo.project.ui.theme.CryptoTrackerThemeProvider

@Composable
fun MarketsColumnHeader(modifier: Modifier = Modifier) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(
                    horizontal = CryptoTrackerTheme.spacing.medium,
                    vertical = CryptoTrackerTheme.spacing.small,
                ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "#",
            style = CryptoTrackerTheme.typography.bodySmall,
            color = CryptoTrackerTheme.colors.onSurfaceVariant,
            modifier = Modifier.width(CryptoTrackerTheme.sizing.rankColumnWidth),
            textAlign = TextAlign.End,
        )
        Spacer(modifier = Modifier.width(CryptoTrackerTheme.sizing.rankExchangeSpacing))
        Text(
            text = "EXCHANGE",
            style = CryptoTrackerTheme.typography.bodySmall,
            color = CryptoTrackerTheme.colors.onSurfaceVariant,
            modifier = Modifier.weight(2f),
        )
        Text(
            text = "PAIR",
            style = CryptoTrackerTheme.typography.bodySmall,
            color = CryptoTrackerTheme.colors.onSurfaceVariant,
            modifier = Modifier.weight(1.5f),
        )
        Text(
            text = "PRICE",
            style = CryptoTrackerTheme.typography.bodySmall,
            color = CryptoTrackerTheme.colors.onSurfaceVariant,
            modifier = Modifier.weight(2f),
            textAlign = TextAlign.End,
        )
        Text(
            text = "24H VOL",
            style = CryptoTrackerTheme.typography.bodySmall,
            color = CryptoTrackerTheme.colors.onSurfaceVariant,
            modifier = Modifier.weight(2f),
            textAlign = TextAlign.End,
        )
        Text(
            text = "TRADES",
            style = CryptoTrackerTheme.typography.bodySmall,
            color = CryptoTrackerTheme.colors.onSurfaceVariant,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
        )
        Text(
            text = "% VOL",
            style = CryptoTrackerTheme.typography.bodySmall,
            color = CryptoTrackerTheme.colors.onSurfaceVariant,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
        )
        Text(
            text = "UPDATED",
            style = CryptoTrackerTheme.typography.bodySmall,
            color = CryptoTrackerTheme.colors.onSurfaceVariant,
            modifier = Modifier.weight(1.5f),
            textAlign = TextAlign.End,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C1B1FL)
@Composable
private fun MarketsColumnHeaderPreview() {
    CryptoTrackerThemeProvider(darkTheme = true) {
        MarketsColumnHeader()
    }
}