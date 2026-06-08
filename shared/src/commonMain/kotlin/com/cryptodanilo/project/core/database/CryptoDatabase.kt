package com.cryptodanilo.project.core.database

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor

@Database(
    entities = [CoinEntity::class],
    version = 1,
    exportSchema = true,
)
@ConstructedBy(CryptoDatabaseConstructor::class)
abstract class CryptoDatabase : RoomDatabase() {
    abstract fun coinDao(): CoinDao

    companion object {
        const val DB_NAME = "crypto_tracker.db"
        const val DB_FOLDER = ".cryptotracker"
    }
}

expect object CryptoDatabaseConstructor : RoomDatabaseConstructor<CryptoDatabase> {
    override fun initialize(): CryptoDatabase
}
