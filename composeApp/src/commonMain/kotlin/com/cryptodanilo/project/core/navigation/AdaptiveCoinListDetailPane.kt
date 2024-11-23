package com.cryptodanilo.project.core.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cryptodanilo.project.core.presentation.util.ObserveAsEvents
import com.cryptodanilo.project.crypto.presentation.coin_detail.CoinDetailScreen
import com.cryptodanilo.project.crypto.presentation.coin_list.CoinListAction
import com.cryptodanilo.project.crypto.presentation.coin_list.CoinListEvent
import com.cryptodanilo.project.crypto.presentation.coin_list.CoinListViewModel
import com.cryptodanilo.project.crypto.presentation.coin_list.components.CoinListScreen
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
    val navigator = rememberListDetailPaneScaffoldNavigator<Any>()

    BackHandler(onBack = { navigator.navigateBack() })
    SharedTransitionLayout {
        ListDetailPaneScaffold(
            directive = navigator.scaffoldDirective,
            value = navigator.scaffoldValue,
            listPane = {
                AnimatedPane {
                    CoinListScreen(animatedPaneScope = this, state = state, onAction = { action ->
                        viewModel.onAction(action)
                        when (action) {
                            is CoinListAction.OnCoinClicked -> {
                                navigator.navigateTo(pane = ListDetailPaneScaffoldRole.Detail)
                            }

                            CoinListAction.OnRefresh -> TODO()
                        }
                    })
                }
            },
            detailPane = {
                AnimatedPane {
                    CoinDetailScreen(animatedPaneScope = this, state = state)
                }
            },
            modifier = modifier
        )
    }
}
