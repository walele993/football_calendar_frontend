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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.walele.footballcalendarapp.ui.theme.GreenAccent
import com.walele.footballcalendarapp.ui.theme.InterVariableFont
import com.walele.footballcalendarapp.ui.theme.OnestVariableFont
import com.walele.footballcalendarapp.ui.theme.OrangeAccent
import com.walele.footballcalendarapp.ui.theme.SecondaryTextLight

@Composable
fun MatchList(
    matches: List<Match>,
    selectedDate: LocalDate,
    bottomPadding: Dp,
    leagueSelected: Boolean = false,
    selectedLeagueName: String?
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
        Row(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 4.dp)) {
            Text(
                text = label.first,
                style = typography.headlineSmall.copy(
                    fontFamily = OnestVariableFont,
                    fontWeight = FontWeight.W900
                ),
                color = Color(0xFF2a1e17),
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = label.second,
                style = typography.headlineSmall.copy(
                    fontFamily = OnestVariableFont,
                    fontWeight = FontWeight.W900
                ),
                color = SecondaryTextLight
            )
        }

        if (leagueSelected && selectedLeagueName != null) {
            Text(
                text = selectedLeagueName,
                style = typography.headlineSmall.copy(
                    fontFamily = OnestVariableFont,
                    fontWeight = FontWeight.W900
                ),
                color = GreenAccent,
                modifier = Modifier
                    .padding(start = 16.dp, bottom = 24.dp)
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
                            tint = SecondaryTextLight,
                            modifier = iconModifier
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (!leagueSelected) "Choose a league to get started" else "No matches for the selected date",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = OnestVariableFont,
                                fontWeight = FontWeight.W500
                            ),
                            color = SecondaryTextLight
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
                    // Pass the boolean indicating if it's the last element
                    MatchItemRow(match = sortedMatches[index], isLastItem = index == sortedMatches.size - 1)
                }

                item {
                    Spacer(modifier = Modifier.height(bottomPadding))
                }
            }
        }
    }
}

@Composable
fun MatchItemRow(match: Match, isLastItem: Boolean) {
    val formattedTime = runCatching {
        LocalTime.parse(match.time).format(DateTimeFormatter.ofPattern("HH:mm"))
    }.getOrElse { match.time }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        // Content above the divider
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (formattedTime != null) {
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontFamily = InterVariableFont,
                            fontWeight = FontWeight.W400
                        ),
                        color = GreenAccent
                    )
                }

                Text(
                    text = match.matchday,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = InterVariableFont,
                        fontWeight = FontWeight.W400
                    ),
                    color = OrangeAccent
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "${match.homeTeam.name} vs ${match.awayTeam.name}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = OnestVariableFont,
                    fontWeight = FontWeight.W400
                ),
                color = Color(0xFF2a1e17)
            )

            match.scoreHome?.let { scoreHome ->
                match.scoreAway?.let { scoreAway ->
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$scoreHome - $scoreAway",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = InterVariableFont,
                            fontWeight = FontWeight.W400
                        ),
                        color = Color(0xFF1e1e1e)
                    )
                }
            }
        }

        // Spacer to push the divider down
        Spacer(modifier = Modifier.height(24.dp))

        // Divider (Only show it if it's not the last item)
        if (!isLastItem) {
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 0.5.dp,
                color = SecondaryTextLight
            )
        }
    }
}
