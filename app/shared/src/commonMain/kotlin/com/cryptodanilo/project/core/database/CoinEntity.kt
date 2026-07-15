package com.cryptodanilo.project.core.database

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.cryptodanilo.project.core.database.CoinEntity.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class CoinEntity(
    @PrimaryKey val id: String,
    val rank: Int,
    val symbol: String,
    val name: String,
    val marketCapUsd: Double,
    val priceUsd: Double,
    val changePercent24Hr: Double,
    val cachedAt: Long,
) {
    companion object {
        const val TABLE_NAME = "coins"
    }
}
