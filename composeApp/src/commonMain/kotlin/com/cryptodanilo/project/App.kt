package com.cryptodanilo.project

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cryptodanilo.project.core.navigation.AdaptiveCoinListDetailPane
import com.cryptodanilo.project.ui.theme.CryptoTrackerTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    CryptoTrackerTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            AdaptiveCoinListDetailPane(modifier = Modifier.padding(innerPadding))
        }
    }
}