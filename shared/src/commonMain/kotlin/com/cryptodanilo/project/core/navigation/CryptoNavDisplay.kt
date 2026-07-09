package com.cryptodanilo.project.core.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.cryptodanilo.project.core.presentation.shell.AppShellViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun CryptoNavDisplay(
    backStack: NavBackStack<NavKey>,
    topBarViewModel: AppShellViewModel,
    visible: Boolean,
    modifier: Modifier = Modifier,
    onBackNavigableChanged: (canNavigateBack: Boolean) -> Unit = {},
    backRequests: Flow<Unit> = emptyFlow(),
) {
    AnimatedVisibility(visible = visible, modifier = modifier) {
        NavDisplay(
            modifier = Modifier.fillMaxSize(),
            backStack = backStack,
            entryDecorators =
                listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator(),
                ),
            entryProvider =
                entryProvider {
                    entry<CoinList> {
                        AdaptiveCoinListDetailPane(
                            topBarViewModel = topBarViewModel,
                            onBackNavigableChanged = onBackNavigableChanged,
                            backRequests = backRequests,
                        )
                    }
                },
        )
    }
}
