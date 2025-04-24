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
import java.time.format.DateTimeFormatter

@Composable
fun MatchList(
    matches: List<Match>,
    selectedDate: LocalDate,
    bottomPadding: Dp
) {
    val listState = rememberLazyListState()
    val today = LocalDate.now()

    // Log quando il composable viene richiamato
    Log.d("MatchList", "Rendering match list for date: $selectedDate")

    val label = when (selectedDate) {
        today -> Pair("Today", today.format(DateTimeFormatter.ofPattern("d MMMM")))
        today.plusDays(1) -> Pair("Tomorrow", today.plusDays(1).format(DateTimeFormatter.ofPattern("d MMMM")))
        today.minusDays(1) -> Pair("Yesterday", today.minusDays(1).format(DateTimeFormatter.ofPattern("d MMMM")))
        else -> {
            val dayOfWeek = selectedDate.dayOfWeek.name.lowercase().replaceFirstChar { it.titlecase() }
            val dayOfMonth = selectedDate.dayOfMonth
            val month = selectedDate.month.name.lowercase().replaceFirstChar { it.titlecase() }
            Pair(dayOfWeek, "$dayOfMonth $month")
        }
    }

    LaunchedEffect(selectedDate) {
        listState.scrollToItem(0)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (label is Pair<*, *>) {
            val dayOfWeekText = label.first as String
            val dateText = label.second as String

            Row(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)) {
                Text(
                    text = dayOfWeekText,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF1F1F1F),
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFFB0B0B0)
                )
            }
        }

        if (matches.isEmpty()) {
            // Log quando non ci sono partite
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
                items(matches.size) { index ->
                    MatchItemCard(matches[index])
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    // Aggiungi uno spacer extra per il padding
                    Spacer(modifier = Modifier.height(bottomPadding))
                }
            }
        }
    }
}


@Composable
fun MatchItemCard(match: Match) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Visualizziamo la data della partita, la lega e il punteggio
            Text(
                text = "${match.date}  •  ${match.league.name}",
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
