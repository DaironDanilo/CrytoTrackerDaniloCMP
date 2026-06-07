package com.cryptodanilo.project.core.presentation.util

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun Long.toRelativeTimeString(): String {
    val diff = Clock.System.now().toEpochMilliseconds() - this
    return when {
        diff < 60_000L -> "Recent"
        diff < 3_600_000L -> "${diff / 60_000}m ago"
        diff < 86_400_000L -> "${diff / 3_600_000}h ago"
        else -> "${diff / 86_400_000}d ago"
    }
}
