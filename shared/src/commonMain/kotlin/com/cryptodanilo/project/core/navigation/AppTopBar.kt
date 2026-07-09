package com.cryptodanilo.project.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.cryptodanilo.project.core.presentation.topbar.TopBarState
import com.cryptodanilo.project.ui.theme.CryptoTrackerTheme
import cryptotrackerdanilo.shared.generated.resources.Res
import cryptotrackerdanilo.shared.generated.resources.go_back
import org.jetbrains.compose.resources.stringResource

@Composable
fun AppTopBar(
    state: TopBarState,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = state.title,
                style = CryptoTrackerTheme.typography.titleLarge,
                color = CryptoTrackerTheme.colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        navigationIcon = {
            if (state.showBackButton) {
                IconButton(onClick = { state.onBack?.invoke() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(Res.string.go_back),
                        tint = CryptoTrackerTheme.colors.onSurfaceVariant,
                    )
                }
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = CryptoTrackerTheme.colors.surface,
                titleContentColor = CryptoTrackerTheme.colors.onSurface,
                navigationIconContentColor = CryptoTrackerTheme.colors.onSurfaceVariant,
            ),
    )
}
