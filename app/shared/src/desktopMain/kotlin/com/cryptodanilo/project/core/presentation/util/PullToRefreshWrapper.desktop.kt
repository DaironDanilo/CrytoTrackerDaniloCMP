package com.cryptodanilo.project.core.presentation.util

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// Desktop is mouse-driven — pull to refresh isn't applicable here. The manual
// [↺] button is the primary refresh mechanism.
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
