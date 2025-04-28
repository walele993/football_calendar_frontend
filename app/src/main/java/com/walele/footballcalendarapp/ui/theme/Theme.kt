package com.walele.footballcalendarapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color

// Colori per il tema chiaro
val BackgroundLight = Color(0xFFFFFFFF) // Bianco
val PrimaryTextLight = Color(0xFF121212) // Quasi nero
val SecondaryTextLight = Color(0xFF757575) // Grigio

val OrangeAccent = Color(0xFFFF6B00) // Arancione intenso
val GreenAccent = Color(0xFF00A86B) // Verde campo

val BlueAccent = Color(0xFF0096FF) // Azzurro
val YellowAccent = Color(0xFFFFD600) // Giallo

// Colori per il tema scuro
val BackgroundDark = Color(0xFF121212) // Background scuro
val PrimaryTextDark = Color(0xFFFFFFFF) // Testo chiaro
val SecondaryTextDark = Color(0xFFB0B0B0) // Grigio chiaro

// Colori per il tema chiaro
private val LightColors = lightColorScheme(
    primary = OrangeAccent,
    onPrimary = Color.White,
    secondary = GreenAccent,
    onSecondary = Color.White,
    background = BackgroundLight,
    onBackground = PrimaryTextLight,
    surface = BackgroundLight,
    onSurface = PrimaryTextLight
)

// Colori per il tema scuro
private val DarkColors = darkColorScheme(
    primary = OrangeAccent,
    onPrimary = Color.Black,
    secondary = GreenAccent,
    onSecondary = Color.Black,
    background = BackgroundDark,
    onBackground = PrimaryTextDark,
    surface = BackgroundDark,
    onSurface = PrimaryTextDark
)

@Composable
fun FootballCalendarAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
