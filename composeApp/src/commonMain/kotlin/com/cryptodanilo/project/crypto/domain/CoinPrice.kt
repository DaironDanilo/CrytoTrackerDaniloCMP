package com.cryptodanilo.project.crypto.domain

import kotlinx.datetime.LocalDateTime

data class CoinPrice(
    val priceUsd: Double,
    val dateTime: LocalDateTime
)
