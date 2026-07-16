package com.cryptodanilo.project.server.feature.coins

import com.cryptodanilo.project.server.common.PagedResult
import com.cryptodanilo.project.server.domain.Coin

/**
 * Deliberately thin: the "list coins" operation has no orchestration or
 * business rules beyond pagination (already validated at the route
 * boundary), so this is close to a pure pass-through today. It still earns
 * its place as a seam between the route and the repository -- the value is
 * having a designated home for coin-related logic if/when it grows (e.g.
 * filtering, sorting options, caching), not that today's version is complex.
 */
class CoinsService(
    private val coinsRepository: CoinsRepository,
) {
    suspend fun getCoins(
        limit: Int,
        offset: Int,
    ): PagedResult<Coin> = coinsRepository.getCoins(limit, offset)
}
