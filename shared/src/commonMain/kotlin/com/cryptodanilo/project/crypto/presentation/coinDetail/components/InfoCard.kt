package com.cryptodanilo.project.crypto.presentation.coinDetail.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.cryptodanilo.project.ui.theme.CryptoTrackerTheme
import com.cryptodanilo.project.ui.theme.CryptoTrackerThemeProvider
import cryptotrackerdanilo.shared.generated.resources.Res
import cryptotrackerdanilo.shared.generated.resources.dollar
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun InfoCard(
    title: String,
    formattedValue: String,
    icon: DrawableResource,
    modifier: Modifier = Modifier,
    contentColor: Color = CryptoTrackerTheme.colors.onSurface,
    accentColor: Color = CryptoTrackerTheme.colors.primary,
    formattedTextStyle: TextStyle =
        LocalTextStyle.current.copy(
            color = contentColor,
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
        ),
) {
    Card(
        modifier =
            modifier
                .padding(CryptoTrackerTheme.spacing.extraSmall)
                .shadow(
                    elevation = CryptoTrackerTheme.sizing.cardElevation,
                    shape = RectangleShape,
                    ambientColor = accentColor,
                    spotColor = accentColor,
                ),
        shape = RectangleShape,
        border = BorderStroke(CryptoTrackerTheme.sizing.borderThin, accentColor),
        colors =
            CardDefaults.cardColors(
                containerColor = CryptoTrackerTheme.colors.surfaceContainer,
                contentColor = contentColor,
            ),
    ) {
        Row(
            modifier = Modifier.padding(CryptoTrackerTheme.sizing.infoCardInnerPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(CryptoTrackerTheme.spacing.extraSmall),
        ) {
            AnimatedContent(
                targetState = icon,
                label = "iconAnimation",
            ) { icon ->
                Icon(
                    painter = painterResource(icon),
                    contentDescription = title,
                    tint = contentColor,
                    modifier = Modifier.size(CryptoTrackerTheme.sizing.infoCardIconSize),
                )
            }
            Column(
                modifier = Modifier.padding(start = CryptoTrackerTheme.spacing.extraSmall),
            ) {
                AnimatedContent(
                    targetState = formattedValue,
                    label = "valueAnimation",
                ) { formattedValue ->
                    Text(
                        text = formattedValue,
                        style = formattedTextStyle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Text(
                    text = title,
                    textAlign = TextAlign.Start,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Light")
@Composable
private fun InfoCardLightPreview() {
    CryptoTrackerThemeProvider(darkTheme = false) {
        InfoCard(
            title = "Market Cap",
            formattedValue = "$ 1,241,273,958,896.68",
            icon = Res.drawable.dollar,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C1B1FL, name = "Dark - Error color")
@Composable
private fun InfoCardDarkPreview() {
    CryptoTrackerThemeProvider(darkTheme = true) {
        InfoCard(
            title = "Change (24h)",
            formattedValue = "-3.45",
            icon = Res.drawable.dollar,
        )
    }
}
