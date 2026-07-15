package com.cryptodanilo.project.crypto.data.mappers

import com.cryptodanilo.project.crypto.data.networking.dto.CoinDto
import com.cryptodanilo.project.crypto.data.networking.dto.CoinPriceDto
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class CoinMapperTest {
    // region toCoin

    @Test
    fun toCoin_mapsAllFieldsCorrectly() {
        val dto =
            CoinDto(
                id = "bitcoin",
                rank = 1,
                name = "Bitcoin",
                symbol = "BTC",
                marketCapUsd = 1_200_000_000_000.0,
                priceUsd = 62_000.0,
                changePercent24Hr = 2.5,
            )

        val coin = dto.toCoin()

        assertEquals("bitcoin", coin.id)
        assertEquals(1, coin.rank)
        assertEquals("Bitcoin", coin.name)
        assertEquals("BTC", coin.symbol)
        assertEquals(1_200_000_000_000.0, coin.marketCapUsd)
        assertEquals(62_000.0, coin.priceUsd)
        assertEquals(2.5, coin.changePercent24Hr)
    }

    @Test
    fun toCoin_preservesNegativeChangePercent() {
        val dto =
            CoinDto(
                id = "ethereum",
                rank = 2,
                name = "Ethereum",
                symbol = "ETH",
                marketCapUsd = 400_000_000_000.0,
                priceUsd = 3_300.0,
                changePercent24Hr = -1.75,
            )

        val coin = dto.toCoin()

        assertEquals(-1.75, coin.changePercent24Hr)
    }

    @Test
    fun toCoin_preservesRankOrder() {
        val first =
            CoinDto(
                id = "bitcoin",
                rank = 1,
                name = "Bitcoin",
                symbol = "BTC",
                marketCapUsd = 1.0,
                priceUsd = 1.0,
                changePercent24Hr = 0.0,
            ).toCoin()
        val second =
            CoinDto(
                id = "ethereum",
                rank = 2,
                name = "Ethereum",
                symbol = "ETH",
                marketCapUsd = 1.0,
                priceUsd = 1.0,
                changePercent24Hr = 0.0,
            ).toCoin()

        assertEquals(1, first.rank)
        assertEquals(2, second.rank)
        assertTrue(first.rank < second.rank)
    }

    // endregion

    // region toCoinPrice

    @OptIn(ExperimentalTime::class)
    @Test
    fun toCoinPrice_preservesPriceUsd() {
        val dto = CoinPriceDto(priceUsd = 42_000.0, time = 1_705_320_000_000L)

        val coinPrice = dto.toCoinPrice()

        assertEquals(42_000.0, coinPrice.priceUsd)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun toCoinPrice_convertsEpochMillisToLocalDateTime() {
        val epochMs = 1_705_320_000_000L
        val dto = CoinPriceDto(priceUsd = 0.0, time = epochMs)

        val coinPrice = dto.toCoinPrice()

        val expected =
            Instant
                .fromEpochMilliseconds(epochMs)
                .toLocalDateTime(TimeZone.currentSystemDefault())
        assertEquals(expected, coinPrice.dateTime)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun toCoinPrice_handlesEpochZero() {
        val dto = CoinPriceDto(priceUsd = 100.0, time = 0L)

        val coinPrice = dto.toCoinPrice()

        val expected =
            Instant
                .fromEpochMilliseconds(0L)
                .toLocalDateTime(TimeZone.currentSystemDefault())
        assertEquals(expected, coinPrice.dateTime)
        assertEquals(100.0, coinPrice.priceUsd)
    }

    // endregion
}
