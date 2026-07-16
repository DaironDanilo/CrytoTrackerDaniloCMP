package com.cryptodanilo.project.crypto.data.networking.server.dto

import kotlinx.serialization.Serializable

@Serializable
data class CoinResponseDto(
    val id: String,
    val symbol: String,
    val name: String,
    val rank: Int?,
    val priceUsd: Double,
    val marketCapUsd: Double?,
    val changePercent24h: Double?,
)

@Serializable
data class MarketResponseDto(
    val exchangeId: String,
    val baseSymbol: String,
    val targetSymbol: String,
    val priceUsd: Double?,
    val volumeUsd24h: Double?,
    val trustScore: String?,
    val lastTradedAt: Long?,
)

@Serializable
data class PageInfoDto(
    val limit: Int,
    val offset: Int,
    val total: Int,
    val hasMore: Boolean,
)

@Serializable
data class CoinsPageDto(
    val data: List<CoinResponseDto>,
    val page: PageInfoDto,
)

@Serializable
data class MarketsPageDto(
    val data: List<MarketResponseDto>,
    val page: PageInfoDto,
)
