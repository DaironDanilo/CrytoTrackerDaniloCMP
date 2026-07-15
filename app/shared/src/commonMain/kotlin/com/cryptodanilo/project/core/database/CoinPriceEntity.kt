package com.cryptodanilo.project.core.database

import androidx.room3.Entity
import androidx.room3.Index
import com.cryptodanilo.project.core.database.CoinPriceEntity.Companion.TABLE_NAME

// Composite PK (coinId, timeframe, timestampMs) clusters rows for the same
// (coinId, timeframe) together in SQLite's B-tree — the practical equivalent of
// "partitioning" in a DB that doesn't support true table partitioning.
// The explicit index on (coinId, timeframe) makes TTL checks and full-range
// fetches index-seeks rather than full-table scans.
// No FK to coins: Room 3 enforces FKs immediately (PRAGMA foreign_keys = ON),
// so a FK with any onDelete action would crash when deleteAllCoins() runs before
// insertCoins() during a list refresh. The index alone is sufficient.
@Entity(
    tableName = TABLE_NAME,
    primaryKeys = ["coinId", "timeframe", "timestampMs"],
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
