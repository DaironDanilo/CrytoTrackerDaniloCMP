package com.cryptodanilo.project.core.presentation.topbar

data class TopBarState(
    val title: String = "",
    val showBackButton: Boolean = false,
    val onBack: (() -> Unit)? = null,
)
