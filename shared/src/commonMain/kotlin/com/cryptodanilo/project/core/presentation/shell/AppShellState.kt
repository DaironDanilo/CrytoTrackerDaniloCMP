package com.cryptodanilo.project.core.presentation.shell

data class AppShellState(
    val title: String = "",
    val showBackButton: Boolean = false,
    val onBack: (() -> Unit)? = null,
    val showTopBar: Boolean = false,
    val showBottomBar: Boolean = true,
)
