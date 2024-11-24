package com.cryptodanilo.project.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import cryptotrackerdanilo.composeapp.generated.resources.Res
import cryptotrackerdanilo.composeapp.generated.resources.space_mono_bold
import cryptotrackerdanilo.composeapp.generated.resources.space_mono_bold_italic
import cryptotrackerdanilo.composeapp.generated.resources.space_mono_italic
import cryptotrackerdanilo.composeapp.generated.resources.space_mono_regular
import org.jetbrains.compose.resources.Font

@Composable
fun SpaceMono() = FontFamily(
    Font(
        resource = Res.font.space_mono_regular,
        weight = FontWeight.Normal
    ),
    Font(
        resource = Res.font.space_mono_italic,
        weight = FontWeight.Normal,
        style = FontStyle.Italic
    ),
    Font(
        resource = Res.font.space_mono_bold,
        weight = FontWeight.Bold
    ),
    Font(
        resource = Res.font.space_mono_bold_italic,
        weight = FontWeight.Bold,
        style = FontStyle.Italic
    ),
)

// Set of Material typography styles to start with
@Composable
fun TypographyApp() = Typography(
    bodySmall = TextStyle(
        fontFamily = SpaceMono(),
        fontWeight = FontWeight.Light,
        fontSize = 12.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = SpaceMono(),
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = SpaceMono(),
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    labelMedium = TextStyle(
        fontFamily = SpaceMono(),
        fontWeight = FontWeight.Normal,
    ),
    headlineMedium = TextStyle(
        fontFamily = SpaceMono(),
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    )
)
