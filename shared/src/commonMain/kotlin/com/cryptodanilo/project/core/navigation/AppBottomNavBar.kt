package com.cryptodanilo.project.core.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cryptodanilo.project.ui.theme.CryptoTrackerTheme
import cryptotrackerdanilo.shared.generated.resources.Res
import cryptotrackerdanilo.shared.generated.resources.btc
import cryptotrackerdanilo.shared.generated.resources.stock
import cryptotrackerdanilo.shared.generated.resources.tab_crypto
import cryptotrackerdanilo.shared.generated.resources.tab_stocks
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private data class BottomTabItem(
    val tab: BottomTab,
    val labelRes: StringResource,
    val icon: DrawableResource,
)

private val bottomTabItems =
    listOf(
        BottomTabItem(tab = BottomTab.CRYPTO, labelRes = Res.string.tab_crypto, icon = Res.drawable.btc),
        BottomTabItem(tab = BottomTab.STOCKS, labelRes = Res.string.tab_stocks, icon = Res.drawable.stock),
    )

@Composable
fun AppBottomNavBar(
    selectedTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        modifier = modifier,
        containerColor = CryptoTrackerTheme.colors.surfaceContainer,
        tonalElevation = 0.dp,
    ) {
        bottomTabItems.forEach { item ->
            val isSelected = item.tab == selectedTab
            val label = stringResource(item.labelRes)
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(item.tab) },
                icon = {
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = label,
                        tint =
                            if (isSelected) {
                                CryptoTrackerTheme.colors.primary
                            } else {
                                CryptoTrackerTheme.colors.onSurfaceVariant
                            },
                        modifier = Modifier.size(CryptoTrackerTheme.sizing.iconMedium),
                    )
                },
                label = {
                    Text(
                        text = label,
                        style = CryptoTrackerTheme.typography.labelSmall,
                        maxLines = 1,
                    )
                },
                colors =
                    NavigationBarItemDefaults.colors(
                        selectedIconColor = CryptoTrackerTheme.colors.primary,
                        selectedTextColor = CryptoTrackerTheme.colors.primary,
                        unselectedIconColor = CryptoTrackerTheme.colors.onSurfaceVariant,
                        unselectedTextColor = CryptoTrackerTheme.colors.onSurfaceVariant,
                        indicatorColor = CryptoTrackerTheme.colors.primary.copy(alpha = 0.12f),
                    ),
            )
        }
    }
}
