package com.cryptodanilo.project.core.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// PullToRefreshBox is built on touch-gesture detection, which never receives a
// dismissal signal from mouse-driven scrolling — the indicator gets stuck forever
// on desktop and web. Android/iOS get the real gesture; desktop/web fall back to
// the manual refresh button as their primary refresh mechanism.
@Composable
expect fun PullToRefreshWrapper(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier,
    content: @Composable () -> Unit,
)