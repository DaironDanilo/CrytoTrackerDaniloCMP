package com.cryptodanilo.project.di

import org.koin.core.context.startKoin
import org.koin.core.error.KoinApplicationAlreadyStartedException
import org.koin.dsl.KoinAppDeclaration

// Guarded against double-start: iOS's Liquid Glass tab bar hosts two independent
// ComposeUIViewControllers (one per tab), each configuring Koin on creation.
fun initKoin(config: KoinAppDeclaration? = null) {
    try {
        startKoin {
            config?.invoke(this)
            modules(sharedModule, platformModule)
        }
    } catch (e: KoinApplicationAlreadyStartedException) {
        // Already started by another entry point (e.g. the first of the two tab
        // ComposeUIViewControllers on iOS) — safe to ignore.
    }
}
