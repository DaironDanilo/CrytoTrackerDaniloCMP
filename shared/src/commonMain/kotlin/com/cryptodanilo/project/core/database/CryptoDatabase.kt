package com.cryptodanilo.project.core.database

import androidx.room3.AutoMigration
import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor

@Database(
    entities = [CoinEntity::class, CoinPriceEntity::class],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
    ],
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
