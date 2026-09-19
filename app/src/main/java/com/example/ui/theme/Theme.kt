package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val WhiteCombinationColorScheme = lightColorScheme(
    primary = AccentBlue,
    onPrimary = Color.White,
    primaryContainer = AccentBlueSoft,
    onPrimaryContainer = AccentBlueDark,
    secondary = AccentTeal,
    onSecondary = Color.White,
    secondaryContainer = AccentTealSoft,
    onSecondaryContainer = AccentTealDark,
    tertiary = AccentGreen,
    onTertiary = Color.White,
    background = WhiteCanvas,
    onBackground = TextPrimary,
    surface = WhiteSurface,
    onSurface = TextPrimary,
    surfaceVariant = WhiteSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = WhiteBorder,
    outlineVariant = WhiteBorderStrong,
    error = AccentRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Default to White Combination Theme
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = WhiteCombinationColorScheme,
        typography = Typography,
        content = content
    )
}

