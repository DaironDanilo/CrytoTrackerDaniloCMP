package com.cryptodanilo.project.crypto.presentation.coinList.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
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
        if (change.value < 0.0) {
            CryptoTrackerTheme.colors.error
        } else {
            CryptoTrackerTheme.colors.primary
        }
    val backgroundColor =
        if (change.value < 0.0) {
            CryptoTrackerTheme.colors.error.copy(alpha = 0.1f)
        } else {
            CryptoTrackerTheme.colors.primary.copy(alpha = 0.1f)
        }
    Row(
        modifier =
            modifier
                .clip(RoundedCornerShape(100))
                .background(backgroundColor)
                .padding(
                    horizontal = CryptoTrackerTheme.spacing.small,
                    vertical = CryptoTrackerTheme.spacing.extraSmall,
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(CryptoTrackerTheme.spacing.extraSmall),
    ) {
        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = null,
            modifier =
                Modifier
                    .size(CryptoTrackerTheme.sizing.priceChangeIconSize)
                    .rotate(if (change.value < 0.0) 0f else 180f),
            tint = contentColor,
        )
        Text(
            text = "${change.formatted}%",
            color = contentColor,
            fontWeight = FontWeight.Bold,
            style = CryptoTrackerTheme.typography.bodySmall,
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
