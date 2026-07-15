package com.cryptodanilo.project.core.database

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor

// version 3: coin_price_history recreated without the FK that caused
// FOREIGN KEY constraint failed on deleteAllCoins() — Room 3 enforces FKs
// immediately. All builders use fallbackToDestructiveMigration so devices
// on v1 or v2 are wiped and recreated clean (acceptable for a cache-only DB).
@Database(
    entities = [CoinEntity::class, CoinPriceEntity::class],
    version = 3,
    exportSchema = true,
)
@ConstructedBy(CryptoDatabaseConstructor::class)
abstract class CryptoDatabase : RoomDatabase() {
    abstract fun coinDao(): CoinDao

    abstract fun coinPriceDao(): CoinPriceDao

    companion object {
        const val DB_NAME = "crypto_tracker.db"
        const val DB_FOLDER = ".cryptotracker"
        const val CACHE_TTL_MS = 5L * 60 * 1000
    }
}

expect object CryptoDatabaseConstructor : RoomDatabaseConstructor<CryptoDatabase> {
    override fun initialize(): CryptoDatabase
}
