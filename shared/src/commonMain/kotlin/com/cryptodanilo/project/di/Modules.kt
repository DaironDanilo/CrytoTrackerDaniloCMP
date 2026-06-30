package com.cryptodanilo.project.di

import com.cryptodanilo.project.BuildKonfig
import com.cryptodanilo.project.core.data.networking.HttpClientFactory
import com.cryptodanilo.project.crypto.data.networking.MockCoinDataSource
import com.cryptodanilo.project.crypto.data.networking.RemoteCoinDataSource
import com.cryptodanilo.project.crypto.domain.CoinDataSource
import com.cryptodanilo.project.crypto.presentation.coinList.CoinListViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule =
    module {
        single { HttpClientFactory.create(get()) }
        if (BuildKonfig.USE_MOCK_DATA) {
            single<CoinDataSource> { MockCoinDataSource() }
        } else {
            singleOf(::RemoteCoinDataSource).bind<CoinDataSource>()
        }
        viewModelOf(::CoinListViewModel)
    }
