package com.cryptodanilo.project.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import cryptotrackerdanilo.shared.generated.resources.Res
import cryptotrackerdanilo.shared.generated.resources.space_mono_bold
import cryptotrackerdanilo.shared.generated.resources.space_mono_bold_italic
import cryptotrackerdanilo.shared.generated.resources.space_mono_italic
import cryptotrackerdanilo.shared.generated.resources.space_mono_regular
import org.jetbrains.compose.resources.Font

@Composable
fun spaceMono() =
    FontFamily(
        Font(
            resource = Res.font.space_mono_regular,
            weight = FontWeight.Normal,
        ),
        Font(
            resource = Res.font.space_mono_italic,
            weight = FontWeight.Normal,
            style = FontStyle.Italic,
        ),
        Font(
            resource = Res.font.space_mono_bold,
            weight = FontWeight.Bold,
        ),
        Font(
            resource = Res.font.space_mono_bold_italic,
            weight = FontWeight.Bold,
            style = FontStyle.Italic,
        ),
    )

// Set of Material typography styles to start with
@Composable
fun typographyApp() =
    Typography(
        bodySmall =
            TextStyle(
                fontFamily = spaceMono(),
                fontWeight = FontWeight.Light,
                fontSize = 12.sp,
            ),
        bodyMedium =
            TextStyle(
                fontFamily = spaceMono(),
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
            ),
        bodyLarge =
            TextStyle(
                fontFamily = spaceMono(),
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.5.sp,
            ),
        labelMedium =
            TextStyle(
                fontFamily = spaceMono(),
                fontWeight = FontWeight.Normal,
            ),
        headlineMedium =
            TextStyle(
                fontFamily = spaceMono(),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
            ),
    )
