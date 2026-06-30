package com.cryptodanilo.project.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.cryptodanilo.project.BuildKonfig
import com.cryptodanilo.project.ui.theme.CryptoTrackerTheme

@Composable
fun BoxScope.MockDataBanner() {
    if (!BuildKonfig.USE_MOCK_DATA) return

    Row(
        modifier =
            Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .background(CryptoTrackerTheme.colors.error.copy(alpha = 0.9F))
                .padding(vertical = CryptoTrackerTheme.spacing.extraSmall),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "MOCK DATA — prices are not real",
            color = CryptoTrackerTheme.colors.onError,
            style = CryptoTrackerTheme.typography.labelSmall,
        )
    }
}
