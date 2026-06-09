package com.cryptodanilo.project.core.util

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal fun getCurrentTimeMs(): Long = Clock.System.now().toEpochMilliseconds()
