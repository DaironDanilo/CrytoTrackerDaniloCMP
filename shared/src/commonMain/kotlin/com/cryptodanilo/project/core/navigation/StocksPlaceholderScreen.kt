package com.cryptodanilo.project.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.cryptodanilo.project.core.presentation.shell.AppShellViewModel
import com.cryptodanilo.project.ui.theme.CryptoTrackerTheme
import cryptotrackerdanilo.shared.generated.resources.Res
import cryptotrackerdanilo.shared.generated.resources.stocks_coming_soon
import cryptotrackerdanilo.shared.generated.resources.tab_stocks
import org.jetbrains.compose.resources.stringResource

@Composable
fun StocksPlaceholderScreen(
    topBarViewModel: AppShellViewModel,
    modifier: Modifier = Modifier,
) {
    val tabTitle = stringResource(Res.string.tab_stocks)
    LaunchedEffect(tabTitle) {
        topBarViewModel.setPlaceholderMode(tabTitle)
    }
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(Res.string.stocks_coming_soon),
            style = CryptoTrackerTheme.typography.titleMedium,
            color = CryptoTrackerTheme.colors.onSurfaceVariant,
        )
    }
}
