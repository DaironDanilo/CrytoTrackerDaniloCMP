package com.cryptodanilo.project.crypto.presentation.coinlist

import com.cryptodanilo.project.core.domain.util.NetworkError

sealed interface CoinListEvent {
    data class Error(
        val error: NetworkError,
    ) : CoinListEvent
}
