package com.cryptodanilo.project.crypto.presentation.coinList

import com.cryptodanilo.project.core.domain.util.NetworkError

sealed interface CoinListEvent {
    data class Error(
        val error: NetworkError,
    ) : CoinListEvent
}
