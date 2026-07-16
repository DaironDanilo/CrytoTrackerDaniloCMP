package com.cryptodanilo.project.server.feature.coins

import com.cryptodanilo.project.server.domain.Coin
import kotlinx.serialization.Serializable

@Serializable
data class CoinResponse(
    val id: String,
    val symbol: String,
    val name: String,
    val rank: Int?,
    val priceUsd: Double,
    val marketCapUsd: Double?,
    val changePercent24h: Double?,
)

fun Coin.toResponse(): CoinResponse =
    CoinResponse(
        id = id,
        symbol = symbol,
        name = name,
        rank = rank,
        priceUsd = priceUsd,
        marketCapUsd = marketCapUsd,
        changePercent24h = changePercent24h,
    )
