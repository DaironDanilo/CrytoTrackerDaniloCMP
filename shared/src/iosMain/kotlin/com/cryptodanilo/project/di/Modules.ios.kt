package com.cryptodanilo.project.di

import com.cryptodanilo.project.core.database.CoinDao
import com.cryptodanilo.project.core.database.CryptoDatabase
import com.cryptodanilo.project.core.database.getDatabaseBuilder
import com.cryptodanilo.project.core.database.getRoomDatabase
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module
    get() =
        module {
            single<HttpClientEngine> { Darwin.create() }
            single<CryptoDatabase> { getRoomDatabase(getDatabaseBuilder()) }
            single<CoinDao> { get<CryptoDatabase>().coinDao() }
        }
