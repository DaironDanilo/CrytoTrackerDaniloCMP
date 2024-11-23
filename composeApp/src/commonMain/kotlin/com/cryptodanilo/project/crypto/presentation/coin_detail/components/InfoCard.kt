package com.cryptodanilo.project.crypto.presentation.coin_detail.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun InfoCard(
    title: String,
    formattedValue: String,
    icon: DrawableResource,
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    formattedTextStyle: TextStyle = LocalTextStyle.current.copy(
        color = contentColor,
        textAlign = TextAlign.Center,
        fontSize = 18.sp
    ),
) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .shadow(
                elevation = 15.dp,
                shape = RectangleShape,
                ambientColor = MaterialTheme.colorScheme.primary,
                spotColor = MaterialTheme.colorScheme.primary
            ),
        shape = RectangleShape,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = contentColor
        )
    ) {
        AnimatedContent(
            targetState = icon,
            label = "iconAnimation",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) { icon ->
            Icon(
                painter = painterResource(icon),
                contentDescription = title,
                tint = contentColor,
                modifier = Modifier
                    .size(75.dp)
                    .padding(top = 16.dp)
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
        AnimatedContent(
            targetState = formattedValue,
            label = "valueAnimation",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) { formattedValue ->
            Text(
                text = formattedValue,
                style = formattedTextStyle,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = title,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            color = contentColor
        )
    }
}

//@PreviewLightDark
//@Composable
//fun InfoCardPreview() {
//    CryptoTrackerTheme {
//        InfoCard(
//            title = "Market Cap",
//            formattedValue = "$ 63,000.000",
//            icon = ImageVector.vectorResource(id = R.drawable.dollar),
//        )
//    }
//}
