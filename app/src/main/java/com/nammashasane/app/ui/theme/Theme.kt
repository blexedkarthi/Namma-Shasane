package com.nammashasane.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val StoneBeige = Color(0xFFF5F0E8)
val ParchmentDark = Color(0xFF8B6914)
val ParchmentLight = Color(0xFFD4A843)
val AncientRed = Color(0xFF8B2500)
val ForestGreen = Color(0xFF2E5C3A)
val InkBlack = Color(0xFF1A1208)
val GoldAccent = Color(0xFFE8B84B)
val White = Color(0xFFFFFFFF)

private val LightColorScheme = lightColorScheme(
    primary = ParchmentDark,
    onPrimary = White,
    primaryContainer = Color(0xFFFFF8E7),
    secondary = ForestGreen,
    onSecondary = White,
    background = StoneBeige,
    surface = Color(0xFFFFFBF4),
    onBackground = InkBlack,
    onSurface = InkBlack,
    error = AncientRed,
    tertiary = GoldAccent
)

@Composable
fun NammaShasaneTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        // We'll stick to a warm theme even in dark mode for this "ancient" vibe,
        // but normally we would define a DarkColorScheme here.
        LightColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
