package com.cryptodanilo.project.crypto.domain

data class Market(
    val exchangeId: String,
    val rank: Int,
    val baseSymbol: String,
    val quoteSymbol: String,
    val baseId: String,
    val quoteId: String,
    val priceUsd: Double,
    val volumeUsd24Hr: Double,
    val percentExchangeVolume: Double,
    val tradesCount24Hr: Long,
    val updated: Long,
)
