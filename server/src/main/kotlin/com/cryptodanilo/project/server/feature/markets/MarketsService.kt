package com.cryptodanilo.project.server.feature.markets

import com.cryptodanilo.project.server.common.PagedResult
import com.cryptodanilo.project.server.domain.Market
import com.cryptodanilo.project.server.feature.coins.CoinsRepository

sealed interface MarketsResult {
    data class Found(
        val result: PagedResult<Market>,
    ) : MarketsResult

    data object CoinNotFound : MarketsResult
}

/** Owns the "does this coin exist, and if so fetch its markets" orchestration
 * that previously lived inline in the route handler. */
class MarketsService(
    private val marketsRepository: MarketsRepository,
    private val coinsRepository: CoinsRepository,
) {
    suspend fun getMarkets(
        coinId: String,
        limit: Int,
        offset: Int,
    ): MarketsResult {
        if (!coinsRepository.coinExists(coinId)) return MarketsResult.CoinNotFound
        return MarketsResult.Found(marketsRepository.getMarkets(coinId, limit, offset))
    }
}
