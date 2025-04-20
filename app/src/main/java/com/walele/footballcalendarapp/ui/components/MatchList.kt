package com.walele.footballcalendarapp.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.walele.footballcalendarapp.data.Match
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.ui.graphics.Color

@Composable
fun MatchList(matches: List<Match>, selectedDate: LocalDate) {
    val listState = rememberLazyListState()
    val today = LocalDate.now()
    val label = when (selectedDate) {
        today -> "Today"
        today.plusDays(1) -> "Tomorrow"
        today.minusDays(1) -> "Yesterday"
        else -> selectedDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
    }

    LaunchedEffect(selectedDate) {
        listState.scrollToItem(0)
    }

    Text(
        text = label,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    if (matches.isEmpty()) {
        Text(
            text = "No match today",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(16.dp),
            color = Color.Gray
        )
    } else {
        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
            items(matches.size) { index ->
                MatchItem(matches[index])
                Spacer(modifier = Modifier.height(8.dp))
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
            Text(text = "${match.time}  •  ${match.league}", style = MaterialTheme.typography.labelMedium)
            Text(text = "${match.home} vs ${match.away}", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
