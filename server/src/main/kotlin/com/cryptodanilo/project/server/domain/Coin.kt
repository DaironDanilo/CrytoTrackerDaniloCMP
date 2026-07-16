package com.cryptodanilo.project.server.domain

/** Internal representation of a coin + its latest snapshot. Distinct from
 * [com.cryptodanilo.project.server.feature.coins.CoinResponse], which is the
 * wire format -- kept separate so the API's shape can evolve independently
 * of what the repository/service layer works with internally. */
data class Coin(
    val id: String,
    val symbol: String,
    val name: String,
    val rank: Int?,
    val priceUsd: Double,
    val marketCapUsd: Double?,
    val changePercent24h: Double?,
)
