package com.walele.footballcalendarapp.ui.components

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
    bottomPadding: Dp // Aggiungiamo il parametro bottomPadding qui
) {
    val listState = rememberLazyListState()
    val today = LocalDate.now()
    val label = when (selectedDate) {
        today -> Pair("Today", today.format(DateTimeFormatter.ofPattern("d MMMM")))
        today.plusDays(1) -> Pair("Tomorrow", today.plusDays(1).format(DateTimeFormatter.ofPattern("d MMMM")))
        today.minusDays(1) -> Pair("Yesterday", today.minusDays(1).format(DateTimeFormatter.ofPattern("d MMMM")))
        else -> {
            // Formattazione personalizzata per il giorno della settimana e la data
            val dayOfWeek = selectedDate.dayOfWeek.name.lowercase().replaceFirstChar { it.titlecase() }
            val dayOfMonth = selectedDate.dayOfMonth
            val month = selectedDate.month.name.lowercase().replaceFirstChar { it.titlecase() }

            // Restituiamo il giorno della settimana e il giorno del mese separati per poter applicare colori diversi
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
                    MatchItem(matches[index])
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    // Aggiungiamo un Spacer extra per lo spazio cuscinetto
                    Spacer(modifier = Modifier.height(bottomPadding))
                }
            }
        }
    }
}

@Composable
fun MatchItem(match: Match) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${match.time}  •  ${match.league}",
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = "${match.home} vs ${match.away}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
