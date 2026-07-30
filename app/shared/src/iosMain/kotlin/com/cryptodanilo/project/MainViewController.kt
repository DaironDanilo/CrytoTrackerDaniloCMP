package com.cryptodanilo.project

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.rememberNavBackStack
import com.cryptodanilo.project.core.navigation.AppTopBar
import com.cryptodanilo.project.core.navigation.BottomTab
import com.cryptodanilo.project.core.navigation.CoinList
import com.cryptodanilo.project.core.navigation.CryptoNavDisplay
import com.cryptodanilo.project.core.navigation.StocksNavDisplay
import com.cryptodanilo.project.core.navigation.StocksTab
import com.cryptodanilo.project.core.navigation.appNavSavedStateConfig
import com.cryptodanilo.project.core.presentation.shell.AppShellViewModel
import com.cryptodanilo.project.di.initKoin
import com.cryptodanilo.project.ui.theme.CryptoTrackerTheme
import com.cryptodanilo.project.ui.theme.CryptoTrackerThemeProvider
import org.koin.compose.viewmodel.koinViewModel

@Suppress("FunctionName")
fun MainViewController() =
    ComposeUIViewController(
        configure = {
            initKoin()
        },
    ) { App() }

// iOS 26+ Liquid Glass tab bar: a native SwiftUI TabView owns tab switching and renders the
// system glass material, so each tab gets its own ComposeUIViewController with no Compose-side
// bottom bar chrome (unlike RootScreen.kt, which drives the Crossfade-based bottom bar used on
// Android/Desktop/Web and on the pre-iOS-26/iPad fallback here). Detail navigation (e.g. coin
// list -> coin detail) still stays entirely inside Compose via the existing NavDisplay per tab —
// only the top-level tab switcher is native.
@Suppress("FunctionName")
fun CryptoTabViewController(onBottomBarVisibilityChanged: (Boolean) -> Unit) =
    ComposeUIViewController(
        configure = { initKoin() },
    ) { SingleTabApp(tab = BottomTab.CRYPTO, onBottomBarVisibilityChanged = onBottomBarVisibilityChanged) }

@Suppress("FunctionName")
fun StocksTabViewController(onBottomBarVisibilityChanged: (Boolean) -> Unit) =
    ComposeUIViewController(
        configure = { initKoin() },
    ) { SingleTabApp(tab = BottomTab.STOCKS, onBottomBarVisibilityChanged = onBottomBarVisibilityChanged) }

@Composable
private fun SingleTabApp(
    tab: BottomTab,
    onBottomBarVisibilityChanged: (Boolean) -> Unit,
) {
    CryptoTrackerThemeProvider {
        val topBarViewModel: AppShellViewModel = koinViewModel()
        val topBarState by topBarViewModel.state.collectAsStateWithLifecycle()

        // Reports AppShellViewModel.setDetailMode's showBottomBar flag (already used on other
        // platforms to hide RootScreen's Compose bottom bar in compact detail mode, e.g. an open
        // coin detail screen) to Swift, so the native tab bar hides/shows in sync instead of
        // floating over a detail screen that expects it gone.
        LaunchedEffect(topBarState.showBottomBar) {
            onBottomBarVisibilityChanged(topBarState.showBottomBar)
        }

        Scaffold(
            topBar = { if (topBarState.showTopBar) AppTopBar(state = topBarState) },
            containerColor = CryptoTrackerTheme.colors.background,
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                when (tab) {
                    BottomTab.CRYPTO -> {
                        val backStack = rememberNavBackStack(appNavSavedStateConfig, CoinList)
                        CryptoNavDisplay(backStack = backStack, topBarViewModel = topBarViewModel, visible = true)
                    }
                    BottomTab.STOCKS -> {
                        val backStack = rememberNavBackStack(appNavSavedStateConfig, StocksTab)
                        StocksNavDisplay(backStack = backStack, topBarViewModel = topBarViewModel, visible = true)
                    }
                }
            }
        }
    }
}
