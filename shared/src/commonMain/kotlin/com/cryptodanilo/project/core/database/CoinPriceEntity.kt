package com.cryptodanilo.project.core.database

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import com.cryptodanilo.project.core.database.CoinPriceEntity.Companion.TABLE_NAME

// Composite PK (coinId, timeframe, timestampMs) clusters rows for the same
// (coinId, timeframe) together in SQLite's B-tree — the practical equivalent of
// "partitioning" in a DB that doesn't support true table partitioning.
// The explicit index on (coinId, timeframe) makes TTL checks and full-range
// fetches index-seeks rather than full-table scans.
@Entity(
    tableName = TABLE_NAME,
    primaryKeys = ["coinId", "timeframe", "timestampMs"],
    foreignKeys = [
        ForeignKey(
            entity = CoinEntity::class,
            parentColumns = ["id"],
            childColumns = ["coinId"],
            // NO_ACTION (deferred check): allows the coin list's deleteAllCoins() +
            // insertCoins() pair to complete without violating this FK during the
            // window between the two calls. Without this, every TTL-expiry list
            // refresh would cascade-delete all chart data.
            onDelete = ForeignKey.NO_ACTION,
        ),
    ],
    indices = [
        Index(value = ["coinId", "timeframe"]),
    ],
)
data class CoinPriceEntity(
    val coinId: String,
    val timeframe: String,
    val timestampMs: Long,
    val priceUsd: Double,
    val cachedAt: Long,
) {
    companion object {
        const val TABLE_NAME = "coin_price_history"
    }
}
