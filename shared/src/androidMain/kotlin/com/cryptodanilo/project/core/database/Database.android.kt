package com.cryptodanilo.project.core.database

import android.content.Context
import androidx.room3.Room
import androidx.room3.RoomDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<CryptoDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath(CryptoDatabase.DB_NAME)
    return Room.databaseBuilder<CryptoDatabase>(
        context = appContext,
        name = dbFile.absolutePath,
    )
}
