package com.cryptodanilo.project.core.navigation

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.window.core.layout.WindowSizeClass
import com.cryptodanilo.project.core.presentation.shell.AppShellViewModel
import com.cryptodanilo.project.ui.theme.CryptoTrackerTheme
import cryptotrackerdanilo.shared.generated.resources.Res
import cryptotrackerdanilo.shared.generated.resources.tab_crypto
import cryptotrackerdanilo.shared.generated.resources.tab_stocks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private data class BottomTabItem(
    val tab: BottomTab,
    val labelRes: StringResource,
    val icon: ImageVector,
)

// Defined as Kotlin ImageVectors rather than composeResources XML drawables — see
// BoldNavIcons.kt for why (the wasmJs target silently fails to resolve newly-added XML vector
// resources at runtime; ImageVector is compiled code, not a runtime-fetched asset, so it works
// identically on every target).
private val bottomTabItems =
    listOf(
        BottomTabItem(tab = BottomTab.CRYPTO, labelRes = Res.string.tab_crypto, icon = BtcBoldIcon),
        BottomTabItem(tab = BottomTab.STOCKS, labelRes = Res.string.tab_stocks, icon = StockBoldIcon),
    )

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun RootScreen(
    modifier: Modifier = Modifier,
    onBackNavigableChanged: (canNavigateBack: Boolean) -> Unit = {},
    backRequests: Flow<Unit> = emptyFlow(),
) {
    val topBarViewModel: AppShellViewModel = koinViewModel()
    val topBarState by topBarViewModel.state.collectAsStateWithLifecycle()

    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    val isExpanded = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
    val isMedium =
        !isExpanded && windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
    val isCompact = !isExpanded && !isMedium

    // One back stack per tab — preserves each tab's own navigation state when switching.
    val cryptoBackStack = rememberNavBackStack(appNavSavedStateConfig, CoinList)
    val stocksBackStack = rememberNavBackStack(appNavSavedStateConfig, StocksTab)

    var selectedTab by remember { mutableStateOf(BottomTab.CRYPTO) }

    // showBottomBar is only false when the shell is in compact detail mode (see
    // AppShellViewModel.setDetailMode) — on medium the rail always stays; expanded renders its
    // own always-visible panel below, bypassing NavigationSuiteScaffold entirely (see comment there).
    val navigationSuiteLayoutType =
        when {
            !topBarState.showBottomBar -> NavigationSuiteType.None
            isMedium -> NavigationSuiteType.NavigationRail
            else -> NavigationSuiteType.NavigationBar
        }

    // navigationSuiteItems/the expanded panel below are plain (non-@Composable) DSL lambdas, so
    // resource lookups must happen here, in composable context, and be captured by item lambdas.
    val tabLabels = bottomTabItems.associate { it.tab to stringResource(it.labelRes) }

    // Selected icon tint: white/black by theme rather than the onPrimary token. The indicator
    // pill is only 30%-alpha primary blended over surfaceContainer (see below), so its actual
    // rendered color skews dark in dark theme and light in light theme — onPrimary is tuned for
    // contrast against solid primary, not that blend, so it reads poorly against the pill as
    // currently drawn. Applied identically to the bottom bar, rail, and the expanded panel.
    val selectedIconTint = if (isSystemInDarkTheme()) Color.White else Color.Black

    // movableContentOf, not a plain lambda: this same content is invoked from two structurally
    // different call sites below (a plain Box on the expanded layout vs. NavigationSuiteScaffold's
    // content slot otherwise). Compose identifies a subtree by its position in the composition, not
    // by lambda identity, so crossing the isExpanded breakpoint would otherwise tear down and
    // recreate everything underneath — including CoinListViewModel, silently resetting the selected
    // coin back to the list's first entry. movableContentOf relocates the existing composition (and
    // ViewModel store) instead of disposing it.
    val screenContent =
        remember {
            movableContentOf {
                Scaffold(
                    topBar = {
                        // On compact, top bar visibility follows the shell state (hidden on the list,
                        // shown with a back button on the detail pane). On medium/expanded, only show it
                        // when there's an actual title to display — the list screen's title is always
                        // empty (see AppShellViewModel.setListMode), so no title ever appears above the
                        // search bar/coin list there; the detail screen's title is the coin name.
                        val showTopBar = if (isCompact) topBarState.showTopBar else topBarState.title.isNotEmpty()
                        if (showTopBar) {
                            AppTopBar(state = topBarState)
                        }
                    },
                    containerColor = CryptoTrackerTheme.colors.background,
                ) { innerPadding ->
                    // Crossfade (not the default show/hide) so tab switches are a plain 200ms fade —
                    // AnimatedVisibility's default expand/shrink spec animates size and reads as a
                    // diagonal slide, which is wrong for tab switching. `visible = true` is passed to
                    // both NavDisplays unconditionally: Crossfade alone decides which is on screen, and
                    // since it never flips back to false, the inner AnimatedVisibility never re-triggers.
                    Crossfade(
                        targetState = selectedTab,
                        animationSpec = tween(durationMillis = 200, easing = LinearEasing),
                        modifier = Modifier.padding(innerPadding).fillMaxSize(),
                    ) { tab ->
                        when (tab) {
                            BottomTab.CRYPTO ->
                                CryptoNavDisplay(
                                    backStack = cryptoBackStack,
                                    topBarViewModel = topBarViewModel,
                                    visible = true,
                                    onBackNavigableChanged = onBackNavigableChanged,
                                    backRequests = backRequests,
                                )
                            BottomTab.STOCKS ->
                                StocksNavDisplay(
                                    backStack = stocksBackStack,
                                    topBarViewModel = topBarViewModel,
                                    visible = true,
                                )
                        }
                    }
                }
            }
        }

    if (isExpanded) {
        // NavigationSuiteType.NavigationDrawer renders via PermanentDrawerSheet, which hardcodes
        // a 240–360dp width floor with no public override — too wide for two short tab items. A
        // plain always-visible panel is used instead so the rail hugs the icon+label content,
        // keeping the same surfaceContainer background and full-height "always on" drawer feel.
        Row(modifier = modifier.fillMaxSize()) {
            Column(
                modifier =
                    Modifier
                        .fillMaxHeight()
                        .wrapContentWidth()
                        .background(CryptoTrackerTheme.colors.surfaceContainer)
                        .padding(vertical = CryptoTrackerTheme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(CryptoTrackerTheme.spacing.small),
            ) {
                bottomTabItems.forEach { tabItem ->
                    val isSelected = tabItem.tab == selectedTab
                    val label = tabLabels.getValue(tabItem.tab)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(CryptoTrackerTheme.spacing.small),
                        modifier =
                            Modifier
                                .padding(horizontal = CryptoTrackerTheme.spacing.small)
                                .clip(RoundedCornerShape(CryptoTrackerTheme.sizing.cornerFull))
                                .clickable { selectedTab = tabItem.tab }
                                .padding(
                                    horizontal = CryptoTrackerTheme.spacing.medium,
                                    vertical = CryptoTrackerTheme.spacing.small,
                                ),
                    ) {
                        // The indicator pill wraps only the icon — same as the stock
                        // NavigationBarItem/NavigationRailItem — so the label (kept on the
                        // panel's own background) stays legible once the indicator is solid.
                        // Padding is horizontal-medium/vertical-extraSmall so the wrapped box
                        // comes out 56x32 (24dp icon + padding) — the same fixed pill dimensions
                        // NavigationBarItem/NavigationRailItem use — instead of a near-square box
                        // that the same cornerFull radius would round into a circle.
                        Box(
                            modifier =
                                Modifier
                                    .clip(RoundedCornerShape(CryptoTrackerTheme.sizing.cornerFull))
                                    .background(
                                        if (isSelected) {
                                            CryptoTrackerTheme.colors.primary.copy(alpha = 0.3f)
                                        } else {
                                            CryptoTrackerTheme.colors.surfaceContainer
                                        },
                                    ),
                        ) {
                            Icon(
                                imageVector = tabItem.icon,
                                contentDescription = label,
                                tint =
                                    if (isSelected) {
                                        selectedIconTint
                                    } else {
                                        CryptoTrackerTheme.colors.onSurfaceVariant
                                    },
                                modifier =
                                    Modifier
                                        .padding(
                                            horizontal = CryptoTrackerTheme.spacing.medium,
                                            vertical = CryptoTrackerTheme.spacing.extraSmall,
                                        ).size(CryptoTrackerTheme.sizing.iconMedium),
                            )
                        }
                        Text(
                            text = label,
                            style = CryptoTrackerTheme.typography.labelSmall,
                            maxLines = 1,
                            softWrap = false,
                            color =
                                if (isSelected) {
                                    CryptoTrackerTheme.colors.primary
                                } else {
                                    CryptoTrackerTheme.colors.onSurfaceVariant
                                },
                        )
                    }
                }
            }
            Box(modifier = Modifier.weight(1f)) {
                screenContent()
            }
        }
    } else {
        // Semi-transparent primary indicator behind the selected icon — a subtle pill rather than
        // a harsh solid fill — with the theme-adaptive selectedIconTint on top for contrast.
        // Applied identically to both the bottom bar and the rail.
        val selectedItemColors =
            NavigationSuiteDefaults.itemColors(
                navigationBarItemColors =
                    NavigationBarItemDefaults.colors(
                        selectedIconColor = selectedIconTint,
                        selectedTextColor = CryptoTrackerTheme.colors.primary,
                        unselectedIconColor = CryptoTrackerTheme.colors.onSurfaceVariant,
                        unselectedTextColor = CryptoTrackerTheme.colors.onSurfaceVariant,
                        indicatorColor = CryptoTrackerTheme.colors.primary.copy(alpha = 0.3f),
                    ),
                navigationRailItemColors =
                    NavigationRailItemDefaults.colors(
                        selectedIconColor = selectedIconTint,
                        selectedTextColor = CryptoTrackerTheme.colors.primary,
                        unselectedIconColor = CryptoTrackerTheme.colors.onSurfaceVariant,
                        unselectedTextColor = CryptoTrackerTheme.colors.onSurfaceVariant,
                        indicatorColor = CryptoTrackerTheme.colors.primary.copy(alpha = 0.3f),
                    ),
            )

        NavigationSuiteScaffold(
            modifier = modifier,
            layoutType = navigationSuiteLayoutType,
            navigationSuiteItems = {
                bottomTabItems.forEach { tabItem ->
                    val isSelected = tabItem.tab == selectedTab
                    val label = tabLabels.getValue(tabItem.tab)
                    item(
                        selected = isSelected,
                        onClick = { selectedTab = tabItem.tab },
                        icon = {
                            Icon(
                                imageVector = tabItem.icon,
                                contentDescription = label,
                                tint =
                                    if (isSelected) {
                                        selectedIconTint
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
                        colors = selectedItemColors,
                    )
                }
            },
            navigationSuiteColors =
                NavigationSuiteDefaults.colors(
                    navigationBarContainerColor = CryptoTrackerTheme.colors.surfaceContainer,
                    navigationRailContainerColor = CryptoTrackerTheme.colors.surfaceContainer,
                ),
            containerColor = CryptoTrackerTheme.colors.background,
        ) {
            screenContent()
        }
    }
}
