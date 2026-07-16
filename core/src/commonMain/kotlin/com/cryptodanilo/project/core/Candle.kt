package com.cryptodanilo.project.core

import kotlinx.serialization.Serializable

@Serializable
data class Candle(
    val openTime: Long,
    val closeTime: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Double,
    val isClosed: Boolean,
)
