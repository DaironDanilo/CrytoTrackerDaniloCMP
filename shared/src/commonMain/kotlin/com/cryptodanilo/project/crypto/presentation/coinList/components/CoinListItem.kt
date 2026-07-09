package com.cryptodanilo.project.crypto.presentation.coinList.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.layout.AnimatedPaneScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import com.cryptodanilo.project.core.presentation.util.DisplayableNumber
import com.cryptodanilo.project.core.presentation.util.formatCoinListPrice
import com.cryptodanilo.project.core.presentation.util.formatFullPrice
import com.cryptodanilo.project.crypto.domain.Coin
import com.cryptodanilo.project.crypto.presentation.coinList.components.CoinListConstants.ASSET_COLUMN_WEIGHT
import com.cryptodanilo.project.crypto.presentation.coinList.components.CoinListConstants.CHANGE_COLUMN_WEIGHT
import com.cryptodanilo.project.crypto.presentation.coinList.components.CoinListConstants.PRICE_COLUMN_WEIGHT
import com.cryptodanilo.project.crypto.presentation.coinList.components.CoinListConstants.TREND_COLUMN_WEIGHT
import com.cryptodanilo.project.crypto.presentation.models.CoinUi
import com.cryptodanilo.project.crypto.presentation.models.toCoinUi
import com.cryptodanilo.project.ui.theme.CryptoTrackerTheme
import com.cryptodanilo.project.ui.theme.CryptoTrackerThemeProvider
import cryptotrackerdanilo.shared.generated.resources.Res
import cryptotrackerdanilo.shared.generated.resources.question_sign
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.CoinListItem(
    animatedPaneScope: AnimatedPaneScope? = null,
    coin: CoinUi,
    isSelected: Boolean = false,
    shouldExistSharedElementTransition: Boolean,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentColor =
        if (isSystemInDarkTheme()) {
            Color.White
        } else {
            Color.Black
        }
    Row(
        modifier =
            modifier
                .clip(RoundedCornerShape(CryptoTrackerTheme.spacing.small))
                .then(
                    if (isSelected) {
                        Modifier.background(CryptoTrackerTheme.colors.surfaceVariant.copy(alpha = 0.3f))
                    } else {
                        Modifier
                    },
                ).clickable { onItemClick() }
                .padding(CryptoTrackerTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(ASSET_COLUMN_WEIGHT),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(CryptoTrackerTheme.sizing.coinIconListSize)
                        .border(
                            width = CryptoTrackerTheme.sizing.borderThin,
                            color = CryptoTrackerTheme.colors.primary,
                            shape = CircleShape,
                        ),
                contentAlignment = Alignment.Center,
            ) {
                if (coin.iconRes == Res.drawable.question_sign) {
                    Text(
                        text = coin.symbol.take(1).uppercase(),
                        color = CryptoTrackerTheme.colors.primary,
                        style = CryptoTrackerTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                    )
                } else {
                    Icon(
                        painter = painterResource(coin.iconRes),
                        contentDescription = null,
                        tint = CryptoTrackerTheme.colors.primary,
                        modifier =
                            Modifier
                                .size(CryptoTrackerTheme.sizing.coinIconSize)
                                .conditional(
                                    condition = shouldExistSharedElementTransition && animatedPaneScope != null,
                                    ifTrue = {
                                        sharedElement(
                                            sharedContentState = rememberSharedContentState(key = "image/${coin.id}"),
                                            animatedVisibilityScope = animatedPaneScope!!,
                                            boundsTransform = { _, _ ->
                                                tween(durationMillis = 1000)
                                            },
                                        )
                                    },
                                ),
                    )
                }
            }
            Spacer(modifier = Modifier.size(CryptoTrackerTheme.spacing.small))
            Column {
                Text(
                    text = coin.symbol,
                    style = CryptoTrackerTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                    maxLines = 1,
                )
                Text(
                    text = coin.name,
                    style = CryptoTrackerTheme.typography.labelSmall,
                    fontWeight = FontWeight.Light,
                    color = CryptoTrackerTheme.colors.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        BoxWithConstraints(modifier = Modifier.weight(PRICE_COLUMN_WEIGHT)) {
            // Starting point for where "$ 62,000.00"-style full price text has room
            // vs. needs to fall back to the compact K/M/B format — nudge if a real
            // device/browser shows it flipping too early or too late.
            val compactPriceThreshold = CryptoTrackerTheme.sizing.coinListCompactPriceBreakpoint
            val priceText =
                if (maxWidth < compactPriceThreshold) {
                    coin.priceUsd.value.formatCoinListPrice()
                } else {
                    "$ ${coin.priceUsd.value.formatFullPrice()}"
                }
            Text(
                text = priceText,
                style = CryptoTrackerTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = contentColor,
                textAlign = TextAlign.Start,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Visible,
            )
        }

        InlineSparkline(
            trendPoints = coin.trendPoints,
            changePercent = coin.changePercent24Hr.value,
            modifier =
                Modifier
                    .weight(TREND_COLUMN_WEIGHT)
                    .height(CryptoTrackerTheme.sizing.coinListTrendColumnHeight)
                    .padding(horizontal = CryptoTrackerTheme.spacing.medium),
        )

        Box(
            modifier =
                Modifier
                    .weight(CHANGE_COLUMN_WEIGHT)
                    // Guarantees the column is always wide enough for the full chip
                    // ("+2.50%") even when 20% of a narrow row would otherwise clip it.
                    .widthIn(min = CryptoTrackerTheme.sizing.coinListChangeChipMinWidth),
            contentAlignment = Alignment.CenterEnd,
        ) {
            PriceChange(
                change = coin.changePercent24Hr,
                modifier =
                    Modifier.semantics {
                        contentDescription = "${coin.changePercent24Hr.formatted}%, 24h change"
                    },
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(showBackground = true, backgroundColor = 0xFF1C1B1FL, name = "Dark - Selected")
@Composable
private fun CoinListItemSelectedPreview() {
    CryptoTrackerThemeProvider(darkTheme = true) {
        SharedTransitionLayout {
            CoinListItem(
                coin = previewCoin,
                isSelected = true,
                shouldExistSharedElementTransition = false,
                onItemClick = {},
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(showBackground = true, name = "Light")
@Composable
private fun CoinListItemLightPreview() {
    CryptoTrackerThemeProvider(darkTheme = false) {
        SharedTransitionLayout {
            CoinListItem(
                coin = previewCoin,
                shouldExistSharedElementTransition = false,
                onItemClick = {},
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(showBackground = true, backgroundColor = 0xFF1C1B1FL, name = "Dark - Negative change")
@Composable
private fun CoinListItemDarkPreview() {
    CryptoTrackerThemeProvider(darkTheme = true) {
        SharedTransitionLayout {
            CoinListItem(
                coin =
                    previewCoin.copy(
                        changePercent24Hr = DisplayableNumber(-3.45, "-3.45"),
                    ),
                shouldExistSharedElementTransition = false,
                onItemClick = {},
            )
        }
    }
}

internal val previewCoin =
    Coin(
        id = "bitcoin",
        rank = 1,
        symbol = "BTC",
        name = "Bitcoin",
        marketCapUsd = 1241273958896.68,
        priceUsd = 62828.54,
        changePercent24Hr = 0.1,
    ).toCoinUi()

@Composable
expect fun getScreenSize(): IntSize

inline fun Modifier.conditional(
    condition: Boolean,
    ifTrue: Modifier.() -> Modifier,
    ifFalse: Modifier.() -> Modifier = { this },
): Modifier =
    if (condition) {
        then(ifTrue(Modifier))
    } else {
        then(ifFalse(Modifier))
    }
