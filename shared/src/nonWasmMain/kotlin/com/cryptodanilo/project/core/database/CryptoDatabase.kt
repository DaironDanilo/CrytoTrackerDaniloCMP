package com.cryptodanilo.project.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

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
        const val DB_FOLDER = ".cryptotracker" // desktop subfolder
    }
}

expect object CryptoDatabaseConstructor : RoomDatabaseConstructor<CryptoDatabase> {
    override fun initialize(): CryptoDatabase
}

fun getRoomDatabase(builder: RoomDatabase.Builder<CryptoDatabase>): CryptoDatabase =
    builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
