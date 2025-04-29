package com.walele.footballcalendarapp.ui.components

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.SentimentVeryDissatisfied
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.walele.footballcalendarapp.data.Match
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.walele.footballcalendarapp.ui.theme.InterVariableFont
import com.walele.footballcalendarapp.ui.theme.OnestVariableFont

@Composable
fun MatchList(
    matches: List<Match>,
    selectedDate: LocalDate,
    bottomPadding: Dp,
    leagueSelected: Boolean = false
) {
    val listState = rememberLazyListState()
    val today = LocalDate.now()

    Log.d("MatchList", "Rendering match list for date: $selectedDate")

    val label = when (selectedDate) {
        today -> "Today" to today.format(DateTimeFormatter.ofPattern("d MMMM"))
        today.plusDays(1) -> "Tomorrow" to today.plusDays(1).format(DateTimeFormatter.ofPattern("d MMMM"))
        today.minusDays(1) -> "Yesterday" to today.minusDays(1).format(DateTimeFormatter.ofPattern("d MMMM"))
        else -> {
            val dayOfWeek = selectedDate.dayOfWeek.name.lowercase().replaceFirstChar { it.titlecase() }
            val dateText = selectedDate.format(DateTimeFormatter.ofPattern("d MMMM"))
            dayOfWeek to dateText
        }
    }

    val sortedMatches = matches.sortedBy { runCatching { LocalTime.parse(it.time) }.getOrNull() }

    LaunchedEffect(selectedDate) {
        listState.scrollToItem(0)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)) {
            Text(
                text = label.first,
                style = typography.headlineSmall.copy(
                    fontFamily = OnestVariableFont,
                    fontWeight = FontWeight.W900,),
                color = Color(0xFF121212),
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = label.second,
                style = typography.headlineSmall.copy(
                    fontFamily = OnestVariableFont,
                    fontWeight = FontWeight.W900,),
                color = Color(0xFFB0B0B0)
            )
        }

        if (sortedMatches.isEmpty()) {
            Log.d("MatchList", "Is league selected: $leagueSelected")
            Log.d("MatchList", "No matches or no league selected")

            val messageKey = if (!leagueSelected) "choose_league" else "no_matches"
            var previousKey by remember { mutableStateOf("") }
            val shouldAnimate = previousKey != messageKey
            previousKey = messageKey

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                this@Column.AnimatedVisibility(
                    visible = true,
                    enter = if (shouldAnimate) fadeIn(animationSpec = tween(400)) else EnterTransition.None,
                    exit = ExitTransition.None
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val icon = if (!leagueSelected) Icons.Outlined.EmojiEvents else Icons.Outlined.SentimentVeryDissatisfied
                        val iconModifier = Modifier
                            .size(64.dp)
                            .graphicsLayer {
                                rotationZ = if (!leagueSelected) 75f else 0f
                            }

                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color(0xFFFF6B00),
                            modifier = iconModifier
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (!leagueSelected) "Choose a league to get started" else "No matches for the selected date",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = OnestVariableFont,
                                fontWeight = FontWeight.W700
                            ),
                            color = Color(0xFFFF6B00)
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                items(sortedMatches.size) { index ->
                    MatchItemCard(sortedMatches[index])
                    Spacer(modifier = Modifier.height(4.dp))
                }

                item {
                    Spacer(modifier = Modifier.height(bottomPadding))
                }
            }
        }
    }
}


@Composable
fun MatchItemCard(match: Match) {
    val formattedTime = runCatching {
        LocalTime.parse(match.time).format(DateTimeFormatter.ofPattern("HH:mm"))
    }.getOrElse { match.time }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp) // Padding esterno
            .clip(RoundedCornerShape(20.dp)) // Bordo arrotondato
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF00A86B).copy(alpha = 0.5f), Color(0xFF00A86B).copy(alpha = 0.7f))
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp)) // Bordi bianchi
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "$formattedTime  •  ${match.league.name}",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontFamily = InterVariableFont,
                    fontWeight = FontWeight.W500
                ),
                color = Color.White // Colore del testo bianco per contrasto
            )
            Text(
                text = "${match.homeTeam.name} vs ${match.awayTeam.name}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = OnestVariableFont,
                    fontWeight = FontWeight.W900
                ),
                color = Color.White
            )
            match.scoreHome?.let { scoreHome ->
                match.scoreAway?.let { scoreAway ->
                    Text(
                        text = "Score: $scoreHome - $scoreAway",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = InterVariableFont,
                            fontWeight = FontWeight.W500
                        ),
                        color = Color.White // Colore più chiaro per il punteggio
                    )
                }
            }
        }
    }
}
