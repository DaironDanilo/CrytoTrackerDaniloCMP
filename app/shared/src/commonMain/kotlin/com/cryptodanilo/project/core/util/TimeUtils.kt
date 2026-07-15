package com.cryptodanilo.project.core.util

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal const val MILLIS_PER_MINUTE = 60_000L
internal const val MILLIS_PER_HOUR = 3_600_000L
internal const val MILLIS_PER_DAY = 86_400_000L

@OptIn(ExperimentalTime::class)
internal fun getCurrentTimeMs(): Long = Clock.System.now().toEpochMilliseconds()
