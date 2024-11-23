package com.cryptodanilo.project

import android.app.Application
import com.cryptodanilo.project.di.initKoin
import org.koin.android.ext.koin.androidContext

class CryptoApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@CryptoApp)
        }
    }
}
