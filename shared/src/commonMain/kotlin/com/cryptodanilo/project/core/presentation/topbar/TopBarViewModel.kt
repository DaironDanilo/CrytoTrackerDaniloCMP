package com.cryptodanilo.project.core.presentation.topbar

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TopBarViewModel : ViewModel() {
    private val _state = MutableStateFlow(TopBarState())
    val state: StateFlow<TopBarState> = _state.asStateFlow()

    fun setListMode(title: String) {
        _state.update { TopBarState(title = title, showBackButton = false, onBack = null) }
    }

    fun setDetailMode(
        title: String,
        onBack: () -> Unit,
    ) {
        _state.update { TopBarState(title = title, showBackButton = true, onBack = onBack) }
    }

    fun setPlaceholderMode(title: String) {
        _state.update { TopBarState(title = title, showBackButton = false, onBack = null) }
    }
}
