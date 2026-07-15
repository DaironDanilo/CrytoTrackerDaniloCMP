package com.cryptodanilo.project.crypto.data.networking.dto

import kotlinx.serialization.Serializable

@Serializable
data class MarketDto(
    val exchangeId: String,
    val rank: String,
    val baseSymbol: String,
    val baseId: String,
    val quoteSymbol: String,
    val quoteId: String,
    val priceQuote: String,
    val priceUsd: String,
    val volumeUsd24Hr: String,
    val percentExchangeVolume: String,
    val tradesCount24Hr: Long? = null,
    val updated: Long,
)
