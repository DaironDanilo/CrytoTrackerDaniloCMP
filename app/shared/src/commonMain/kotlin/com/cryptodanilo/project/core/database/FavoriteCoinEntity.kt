package com.cryptodanilo.project.core.database

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.cryptodanilo.project.core.database.FavoriteCoinEntity.Companion.TABLE_NAME

// Deliberately its own table, not a column on CoinEntity: coinDao.insertCoins() uses
// OnConflictStrategy.REPLACE on every list refresh, which fully replaces each row and would
// silently wipe an isFavorite column back to its default. Keeping favorites in a separate
// table means the coin cache's refresh lifecycle can never touch this data. No FK to coins,
// same reasoning as CoinPriceEntity: Room 3 enforces FKs immediately, and coinDao.deleteAllCoins()
// runs before every refresh.
@Entity(tableName = TABLE_NAME)
data class FavoriteCoinEntity(
    @PrimaryKey val coinId: String,
) {
    companion object {
        const val TABLE_NAME = "favorite_coins"
    }
}
