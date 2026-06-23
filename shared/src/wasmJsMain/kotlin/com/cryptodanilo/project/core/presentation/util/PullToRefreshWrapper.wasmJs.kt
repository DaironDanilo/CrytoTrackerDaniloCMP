package com.cryptodanilo.project.core.presentation.util

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// Desktop browsers are mouse-driven, and PullToRefreshBox's touch-gesture
// detection gets stuck mid-refresh when driven by mouse scroll — the indicator
// never dismisses. The manual [↺] button is the primary refresh mechanism on
// web for both desktop and mobile browsers.
@Composable
actual fun PullToRefreshWrapper(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        content()
    }
}