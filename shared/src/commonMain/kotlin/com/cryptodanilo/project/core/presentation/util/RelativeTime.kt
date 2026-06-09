package com.cryptodanilo.project.core.presentation.util

import com.cryptodanilo.project.core.util.MILLIS_PER_DAY
import com.cryptodanilo.project.core.util.MILLIS_PER_HOUR
import com.cryptodanilo.project.core.util.MILLIS_PER_MINUTE
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun Long.toRelativeTimeString(): String {
    val diff = Clock.System.now().toEpochMilliseconds() - this
    return when {
        diff < MILLIS_PER_MINUTE -> "Recent"
        diff < MILLIS_PER_HOUR -> "${diff / MILLIS_PER_MINUTE}m ago"
        diff < MILLIS_PER_DAY -> "${diff / MILLIS_PER_HOUR}h ago"
        else -> "${diff / MILLIS_PER_DAY}d ago"
    }
}
