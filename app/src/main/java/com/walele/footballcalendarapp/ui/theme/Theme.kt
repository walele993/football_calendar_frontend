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
val BackgroundLight = Color(0xFFf4ede8)
val BackgroundGradient = Color(0xFFDAD7CD)
val PrimaryTextLight = Color(0xFF344e41)
val SecondaryTextLight = Color(0xFFa3b18a)

val GreenAccent = Color(0xFF588157)
val PinkAccent = Color(0xFFFF4D97)

// Colori per il tema scuro
val BackgroundDark = Color(0xFF1B1B1B)              // Sfondo principale, quasi nero ma non assoluto
val BackgroundGradientDark = Color(0xFF2A2A2A)       // Variante per sfumature o pannelli
val PrimaryTextDark = Color(0xFFECECEC)              // Testo principale molto chiaro
val SecondaryTextDark = Color(0xFFB5C8A5)            // Testo secondario con un verde desaturato

val GreenAccentDark = Color(0xFF7DA47A)              // Verde più morbido, leggibile su scuro
val PinkAccentDark = Color(0xFFFF75B5)

// Colori per il tema chiaro
private val LightColors = lightColorScheme(
    primary = PinkAccent,
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
    primary = PinkAccent,
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
