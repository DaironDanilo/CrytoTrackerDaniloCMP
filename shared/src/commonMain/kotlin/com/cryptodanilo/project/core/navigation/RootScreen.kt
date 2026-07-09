package com.cryptodanilo.project.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.rememberNavBackStack
import com.cryptodanilo.project.core.presentation.shell.AppShellViewModel
import com.cryptodanilo.project.ui.theme.CryptoTrackerTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RootScreen(
    modifier: Modifier = Modifier,
    onBackNavigableChanged: (canNavigateBack: Boolean) -> Unit = {},
    backRequests: Flow<Unit> = emptyFlow(),
) {
    val topBarViewModel: AppShellViewModel = koinViewModel()
    val topBarState by topBarViewModel.state.collectAsStateWithLifecycle()

    // One back stack per tab — preserves each tab's own navigation state when switching.
    val cryptoBackStack = rememberNavBackStack(appNavSavedStateConfig, CoinList)
    val stocksBackStack = rememberNavBackStack(appNavSavedStateConfig, StocksTab)

    var selectedTab by remember { mutableStateOf(BottomTab.CRYPTO) }

    Scaffold(
        modifier = modifier,
        topBar = {
            if (topBarState.showTopBar) {
                AppTopBar(state = topBarState)
            }
        },
        bottomBar = {
            if (topBarState.showBottomBar) {
                AppBottomNavBar(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                )
            }
        },
        containerColor = CryptoTrackerTheme.colors.background,
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            CryptoNavDisplay(
                backStack = cryptoBackStack,
                topBarViewModel = topBarViewModel,
                visible = selectedTab == BottomTab.CRYPTO,
                onBackNavigableChanged = onBackNavigableChanged,
                backRequests = backRequests,
            )
            StocksNavDisplay(
                backStack = stocksBackStack,
                topBarViewModel = topBarViewModel,
                visible = selectedTab == BottomTab.STOCKS,
            )
        }
    }
}
