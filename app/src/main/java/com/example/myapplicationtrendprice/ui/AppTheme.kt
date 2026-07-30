package com.example.myapplicationtrendprice.ui

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val BrandGreen = Color(0xFF00A36C)
val BrandGreenDark = Color(0xFF063B32)
val BrandGreenSoft = Color(0xFFE6F7EF)
val BrandMint = Color(0xFFBFF4DB)
val AppBackground = Color(0xFFF6F8F7)
val CardWhite = Color.White
val TextPrimary = Color(0xFF121A17)
val TextSecondary = Color(0xFF66736D)
val BorderColor = Color(0xFFE1E8E4)
val DangerRed = Color(0xFFE5484D)
val WarningOrange = Color(0xFFFFA726)
val PremiumDark = Color(0xFF071F1A)

private val LightColors = lightColorScheme(
    primary = BrandGreen,
    onPrimary = Color.White,
    primaryContainer = BrandGreenSoft,
    onPrimaryContainer = BrandGreenDark,
    secondary = BrandGreenDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDDF3EA),
    background = AppBackground,
    onBackground = TextPrimary,
    surface = CardWhite,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFF0F4F2),
    onSurfaceVariant = TextSecondary,
    outline = BorderColor,
    error = DangerRed
)

private val DarkColors = darkColorScheme(
    primary = BrandMint,
    onPrimary = PremiumDark,
    primaryContainer = Color(0xFF0C5949),
    onPrimaryContainer = Color.White,
    secondary = BrandMint,
    background = Color(0xFF08110F),
    surface = Color(0xFF101B18),
    onSurface = Color(0xFFEAF3EF),
    surfaceVariant = Color(0xFF17231F),
    onSurfaceVariant = Color(0xFFB7C7C0),
    outline = Color(0xFF25352F),
    error = Color(0xFFFFB4AB)
)

private val AppTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Black,
        fontSize = 34.sp,
        lineHeight = 38.sp,
        letterSpacing = (-0.6).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Black,
        fontSize = 26.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.3).sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 21.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 18.sp
    )
)

@Composable
fun TrendPriceTheme(
    useDynamicColor: Boolean = false,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colors = when {
        useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        content = content
    )
}
