package com.cryptodanilo.project.core.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import androidx.window.core.layout.WindowSizeClass
import com.cryptodanilo.project.core.presentation.shell.AppShellViewModel
import com.cryptodanilo.project.core.presentation.util.ObserveAsEvents
import com.cryptodanilo.project.crypto.presentation.coinDetail.CoinDetailScreen
import com.cryptodanilo.project.crypto.presentation.coinList.CoinListAction
import com.cryptodanilo.project.crypto.presentation.coinList.CoinListEvent
import com.cryptodanilo.project.crypto.presentation.coinList.CoinListViewModel
import com.cryptodanilo.project.crypto.presentation.coinList.components.CoinListScreen
import cryptotrackerdanilo.shared.generated.resources.Res
import cryptotrackerdanilo.shared.generated.resources.tab_crypto
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun AdaptiveCoinListDetailPane(
    topBarViewModel: AppShellViewModel,
    viewModel: CoinListViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
    onBackNavigableChanged: (canNavigateBack: Boolean) -> Unit = {},
    backRequests: Flow<Unit> = emptyFlow(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(events = viewModel.events) { event ->
        when (event) {
            is CoinListEvent.Error -> {
                println("error: ${event.error}")
            }
        }
    }
    val windowAdaptiveInfo = currentWindowAdaptiveInfoV2()
    val isCompact =
        !windowAdaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(
            WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND,
        )
    val navigator =
        rememberListDetailPaneScaffoldNavigator(
            scaffoldDirective = calculatePaneScaffoldDirective(windowAdaptiveInfo),
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
    val canNavigateBack = navigator.canNavigateBack()
    NavigationBackHandler(
        state = navState,
        isBackEnabled = canNavigateBack,
        onBackCompleted = { navigateBack() },
    )

    // Lets the web/Wasm target mirror this in-app back-navigable state onto the
    // browser's history stack (see WebMain.kt), since browser back/forward are
    // otherwise not synchronized with this adaptive-pane navigator.
    LaunchedEffect(canNavigateBack) {
        onBackNavigableChanged(canNavigateBack)
    }
    LaunchedEffect(backRequests) {
        backRequests.collect { navigateBack() }
    }

    // Mirrors the pane navigator's list/detail state onto the app-level top bar: full title
    // when the list is showing, the selected coin's name + a working back button (wired to
    // this same pane navigator) when mobile is showing the detail pane on its own.
    val cryptoTabTitle = stringResource(Res.string.tab_crypto)
    val selectedCoinName = state.selectedCoinUi?.name
    LaunchedEffect(isListOfCoinsPaneHidden, selectedCoinName, cryptoTabTitle, isCompact, isDualPane) {
        if (isListOfCoinsPaneHidden && selectedCoinName != null) {
            topBarViewModel.setDetailMode(
                title = selectedCoinName,
                onBack = { navigateBack() },
                isCompact = isCompact,
                // isListOfCoinsPaneHidden being true already implies isDualPane is false here,
                // but this keeps the shell state driven by the real pane-navigator signal rather
                // than an assumption baked into the call site.
                isTwoPane = isDualPane,
            )
        } else {
            topBarViewModel.setListMode(title = cryptoTabTitle)
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                },
    ) {
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
                                // Tag coin selection with the current pane layout so the ViewModel
                                // knows whether to keep the selected detail tab (dual-pane) or reset
                                // it to Chart (single-pane, where the user explicitly navigated away).
                                val resolvedAction =
                                    if (action is CoinListAction.OnCoinClicked) {
                                        action.copy(isDualPane = isDualPane)
                                    } else {
                                        action
                                    }
                                viewModel.onAction(resolvedAction)
                                when (resolvedAction) {
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
                            shouldExistSharedElementTransition = shouldExistSharedElementTransition,
                            onAction = { action -> viewModel.onAction(action) },
                        )
                    }
                },
                modifier = modifier,
            )
        }
    }
}
