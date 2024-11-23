package com.cryptodanilo.project.crypto.presentation.coin_list.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.layout.AnimatedPaneScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cryptodanilo.project.crypto.domain.Coin
import com.cryptodanilo.project.crypto.presentation.models.CoinUi
import com.cryptodanilo.project.crypto.presentation.models.toCoinUi
import org.jetbrains.compose.resources.painterResource


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.CoinListItem(
    animatedPaneScope: AnimatedPaneScope,
    coin: CoinUi,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentColor = if (isSystemInDarkTheme()) {
        Color.White
    } else {
        Color.Black
    }
    Row(
        modifier = modifier
            .clickable { onItemClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            painter = painterResource(coin.iconRes),
            contentDescription = coin.name,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(85.dp)
                .putIfPortrait(
                    Modifier.sharedElement(
                        state = rememberSharedContentState(key = "image/${coin.id}"),
                        animatedVisibilityScope = animatedPaneScope,
                        boundsTransform = { _, _ ->
                            tween(durationMillis = 1000)
                        }
                    )
                )
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = coin.symbol,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Text(
                text = coin.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                color = contentColor
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "$ ${coin.priceUsd.formatted}",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            PriceChange(change = coin.changePercent24Hr)
        }
    }
}

//@OptIn(ExperimentalSharedTransitionApi::class)
//@PreviewLightDark
//@Composable
//fun CoinListItemPreview() {
//    CryptoTrackerTheme {
//        SharedTransitionLayout {
//            AnimatedVisibility(visible = true) {
//                CoinListItem(
//                    animatedPaneScope = this as AnimatedPaneScope,
//                    coin = previewCoin,
//                    onItemClick = {},
//                    modifier = Modifier.background(MaterialTheme.colorScheme.background)
//                )
//            }
//        }
//    }
//}

internal val previewCoin = Coin(
    id = "bitcoin",
    rank = 1,
    symbol = "BTC",
    name = "Bitcoin",
    marketCapUsd = 1241273958896.68,
    priceUsd = 62828.54,
    changePercent24Hr = 0.1
).toCoinUi()

@Composable
expect fun getScreenSize(): IntSize

fun Modifier.putIfPortrait(modifier: Modifier): Modifier = composed {
    // Get the current window size
    val windowSize = getScreenSize()
    // Check if the height is greater than the width (portrait) or width is greater (landscape)
    if (windowSize.height > windowSize.width) {
        this.then(modifier) // Portrait
    } else {
        this // Landscape or other
    }
}
