package com.walele.footballcalendarapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.walele.footballcalendar.R

// Colori per il tema chiaro
val BackgroundLight = Color(0xFFf4ede8) // Grigio chiarissimo, quasi bianco
val BackgroundGradient = Color(0xFFDAD7CD)
val PrimaryTextLight = Color(0xFF344e41) // Nero caldo
val SecondaryTextLight = Color(0xFFa3b18a) // Grigio elegante

val GreenAccent = Color(0xFF588157) // Blu acceso stile iOS
val OrangeAccent = Color(0xFFFF4D97) // Arancione energico

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

val OnestVariableFont = FontFamily(
    Font(R.font.onest_variablefont_wght, weight = FontWeight.Normal)
)

val InterVariableFont = FontFamily(
    Font(R.font.inter_variablefont_opsz_wght, weight = FontWeight.Normal)
)

val InterItalicVariableFont = FontFamily(
    Font(R.font.inter_italic_variablefont_opsz_wght, weight = FontWeight.Normal)
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
