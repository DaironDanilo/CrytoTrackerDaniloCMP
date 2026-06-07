package com.cryptodanilo.project.crypto.presentation.coinDetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cryptodanilo.project.crypto.presentation.models.DisplayableNumber
import com.cryptodanilo.project.crypto.presentation.models.MarketUi
import com.cryptodanilo.project.crypto.presentation.models.asDollarString
import com.cryptodanilo.project.crypto.presentation.models.pairWithTradesLine
import com.cryptodanilo.project.crypto.presentation.models.volumeWithPercentLine
import com.cryptodanilo.project.ui.theme.CryptoTrackerTheme
import cryptotrackerdanilo.shared.generated.resources.Res
import cryptotrackerdanilo.shared.generated.resources.market_trades
import cryptotrackerdanilo.shared.generated.resources.market_vol
import org.jetbrains.compose.resources.stringResource

@Composable
fun MarketListItem(
    market: MarketUi,
    isCompact: Boolean,
    modifier: Modifier = Modifier,
) {
    if (isCompact) {
        CompactMarketListItem(market = market, modifier = modifier)
    } else {
        WideMarketListItem(market = market, modifier = modifier)
    }
}

@Composable
private fun CompactMarketListItem(
    market: MarketUi,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RectangleShape)
                .padding(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RankBadge(rank = market.rank)
                Text(
                    text = market.exchangeId.uppercase(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Text(
                text = market.priceUsd.asDollarString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = market.pairWithTradesLine(stringResource(Res.string.market_trades)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = market.volumeWithPercentLine(stringResource(Res.string.market_vol)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = market.updated,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            fontStyle = FontStyle.Italic,
            modifier = Modifier.align(Alignment.End),
        )
    }
}

@Composable
private fun WideMarketListItem(
    market: MarketUi,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "${market.rank}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(32.dp),
            textAlign = TextAlign.End,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Row(
            modifier = Modifier.weight(2f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MonogramChip(exchangeId = market.exchangeId)
            Text(
                text = market.exchangeId.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        Text(
            text = market.pair,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1.5f),
        )
        Text(
            text = market.priceUsd.asDollarString(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(2f),
            textAlign = TextAlign.End,
        )
        Text(
            text = market.volumeUsd24Hr,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(2f),
            textAlign = TextAlign.End,
        )
        Text(
            text = market.tradesCount24Hr,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
        )
        Text(
            text = market.percentExchangeVolume,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
        )
        Text(
            text = market.updated,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1.5f),
            textAlign = TextAlign.End,
        )
    }
}

@Composable
private fun RankBadge(rank: Int) {
    val isTop = rank == 1
    val bgColor = if (isTop) MaterialTheme.colorScheme.primary else Color.Transparent
    val textColor = if (isTop) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    val shape = RoundedCornerShape(4.dp)
    Box(
        modifier =
            Modifier
                .size(24.dp)
                .background(bgColor, shape)
                .then(
                    if (!isTop) {
                        Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
                    } else {
                        Modifier
                    },
                ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "$rank",
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
        )
    }
}

@Composable
private fun MonogramChip(exchangeId: String) {
    val shape = RoundedCornerShape(4.dp)
    Box(
        modifier =
            Modifier
                .size(28.dp)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh, shape)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = exchangeId.take(2).uppercase(),
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp,
        )
    }
}

internal val previewMarket =
    MarketUi(
        rank = 1,
        exchangeId = "binance",
        pair = "ETH/USDT",
        priceUsd = DisplayableNumber(2076.45, "2,076.45"),
        volumeUsd24Hr = "$ 1.2B",
        percentExchangeVolume = "34.2%",
        tradesCount24Hr = "1.4M",
        updated = "Recent",
    )

@Preview(showBackground = true, backgroundColor = 0xFF1C1B1FL, name = "Compact")
@Composable
private fun MarketListItemCompactPreview() {
    CryptoTrackerTheme(darkTheme = true) {
        MarketListItem(market = previewMarket, isCompact = true)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C1B1FL, name = "Wide")
@Composable
private fun MarketListItemWidePreview() {
    CryptoTrackerTheme(darkTheme = true) {
        MarketListItem(market = previewMarket, isCompact = false)
    }
}
