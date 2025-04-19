package com.walele.footballcalendarapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.walele.footballcalendarapp.ui.theme.FootballCalendarAppTheme
import java.time.LocalDate
import java.time.YearMonth

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

        CalendarPager()

        Spacer(modifier = Modifier.height(16.dp))

        MatchList(matches = listOf(
            Match("12:30", "Real Madrid", "Barcelona", "La Liga"),
            Match("18:45", "Arsenal", "Chelsea", "Premier League")
        ))
    }
}

@Composable
fun TopBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Football Calendar",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black
        )
        IconButton(onClick = { /* TODO: Add filter functionality */ }) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Filter",
                tint = Color.Black
            )
        }
    }
}

@Composable
fun CalendarPager() {
    val startYear = 2020
    val endYear = 2026

    val monthYearList = remember {
        (startYear..endYear).flatMap { year ->
            (1..12).map { month ->
                YearMonth.of(year, month)
            }
        }
    }

    val initialPage = monthYearList.indexOf(YearMonth.of(2025, 4))
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { monthYearList.size })

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth()
    ) { page ->
        val yearMonth = monthYearList[page]
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color(0xFFF2F2F2)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val formattedMonth = yearMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }
            Text(
                text = "$formattedMonth ${yearMonth.year}",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )
            CalendarView(yearMonth)
        }
    }
}

@Composable
fun CalendarView(yearMonth: YearMonth) {
    val firstDayOfMonth = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // Sunday = 0

    val days = List(daysInMonth) { it + 1 }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val weekDays = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            weekDays.forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        val emptyDays = List(firstDayOfWeek) { "" }
        val allDays = emptyDays + days.map { it.toString() }

        allDays.chunked(7).forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                week.forEach { day ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

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
