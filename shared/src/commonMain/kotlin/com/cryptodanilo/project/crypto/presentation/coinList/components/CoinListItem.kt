package com.cryptodanilo.project.crypto.presentation.coinList.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.layout.AnimatedPaneScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import com.cryptodanilo.project.crypto.domain.Coin
import com.cryptodanilo.project.crypto.presentation.models.CoinUi
import com.cryptodanilo.project.crypto.presentation.models.DisplayableNumber
import com.cryptodanilo.project.crypto.presentation.models.toCoinUi
import com.cryptodanilo.project.ui.theme.CryptoTrackerTheme
import com.cryptodanilo.project.ui.theme.CryptoTrackerThemeProvider
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
                .then(
                    if (isSelected) {
                        Modifier
                            .background(CryptoTrackerTheme.colors.surfaceVariant)
                            .clip(RoundedCornerShape(CryptoTrackerTheme.spacing.small))
                    } else {
                        Modifier
                    },
                ).clickable { onItemClick() }
                .padding(CryptoTrackerTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(CryptoTrackerTheme.spacing.medium),
    ) {
        Icon(
            painter = painterResource(coin.iconRes),
            contentDescription = coin.name,
            tint = CryptoTrackerTheme.colors.primary,
            modifier =
                Modifier
                    .size(CryptoTrackerTheme.sizing.coinIconListSize)
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
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = coin.symbol,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor,
            )
            Text(
                text = coin.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                color = contentColor,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "$ ${coin.priceUsd.formatted}",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = contentColor,
            )
            Spacer(modifier = Modifier.height(CryptoTrackerTheme.spacing.small))
            PriceChange(change = coin.changePercent24Hr)
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
