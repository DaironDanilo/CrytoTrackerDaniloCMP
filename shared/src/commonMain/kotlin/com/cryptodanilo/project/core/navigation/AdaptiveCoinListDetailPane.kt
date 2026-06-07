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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Duration.Companion.milliseconds

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

    val isDualPane = !isListOfCoinsPaneHidden && !isCoinDetailPaneHidden

    LaunchedEffect(isDualPane, state.coins) {
        if (isDualPane && state.coins.isNotEmpty()) {
            viewModel.onAction(CoinListAction.OnCoinsLoaded)
        }
    }

    val shouldExistSharedElementTransition =
        !isListOfCoinsPaneHidden && isCoinDetailPaneHidden || isListOfCoinsPaneHidden && !isCoinDetailPaneHidden

    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var searchBarFocusable by remember { mutableStateOf(true) }

    fun navigateBack() {
        searchBarFocusable = false
        keyboardController?.hide()
        coroutineScope.launch {
            navigator.navigateBack()
            delay(400.milliseconds)
            searchBarFocusable = true
        }
    }

    val navState = rememberNavigationEventState(NavigationEventInfo.None)
    NavigationBackHandler(
        state = navState,
        isBackEnabled = true,
        onBackCompleted = { navigateBack() },
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
                        isSearchBarFocusable = searchBarFocusable,
                        onAction = { action ->
                            viewModel.onAction(action)
                            when (action) {
                                is CoinListAction.OnCoinClicked -> {
                                    keyboardController?.hide()
                                    focusManager.clearFocus(force = true)
                                    coroutineScope.launch {
                                        navigator.navigateTo(pane = ListDetailPaneScaffoldRole.Detail)
                                    }
                                }

                                CoinListAction.OnRefresh -> Unit
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
                        onBack = { navigateBack() },
                        onAction = { action -> viewModel.onAction(action) },
                    )
                }
            },
            modifier = modifier,
        )
    }
}
