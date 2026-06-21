package com.cryptodanilo.project.crypto.presentation.coinList.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.cryptodanilo.project.core.presentation.util.DisplayableNumber
import com.cryptodanilo.project.ui.theme.CryptoTrackerTheme
import com.cryptodanilo.project.ui.theme.CryptoTrackerThemeProvider
import com.cryptodanilo.project.ui.theme.greenBackground

@Composable
fun PriceChange(
    change: DisplayableNumber,
    modifier: Modifier = Modifier,
) {
    val contentColor =
        if (change.value < 0.0) {
            CryptoTrackerTheme.colors.onErrorContainer
        } else {
            Color.Green
        }
    val backgroundColor =
        if (change.value < 0.0) {
            CryptoTrackerTheme.colors.errorContainer
        } else {
            greenBackground
        }
    Row(
        modifier =
            modifier
                .clip(RoundedCornerShape(100f))
                .background(backgroundColor)
                .padding(horizontal = CryptoTrackerTheme.spacing.extraSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector =
                if (change.value < 0.0) {
                    Icons.Default.KeyboardArrowDown
                } else {
                    Icons.Default.KeyboardArrowUp
                },
            contentDescription = null,
            modifier = Modifier.size(CryptoTrackerTheme.sizing.priceChangeIconSize),
            tint = contentColor,
        )
        Text(
            text = "${change.formatted}%",
            color = contentColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
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
