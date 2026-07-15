package com.cryptodanilo.project.core.database

import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.web.WebWorkerSQLiteDriver

fun getDatabaseBuilder(): RoomDatabase.Builder<CryptoDatabase> =
    Room
        .databaseBuilder<CryptoDatabase>(name = CryptoDatabase.DB_NAME)
        .setDriver(WebWorkerSQLiteDriver(createSQLiteWorker()))
        .fallbackToDestructiveMigration(dropAllTables = true)
