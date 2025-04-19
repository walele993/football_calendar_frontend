package com.walele.footballcalendarapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.walele.footballcalendarapp.ui.theme.FootballCalendarAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FootballCalendarAppTheme {
                HomeScreen()
            }
        }
    }
}

// Schermata principale
@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        TopBar()

        Spacer(modifier = Modifier.height(16.dp))

        CalendarPlaceholder()

        Spacer(modifier = Modifier.height(16.dp))

        MatchList(matches = listOf(
            Match("12:30", "Real Madrid", "Barcelona", "La Liga"),
            Match("18:45", "Arsenal", "Chelsea", "Premier League")
        ))
    }
}

// Top Bar con mese e filtro
@Composable
fun TopBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "April 2025",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black
        )
        IconButton(onClick = { /* TODO: Aggiungi filtro */ }) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Filter",
                tint = Color.Black
            )
        }
    }
}

// Calendario provvisorio
@Composable
fun CalendarPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color(0xFFF2F2F2)),
        contentAlignment = Alignment.Center
    ) {
        Text("Calendar View (Coming Soon)", color = Color.Gray)
    }
}

// Match e lista
data class Match(val time: String, val home: String, val away: String, val league: String)

@Composable
fun MatchList(matches: List<Match>) {
    Column {
        matches.forEach { match ->
            MatchItem(match)
            Spacer(modifier = Modifier.height(8.dp))
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
