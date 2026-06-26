package com.cryptodanilo.project.core.database

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction

@Dao
interface CoinPriceDao {
    @Query("SELECT * FROM ${CoinPriceEntity.TABLE_NAME} WHERE coinId = :coinId AND timeframe = :timeframe ORDER BY timestampMs ASC")
    suspend fun getPriceHistory(
        coinId: String,
        timeframe: String,
    ): List<CoinPriceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPriceHistory(prices: List<CoinPriceEntity>)

    @Query("DELETE FROM ${CoinPriceEntity.TABLE_NAME} WHERE coinId = :coinId AND timeframe = :timeframe")
    suspend fun deletePriceHistory(
        coinId: String,
        timeframe: String,
    )

    @Query("SELECT cachedAt FROM ${CoinPriceEntity.TABLE_NAME} WHERE coinId = :coinId AND timeframe = :timeframe LIMIT 1")
    suspend fun getCachedAt(
        coinId: String,
        timeframe: String,
    ): Long?

    @Query("SELECT COUNT(*) FROM ${CoinPriceEntity.TABLE_NAME} WHERE coinId = :coinId AND timeframe = :timeframe")
    suspend fun getCount(
        coinId: String,
        timeframe: String,
    ): Int

    @Transaction
    suspend fun replacePriceHistory(
        coinId: String,
        timeframe: String,
        prices: List<CoinPriceEntity>,
    ) {
        deletePriceHistory(coinId, timeframe)
        if (prices.isNotEmpty()) {
            insertPriceHistory(prices)
        }
    }
}
