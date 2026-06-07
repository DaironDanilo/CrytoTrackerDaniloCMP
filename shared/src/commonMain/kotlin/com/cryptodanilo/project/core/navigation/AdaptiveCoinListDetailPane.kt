package com.cryptodanilo.project.core.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
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
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.cryptodanilo.project.core.presentation.util.ObserveAsEvents
import com.cryptodanilo.project.crypto.presentation.coinDetail.CoinDetailScreen
import com.cryptodanilo.project.crypto.presentation.coinList.CoinListAction
import com.cryptodanilo.project.crypto.presentation.coinList.CoinListEvent
import com.cryptodanilo.project.crypto.presentation.coinList.CoinListViewModel
import com.cryptodanilo.project.crypto.presentation.coinList.components.CoinListScreen
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

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
    val navigator =
        rememberListDetailPaneScaffoldNavigator(
            scaffoldDirective =
                calculatePaneScaffoldDirective(
                    currentWindowAdaptiveInfoV2(),
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

    val navState = rememberNavigationEventState(NavigationEventInfo.None)
    NavigationBackHandler(
        state = navState,
        isBackEnabled = true,
        onBackCompleted = {
            coroutineScope.launch {
                navigator.navigateBack()
            }
        },
    )
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
                                else -> Unit
                            }
                        },
                    )
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
                        },
                        onAction = { action -> viewModel.onAction(action) },
                    )
                }
            },
            modifier = modifier,
        )
    }
}
