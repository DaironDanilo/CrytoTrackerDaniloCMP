package com.cryptodanilo.project.crypto.presentation.coinlist

import com.cryptodanilo.project.crypto.presentation.models.CoinUi

sealed interface CoinListAction {
    data class OnCoinClicked(
        val coinUi: CoinUi,
    ) : CoinListAction

    data object OnRefresh : CoinListAction
}
