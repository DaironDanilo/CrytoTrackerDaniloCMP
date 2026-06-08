package com.cryptodanilo.project.core.database

import androidx.room.Room
import androidx.room.RoomDatabase
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
