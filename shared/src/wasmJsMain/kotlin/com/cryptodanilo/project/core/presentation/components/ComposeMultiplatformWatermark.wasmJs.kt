package com.cryptodanilo.project.core.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextDecoration
import com.cryptodanilo.project.ui.theme.CryptoTrackerTheme
import cryptotrackerdanilo.shared.generated.resources.Res
import cryptotrackerdanilo.shared.generated.resources.built_with
import cryptotrackerdanilo.shared.generated.resources.compose_multiplatform
import cryptotrackerdanilo.shared.generated.resources.compose_multiplatform_name
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun ComposeMultiplatformWatermark() {
    val uriHandler = LocalUriHandler.current
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd,
    ) {
        Row(
            modifier =
                Modifier
                    .padding(CryptoTrackerTheme.spacing.medium)
                    .clip(RoundedCornerShape(CryptoTrackerTheme.sizing.cornerFull))
                    .background(CryptoTrackerTheme.colors.surfaceContainerHigh)
                    .border(
                        width = CryptoTrackerTheme.sizing.borderThin,
                        color = CryptoTrackerTheme.colors.outline,
                        shape = RoundedCornerShape(CryptoTrackerTheme.sizing.cornerFull),
                    ).clickable {
                        uriHandler.openUri(COMPOSE_MULTIPLATFORM_URL)
                    }.padding(
                        horizontal = CryptoTrackerTheme.spacing.medium,
                        vertical = CryptoTrackerTheme.spacing.small,
                    ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(CryptoTrackerTheme.spacing.extraSmall),
        ) {
            Image(
                painter = painterResource(Res.drawable.compose_multiplatform),
                contentDescription = "Compose Multiplatform",
                modifier = Modifier.size(CryptoTrackerTheme.sizing.iconSmall),
                colorFilter = ColorFilter.tint(CryptoTrackerTheme.colors.primary),
            )

            Text(
                text = stringResource(Res.string.built_with),
                style = CryptoTrackerTheme.typography.bodySmall,
                color = CryptoTrackerTheme.colors.onSurfaceVariant,
            )

            Text(
                text = stringResource(Res.string.compose_multiplatform_name) + " " + EXTERNAL_LINK_ARROW,
                style =
                    CryptoTrackerTheme.typography.bodySmall.copy(
                        textDecoration = TextDecoration.Underline,
                    ),
                color = CryptoTrackerTheme.colors.primary,
            )
        }
    }
}
