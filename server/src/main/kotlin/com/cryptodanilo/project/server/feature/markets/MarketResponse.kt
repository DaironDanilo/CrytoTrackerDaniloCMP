package com.cryptodanilo.project.server.feature.markets

import com.cryptodanilo.project.server.domain.Market
import kotlinx.serialization.Serializable

@Serializable
data class MarketResponse(
    val exchangeId: String,
    val baseSymbol: String,
    val targetSymbol: String,
    val priceUsd: Double?,
    val volumeUsd24h: Double?,
    val trustScore: String?,
    val lastTradedAt: Long?,
)

fun Market.toResponse(): MarketResponse =
    MarketResponse(
        exchangeId = exchangeId,
        baseSymbol = baseSymbol,
        targetSymbol = targetSymbol,
        priceUsd = priceUsd,
        volumeUsd24h = volumeUsd24h,
        trustScore = trustScore,
        lastTradedAt = lastTradedAt,
    )
