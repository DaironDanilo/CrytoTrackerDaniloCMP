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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
@Preview
fun App(
    onBackNavigableChanged: (canNavigateBack: Boolean) -> Unit = {},
    backRequests: Flow<Unit> = emptyFlow(),
) {
    CryptoTrackerThemeProvider {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                AdaptiveCoinListDetailPane(
                    modifier = Modifier.padding(innerPadding),
                    onBackNavigableChanged = onBackNavigableChanged,
                    backRequests = backRequests,
                )
            }

            ComposeMultiplatformWatermark()
        }
    }
}
