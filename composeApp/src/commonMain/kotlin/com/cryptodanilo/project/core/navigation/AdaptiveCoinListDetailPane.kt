package com.cryptodanilo.project.core.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldRole
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cryptodanilo.project.core.presentation.util.ObserveAsEvents
import com.cryptodanilo.project.crypto.presentation.coin_detail.CoinDetailScreen
import com.cryptodanilo.project.crypto.presentation.coin_list.CoinListAction
import com.cryptodanilo.project.crypto.presentation.coin_list.CoinListEvent
import com.cryptodanilo.project.crypto.presentation.coin_list.CoinListViewModel
import com.cryptodanilo.project.crypto.presentation.coin_list.components.CoinListScreen
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
expect fun BackHandler(enabled: Boolean = true, onBack: () -> Unit = {})

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun AdaptiveCoinListDetailPane(
    viewModel: CoinListViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(events = viewModel.events) { event ->
        when (event) {
            is CoinListEvent.Error -> {
                println("error: ${event.error}")
            }
        }
    }
    val navigator = rememberListDetailPaneScaffoldNavigator(
        scaffoldDirective = calculatePaneScaffoldDirective(
            currentWindowAdaptiveInfo()
        ),
    )
    // calculates if the list pane is hidden
    val isListOfCoinsPaneHidden =
        navigator.scaffoldValue[ThreePaneScaffoldRole.Secondary] == PaneAdaptedValue.Hidden

    // calculates if the coin details pane is hidden
    val isCoinDetailPaneHidden =
        navigator.scaffoldValue[ThreePaneScaffoldRole.Primary] == PaneAdaptedValue.Hidden

    val shouldExistSharedElementTransition =
        !isListOfCoinsPaneHidden && isCoinDetailPaneHidden || isListOfCoinsPaneHidden && !isCoinDetailPaneHidden

    val coroutineScope = rememberCoroutineScope()

    BackHandler {
        coroutineScope.launch {
            navigator.navigateBack()
        }
    }
    SharedTransitionLayout {
        ListDetailPaneScaffold(
            directive = navigator.scaffoldDirective,
            value = navigator.scaffoldValue,
            listPane = {
                AnimatedPane {
                    CoinListScreen(
                        animatedPaneScope = this,
                        state = state,
                        shouldExistSharedElementTransition = shouldExistSharedElementTransition,
                        onAction = { action ->
                            viewModel.onAction(action)
                            when (action) {
                                is CoinListAction.OnCoinClicked -> {
                                    coroutineScope.launch {
                                        navigator.navigateTo(pane = ListDetailPaneScaffoldRole.Detail)
                                    }
                                }

                                CoinListAction.OnRefresh -> TODO()
                            }
                        })
                }
            },
            detailPane = {
                AnimatedPane {
                    CoinDetailScreen(
                        animatedPaneScope = this,
                        state = state,
                        shouldShowBackNavigationIcon = isListOfCoinsPaneHidden,
                        shouldExistSharedElementTransition = shouldExistSharedElementTransition,
                        onBack = {
                            coroutineScope.launch {
                                navigator.navigateBack()
                            }
                        })
                }
            },
            modifier = modifier
        )
    }
}
