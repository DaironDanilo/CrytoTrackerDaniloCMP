package com.cryptodanilo.project.crypto.data.networking.dto

import kotlinx.serialization.Serializable

@Serializable
data class MarketsResponseDto(
    val timestamp: Long,
    val data: List<MarketDto>,
)
