package com.cryptodanilo.project.core.presentation.shell

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AppShellViewModel : ViewModel() {
    private val _state = MutableStateFlow(AppShellState())
    val state: StateFlow<AppShellState> = _state.asStateFlow()

    fun setListMode(title: String) {
        _state.update {
            AppShellState(
                title = title,
                showBackButton = false,
                onBack = null,
                showTopBar = false,
                showBottomBar = true,
            )
        }
    }

    fun setDetailMode(
        title: String,
        onBack: () -> Unit,
    ) {
        _state.update {
            AppShellState(
                title = title,
                showBackButton = true,
                onBack = onBack,
                showTopBar = true,
                showBottomBar = false,
            )
        }
    }

    fun setPlaceholderMode(title: String) {
        _state.update {
            AppShellState(
                title = title,
                showBackButton = false,
                onBack = null,
                showTopBar = true,
                showBottomBar = true,
            )
        }
    }
}
