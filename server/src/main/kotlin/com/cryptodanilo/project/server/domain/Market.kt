package com.cryptodanilo.project.server.domain

/** Internal representation of an exchange listing for a coin. Distinct from
 * [com.cryptodanilo.project.server.feature.markets.MarketResponse], the wire
 * format -- see [Coin]'s doc for why these are kept separate. */
data class Market(
    val exchangeId: String,
    val baseSymbol: String,
    val targetSymbol: String,
    val priceUsd: Double?,
    val volumeUsd24h: Double?,
    val trustScore: String?,
    val lastTradedAt: Long?,
)
