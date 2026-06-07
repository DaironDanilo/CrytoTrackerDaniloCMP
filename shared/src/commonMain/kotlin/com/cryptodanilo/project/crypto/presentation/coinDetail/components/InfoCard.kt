package com.cryptodanilo.project.crypto.presentation.coinDetail.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
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
                .padding(CryptoTrackerTheme.spacing.small)
                .shadow(
                    elevation = CryptoTrackerTheme.sizing.cardElevation,
                    shape = RectangleShape,
                    ambientColor = CryptoTrackerTheme.colors.primary,
                    spotColor = CryptoTrackerTheme.colors.primary,
                ),
        shape = RectangleShape,
        border = BorderStroke(CryptoTrackerTheme.sizing.borderThin, CryptoTrackerTheme.colors.primary),
        colors =
            CardDefaults.cardColors(
                containerColor = CryptoTrackerTheme.colors.surfaceContainer,
                contentColor = contentColor,
            ),
    ) {
        AnimatedContent(
            targetState = icon,
            label = "iconAnimation",
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) { icon ->
            Icon(
                painter = painterResource(icon),
                contentDescription = title,
                tint = contentColor,
                modifier =
                    Modifier
                        .size(CryptoTrackerTheme.sizing.infoCardIconSize)
                        .padding(top = CryptoTrackerTheme.sizing.infoCardInnerPadding),
            )
        }
        Spacer(modifier = Modifier.size(CryptoTrackerTheme.spacing.small))
        AnimatedContent(
            targetState = formattedValue,
            label = "valueAnimation",
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) { formattedValue ->
            Text(
                text = formattedValue,
                style = formattedTextStyle,
                modifier = Modifier.padding(horizontal = CryptoTrackerTheme.spacing.medium),
            )
        }
        Spacer(modifier = Modifier.size(CryptoTrackerTheme.spacing.small))
        Text(
            text = title,
            textAlign = TextAlign.Center,
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = CryptoTrackerTheme.spacing.medium)
                    .padding(bottom = CryptoTrackerTheme.sizing.infoCardInnerPadding),
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            color = contentColor,
        )
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