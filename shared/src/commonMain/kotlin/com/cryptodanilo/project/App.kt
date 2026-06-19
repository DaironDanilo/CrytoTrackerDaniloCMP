package com.cryptodanilo.project

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.cryptodanilo.project.core.navigation.AdaptiveCoinListDetailPane
import com.cryptodanilo.project.core.presentation.components.ComposeMultiplatformWatermark
import com.cryptodanilo.project.ui.theme.CryptoTrackerThemeProvider

@Composable
@Preview
fun App() {
    CryptoTrackerThemeProvider {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                AdaptiveCoinListDetailPane(modifier = Modifier.padding(innerPadding))
            }

            ComposeMultiplatformWatermark()
        }
    }
}
