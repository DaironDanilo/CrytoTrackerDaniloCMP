package com.cryptodanilo.project.core.database

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query

@Dao
interface FavoriteCoinDao {
    @Query("SELECT coinId FROM ${FavoriteCoinEntity.TABLE_NAME}")
    suspend fun getFavoriteCoinIds(): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavorite(favorite: FavoriteCoinEntity)

    @Query("DELETE FROM ${FavoriteCoinEntity.TABLE_NAME} WHERE coinId = :coinId")
    suspend fun removeFavorite(coinId: String)
}
