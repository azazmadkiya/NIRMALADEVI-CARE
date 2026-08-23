package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BrandAcid,
    onPrimary = BrandInk,
    primaryContainer = BrandSteelLight,
    onPrimaryContainer = BrandAcid,
    secondary = BrandAzureLight,
    onSecondary = Color.White,
    secondaryContainer = BrandSteel,
    onSecondaryContainer = Color.White,
    tertiary = BrandAcid,
    onTertiary = BrandInk,
    background = BrandInk,
    onBackground = Color.White,
    surface = BrandSteel,
    onSurface = Color.White,
    surfaceVariant = BrandSteelLight,
    onSurfaceVariant = BrandMist,
    outline = BrandBorderDark,
    error = DangerRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = BrandInk,
    onPrimary = BrandAcid,
    primaryContainer = BrandAcid,
    onPrimaryContainer = BrandInk,
    secondary = BrandAzure,
    onSecondary = Color.White,
    secondaryContainer = BrandMist,
    onSecondaryContainer = BrandInk,
    tertiary = BrandAcidDark,
    onTertiary = Color.White,
    background = BrandFog,
    onBackground = BrandTextDark,
    surface = Color.White,
    onSurface = BrandTextDark,
    surfaceVariant = BrandMist,
    onSurfaceVariant = BrandMid,
    outline = BrandBorder,
    error = DangerRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek brand dark theme
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
