package com.cryptodanilo.project.core.navigation

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Serializable
sealed interface AppNavKey : NavKey

@Serializable
data object CoinList : AppNavKey

@Serializable
data object StocksTab : AppNavKey

val appNavSavedStateConfig =
    SavedStateConfiguration {
        serializersModule =
            SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(CoinList::class, CoinList.serializer())
                    subclass(StocksTab::class, StocksTab.serializer())
                }
                polymorphic(AppNavKey::class) {
                    subclass(CoinList::class, CoinList.serializer())
                    subclass(StocksTab::class, StocksTab.serializer())
                }
            }
    }

enum class BottomTab {
    CRYPTO,
    STOCKS,
}
