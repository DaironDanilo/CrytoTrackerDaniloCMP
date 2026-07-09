package com.cryptodanilo.project.crypto.presentation.coinList.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.cryptodanilo.project.core.presentation.util.DisplayableNumber
import com.cryptodanilo.project.ui.theme.CryptoTrackerTheme
import com.cryptodanilo.project.ui.theme.CryptoTrackerThemeProvider

@Composable
fun PriceChange(
    change: DisplayableNumber,
    modifier: Modifier = Modifier,
) {
    val contentColor =
        when {
            change.value > 0.0 -> CryptoTrackerTheme.colors.primary
            change.value < 0.0 -> CryptoTrackerTheme.colors.error
            else -> CryptoTrackerTheme.colors.onSurfaceVariant
        }
    val backgroundColor = contentColor.copy(alpha = 0.15f)

    val prefix = if (change.value > 0.0) "+" else ""

    Row(
        modifier =
            modifier
                .wrapContentWidth()
                .clip(CircleShape)
                .background(backgroundColor)
                .padding(
                    horizontal = CryptoTrackerTheme.spacing.small,
                    vertical = CryptoTrackerTheme.spacing.extraSmall,
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "$prefix${change.formatted}%",
            color = contentColor,
            style = CryptoTrackerTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            softWrap = false,
        )
    }
}

@Preview(showBackground = true, name = "Light - Positive")
@Composable
private fun PriceChangePositivePreview() {
    CryptoTrackerThemeProvider(darkTheme = false) {
        PriceChange(change = DisplayableNumber(0.54, "0.54"))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C1B1FL, name = "Dark - Negative")
@Composable
private fun PriceChangeNegativePreview() {
    CryptoTrackerThemeProvider(darkTheme = true) {
        PriceChange(change = DisplayableNumber(-2.10, "-2.10"))
    }
}
