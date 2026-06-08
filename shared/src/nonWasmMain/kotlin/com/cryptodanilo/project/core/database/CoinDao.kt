package com.cryptodanilo.project.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CoinDao {
    @Query("SELECT * FROM ${CoinEntity.TABLE_NAME} ORDER BY rank ASC")
    suspend fun getAllCoins(): List<CoinEntity>

    @Query("SELECT * FROM ${CoinEntity.TABLE_NAME} ORDER BY rank ASC LIMIT :limit OFFSET :offset")
    suspend fun getCoins(
        limit: Int,
        offset: Int,
    ): List<CoinEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoins(coins: List<CoinEntity>)

    @Query("DELETE FROM ${CoinEntity.TABLE_NAME}")
    suspend fun deleteAllCoins()

    @Query("SELECT COUNT(*) FROM ${CoinEntity.TABLE_NAME}")
    suspend fun getCount(): Int
}
