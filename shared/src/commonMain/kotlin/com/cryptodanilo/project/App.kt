package com.cryptodanilo.project

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.cryptodanilo.project.core.navigation.RootScreen
import com.cryptodanilo.project.core.presentation.components.ComposeMultiplatformWatermark
import com.cryptodanilo.project.core.presentation.components.MockDataBanner
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
            RootScreen(
                modifier = Modifier.fillMaxSize(),
                onBackNavigableChanged = onBackNavigableChanged,
                backRequests = backRequests,
            )

            ComposeMultiplatformWatermark()
            MockDataBanner()
        }
    }
}
