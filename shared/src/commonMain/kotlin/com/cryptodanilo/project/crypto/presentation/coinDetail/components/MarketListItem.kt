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
import androidx.compose.ui.unit.sp
import com.cryptodanilo.project.crypto.presentation.models.DisplayableNumber
import com.cryptodanilo.project.crypto.presentation.models.MarketUi
import com.cryptodanilo.project.crypto.presentation.models.asDollarString
import com.cryptodanilo.project.crypto.presentation.models.pairWithTradesLine
import com.cryptodanilo.project.crypto.presentation.models.volumeWithPercentLine
import com.cryptodanilo.project.ui.theme.CryptoTrackerTheme
import com.cryptodanilo.project.ui.theme.CryptoTrackerThemeProvider
import cryptotrackerdanilo.shared.generated.resources.Res
import cryptotrackerdanilo.shared.generated.resources.market_trades
import cryptotrackerdanilo.shared.generated.resources.market_vol
import org.jetbrains.compose.resources.stringResource

@Composable
fun MarketListItem(
    rank: Int,
    market: MarketUi,
    isCompact: Boolean,
    modifier: Modifier = Modifier,
) {
    if (isCompact) {
        CompactMarketListItem(rank = rank, market = market, modifier = modifier)
    } else {
        WideMarketListItem(rank = rank, market = market, modifier = modifier)
    }
}

@Composable
private fun CompactMarketListItem(
    rank: Int,
    market: MarketUi,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = CryptoTrackerTheme.spacing.small, vertical = CryptoTrackerTheme.spacing.extraSmall)
                .border(CryptoTrackerTheme.sizing.borderThin, CryptoTrackerTheme.colors.outlineVariant, RectangleShape)
                .padding(CryptoTrackerTheme.sizing.marketItemInnerPadding),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(CryptoTrackerTheme.spacing.small),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RankBadge(rank = rank)
                Text(
                    text = market.exchangeId.uppercase(),
                    style = CryptoTrackerTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = CryptoTrackerTheme.colors.onSurface,
                )
            }
            Text(
                text = market.priceUsd.asDollarString(),
                style = CryptoTrackerTheme.typography.bodyMedium,
                color = CryptoTrackerTheme.colors.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.height(CryptoTrackerTheme.spacing.extraSmall))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = market.pairWithTradesLine(stringResource(Res.string.market_trades)),
                style = CryptoTrackerTheme.typography.bodySmall,
                color = CryptoTrackerTheme.colors.onSurfaceVariant,
            )
            Text(
                text = market.volumeWithPercentLine(stringResource(Res.string.market_vol)),
                style = CryptoTrackerTheme.typography.bodySmall,
                color = CryptoTrackerTheme.colors.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.height(CryptoTrackerTheme.sizing.marketItemSubRowSpacing))
        Text(
            text = market.updated,
            style = CryptoTrackerTheme.typography.bodySmall,
            color = CryptoTrackerTheme.colors.onSurfaceVariant.copy(alpha = 0.6f),
            fontStyle = FontStyle.Italic,
            modifier = Modifier.align(Alignment.End),
        )
    }
}

@Composable
private fun WideMarketListItem(
    rank: Int,
    market: MarketUi,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(
                    horizontal = CryptoTrackerTheme.spacing.medium,
                    vertical = CryptoTrackerTheme.sizing.marketItemInnerPadding,
                ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "$rank",
            style = CryptoTrackerTheme.typography.bodySmall,
            color = CryptoTrackerTheme.colors.onSurfaceVariant,
            modifier = Modifier.width(CryptoTrackerTheme.sizing.rankColumnWidth),
            textAlign = TextAlign.End,
        )
        Spacer(modifier = Modifier.width(CryptoTrackerTheme.sizing.rankExchangeSpacing))
        Row(
            modifier = Modifier.weight(2f),
            horizontalArrangement = Arrangement.spacedBy(CryptoTrackerTheme.spacing.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MonogramChip(exchangeId = market.exchangeId)
            Text(
                text = market.exchangeId.replaceFirstChar { it.uppercase() },
                style = CryptoTrackerTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = CryptoTrackerTheme.colors.onSurface,
            )
        }
        Text(
            text = market.pair,
            style = CryptoTrackerTheme.typography.bodySmall,
            color = CryptoTrackerTheme.colors.onSurfaceVariant,
            modifier = Modifier.weight(1.5f),
        )
        Text(
            text = market.priceUsd.asDollarString(),
            style = CryptoTrackerTheme.typography.bodySmall,
            color = CryptoTrackerTheme.colors.onSurface,
            modifier = Modifier.weight(2f),
            textAlign = TextAlign.End,
        )
        Text(
            text = market.volumeUsd24Hr,
            style = CryptoTrackerTheme.typography.bodySmall,
            color = CryptoTrackerTheme.colors.onSurface,
            modifier = Modifier.weight(2f),
            textAlign = TextAlign.End,
        )
        Text(
            text = market.tradesCount24Hr,
            style = CryptoTrackerTheme.typography.bodySmall,
            color = CryptoTrackerTheme.colors.onSurface,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
        )
        Text(
            text = market.percentExchangeVolume,
            style = CryptoTrackerTheme.typography.bodySmall,
            color = CryptoTrackerTheme.colors.onSurface,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
        )
        Text(
            text = market.updated,
            style = CryptoTrackerTheme.typography.bodySmall,
            color = CryptoTrackerTheme.colors.onSurfaceVariant,
            modifier = Modifier.weight(1.5f),
            textAlign = TextAlign.End,
        )
    }
}

@Composable
private fun RankBadge(rank: Int) {
    val isTop = rank == 1
    val bgColor = if (isTop) CryptoTrackerTheme.colors.primary else Color.Transparent
    val textColor =
        if (isTop) CryptoTrackerTheme.colors.onPrimary else CryptoTrackerTheme.colors.onSurfaceVariant
    val shape = RoundedCornerShape(CryptoTrackerTheme.sizing.cornerSmall)
    Box(
        modifier =
            Modifier
                .size(CryptoTrackerTheme.sizing.iconMedium)
                .background(bgColor, shape)
                .then(
                    if (!isTop) {
                        Modifier.border(
                            CryptoTrackerTheme.sizing.borderThin,
                            CryptoTrackerTheme.colors.outlineVariant,
                            shape,
                        )
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
    val shape = RoundedCornerShape(CryptoTrackerTheme.sizing.cornerSmall)
    Box(
        modifier =
            Modifier
                .size(CryptoTrackerTheme.sizing.exchangeChipSize)
                .background(CryptoTrackerTheme.colors.surfaceContainerHigh, shape)
                .border(CryptoTrackerTheme.sizing.borderThin, CryptoTrackerTheme.colors.outlineVariant, shape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = exchangeId.take(2).uppercase(),
            color = CryptoTrackerTheme.colors.onSurface,
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
    CryptoTrackerThemeProvider(darkTheme = true) {
        MarketListItem(rank = 1, market = previewMarket, isCompact = true)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C1B1FL, name = "Wide")
@Composable
private fun MarketListItemWidePreview() {
    CryptoTrackerThemeProvider(darkTheme = true) {
        MarketListItem(rank = 1, market = previewMarket, isCompact = false)
    }
}
