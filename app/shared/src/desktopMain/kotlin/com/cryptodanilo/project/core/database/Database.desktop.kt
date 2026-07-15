package com.cryptodanilo.project.core.database

import androidx.room3.Room
import androidx.room3.RoomDatabase
import java.io.File

fun getDatabaseBuilder(): RoomDatabase.Builder<CryptoDatabase> {
    val dbFile =
        File(
            System.getProperty("user.home"),
            "${CryptoDatabase.DB_FOLDER}/${CryptoDatabase.DB_NAME}",
        )
    dbFile.parentFile?.mkdirs()
    return Room.databaseBuilder<CryptoDatabase>(
        name = dbFile.absolutePath,
    )
}
