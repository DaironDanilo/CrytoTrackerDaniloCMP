package com.cryptodanilo.project.di

import com.cryptodanilo.project.core.database.CoinDao
import com.cryptodanilo.project.core.database.CoinPriceDao
import com.cryptodanilo.project.core.database.CryptoDatabase
import com.cryptodanilo.project.core.database.getDatabaseBuilder
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.js.Js
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module
    get() =
        module {
            single<HttpClientEngine> { Js.create() }
            single<CryptoDatabase> { getDatabaseBuilder().build() }
            single<CoinDao> { get<CryptoDatabase>().coinDao() }
            single<CoinPriceDao> { get<CryptoDatabase>().coinPriceDao() }
        }
