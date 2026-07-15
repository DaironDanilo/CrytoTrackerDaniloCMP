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
                // The list screen never has a title, on any screen size.
                title = "",
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
        isCompact: Boolean,
        isTwoPane: Boolean,
    ) {
        _state.update {
            AppShellState(
                title = title,
                // The back button only makes sense when the list isn't visible alongside the
                // detail pane — in a two-pane layout the list is already on screen.
                showBackButton = !isTwoPane,
                onBack = onBack,
                showTopBar = true,
                // On compact, the detail pane replaces the list, so the bottom nav hides.
                // On medium/expanded, the nav rail/drawer stays put alongside both panes.
                showBottomBar = !isCompact,
                isTwoPane = isTwoPane,
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
