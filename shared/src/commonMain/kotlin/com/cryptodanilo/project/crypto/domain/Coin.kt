package com.cryptodanilo.project.crypto.domain

data class Coin(
    val id: String,
    val rank: Int,
    val symbol: String,
    val name: String,
    val marketCapUsd: Double,
    val priceUsd: Double,
    val changePercent24Hr: Double,
)
