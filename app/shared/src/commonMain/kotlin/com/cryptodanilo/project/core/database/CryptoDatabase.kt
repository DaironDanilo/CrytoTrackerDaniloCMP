package com.cryptodanilo.project.core.database

import androidx.room3.AutoMigration
import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor

// version 3: coin_price_history recreated without the FK that caused
// FOREIGN KEY constraint failed on deleteAllCoins() — Room 3 enforces FKs
// immediately. All builders use fallbackToDestructiveMigration so devices
// on v1 or v2 are wiped and recreated clean (acceptable for a cache-only DB).
//
// version 4: added favorite_coins (purely additive new table, hence the plain
// @AutoMigration below rather than relying on fallbackToDestructiveMigration like the v1->v2->v3
// bumps did). Unlike coins/coin_price_history, favorite_coins holds real, non-recoverable user
// data — it can't be re-fetched from the network — so this bump must preserve existing rows
// rather than wipe the whole DB.
@Database(
    entities = [CoinEntity::class, CoinPriceEntity::class, FavoriteCoinEntity::class],
    version = 4,
    autoMigrations = [AutoMigration(from = 3, to = 4)],
    exportSchema = true,
)
@ConstructedBy(CryptoDatabaseConstructor::class)
abstract class CryptoDatabase : RoomDatabase() {
    abstract fun coinDao(): CoinDao

    abstract fun coinPriceDao(): CoinPriceDao

    abstract fun favoriteCoinDao(): FavoriteCoinDao

    companion object {
        const val DB_NAME = "crypto_tracker.db"
        const val DB_FOLDER = ".cryptotracker"
        const val CACHE_TTL_MS = 5L * 60 * 1000
    }
}

expect object CryptoDatabaseConstructor : RoomDatabaseConstructor<CryptoDatabase> {
    override fun initialize(): CryptoDatabase
}
