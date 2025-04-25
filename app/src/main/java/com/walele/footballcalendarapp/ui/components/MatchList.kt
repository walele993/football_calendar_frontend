package com.walele.footballcalendarapp.ui.components

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.walele.footballcalendarapp.data.Match
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun MatchList(
    matches: List<Match>,
    selectedDate: LocalDate,
    bottomPadding: Dp
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
        Row(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)) {
            Text(
                text = label.first,
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF1F1F1F),
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = label.second,
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFFB0B0B0)
            )
        }

        if (sortedMatches.isEmpty()) {
            Log.d("MatchList", "No matches for selected date")
            Text(
                text = "No match today",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp),
                color = Color.Gray
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(sortedMatches.size) { index ->
                    MatchItemCard(sortedMatches[index])
                    Spacer(modifier = Modifier.height(8.dp))
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

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "$formattedTime  •  ${match.league.name}",
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = "${match.homeTeam.name} vs ${match.awayTeam.name}",
                style = MaterialTheme.typography.bodyLarge
            )
            match.scoreHome?.let { scoreHome ->
                match.scoreAway?.let { scoreAway ->
                    Text(
                        text = "Score: $scoreHome - $scoreAway",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFB0B0B0)
                    )
                }
            }
        }
    }
}
