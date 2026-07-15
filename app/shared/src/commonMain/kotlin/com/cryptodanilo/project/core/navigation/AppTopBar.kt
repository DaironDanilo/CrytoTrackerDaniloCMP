package com.cryptodanilo.project.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.cryptodanilo.project.core.presentation.shell.AppShellState
import com.cryptodanilo.project.ui.theme.CryptoTrackerTheme
import cryptotrackerdanilo.shared.generated.resources.Res
import cryptotrackerdanilo.shared.generated.resources.go_back
import org.jetbrains.compose.resources.stringResource

@Composable
fun AppTopBar(
    state: AppShellState,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = state.title,
                style =
                    CryptoTrackerTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp,
                    ),
                color = CryptoTrackerTheme.colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        navigationIcon = {
            // showBackButton already accounts for two-pane layouts (see
            // AppShellViewModel.setDetailMode) — it's only true when the detail pane is showing
            // without the list alongside it.
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
        actions = {
            // Balances the navigationIcon's IconButton so the title's own fillMaxWidth/
            // TextAlign.Center is centered within the whole bar, not just the space left of it.
            if (state.showBackButton) {
                Box(modifier = Modifier.size(CryptoTrackerTheme.sizing.touchTargetMinimum))
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
