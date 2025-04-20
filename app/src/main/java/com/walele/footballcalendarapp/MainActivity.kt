package com.walele.footballcalendarapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.walele.footballcalendarapp.ui.theme.FootballCalendarAppTheme
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState

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
    val selectedDate = remember { mutableStateOf(LocalDate.now()) }
    val startYear = 2020
    val endYear = 2026
    val monthYearList = remember {
        (startYear..endYear).flatMap { year ->
            (1..12).map { month -> YearMonth.of(year, month) }
        }
    }
    val initialPage = monthYearList.indexOf(YearMonth.of(selectedDate.value.year, selectedDate.value.month))
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { monthYearList.size })
    val coroutineScope = rememberCoroutineScope()
    val currentMonthYear = remember { mutableStateOf(monthYearList[initialPage]) }

    LaunchedEffect(pagerState.currentPage) {
        currentMonthYear.value = monthYearList[pagerState.currentPage]
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(currentMonthYear.value)

            // Calendar (top half)
            Column(modifier = Modifier.weight(1f)) {
                HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth()) { page ->
                    val yearMonth = monthYearList[page]
                    CalendarView(
                        yearMonth = yearMonth,
                        selectedDate = selectedDate.value,
                        onDateSelected = { date, isCurrentMonth ->
                            val newMonthIndex = monthYearList.indexOf(YearMonth.of(date.year, date.month))
                            coroutineScope.launch {
                                if (!isCurrentMonth) {
                                    pagerState.animateScrollToPage(newMonthIndex)
                                }
                                selectedDate.value = date
                            }
                        }
                    )
                }
            }

            // Match List (bottom half) with vertical scrolling
            Column(modifier = Modifier.weight(1f).padding(16.dp)) {
                MatchList(matches = getMatchesForDate(selectedDate.value), selectedDate = selectedDate.value)
            }
        }
    }
}

@Composable
fun TopBar(currentMonthYear: YearMonth) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val formattedMonth = currentMonthYear.month.name.lowercase().replaceFirstChar { it.uppercase() }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = formattedMonth,
                style = MaterialTheme.typography.headlineSmall.copy(color = Color(0xFF1F1F1F)),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${currentMonthYear.year}",
                style = MaterialTheme.typography.headlineSmall.copy(color = Color(0xFFB0B0B0)),
            )
        }
        IconButton(onClick = { /* TODO */ }) {
            Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = Color.Black)
        }
    }
}

@Composable
fun CalendarView(
    yearMonth: YearMonth,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate, Boolean) -> Unit
) {
    val today = LocalDate.now()
    val firstDayOfMonth = yearMonth.atDay(1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
    val daysInMonth = yearMonth.lengthOfMonth()

    val previousMonth = yearMonth.minusMonths(1)
    val daysInPreviousMonth = previousMonth.lengthOfMonth()
    val leadingDays = (daysInPreviousMonth - firstDayOfWeek + 1..daysInPreviousMonth).map {
        LocalDate.of(previousMonth.year, previousMonth.month, it)
    }
    val currentMonthDays = (1..daysInMonth).map {
        LocalDate.of(yearMonth.year, yearMonth.month, it)
    }
    val totalDays = leadingDays.size + currentMonthDays.size
    val trailingDaysCount = (7 - (totalDays % 7)).takeIf { it < 7 } ?: 0
    val nextMonth = yearMonth.plusMonths(1)
    val trailingDays = (1..trailingDaysCount).map {
        LocalDate.of(nextMonth.year, nextMonth.month, it)
    }
    val allDays = (leadingDays + currentMonthDays + trailingDays).chunked(7)

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
        val weekDays = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        Row(modifier = Modifier.fillMaxWidth()) {
            weekDays.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.DarkGray
                )
            }
        }
        allDays.forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { date ->
                    val isCurrentMonth = date.month == yearMonth.month
                    val isSelected = date == selectedDate
                    val isToday = date == today

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(4.dp)
                            .background(
                                if (isSelected) Color(0xFFCCE5FF) else Color.Transparent,
                                shape = MaterialTheme.shapes.small
                            )
                            .clickable { onDateSelected(date, isCurrentMonth) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = date.dayOfMonth.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = when {
                                isToday -> Color(0xFFFF5722)
                                isCurrentMonth -> Color.Black
                                else -> Color.Gray
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MatchList(matches: List<Match>, selectedDate: LocalDate) {
    val listState = rememberLazyListState() // Aggiungi il LazyListState
    val today = LocalDate.now()
    val label = when (selectedDate) {
        today -> "Today"
        today.plusDays(1) -> "Tomorrow"
        today.minusDays(1) -> "Yesterday"
        else -> selectedDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
    }

    // Riporta il listState all'inizio quando cambia la data
    LaunchedEffect(selectedDate) {
        listState.scrollToItem(0)
    }

    Text(
        text = label,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
        items(matches.size) { index ->
            MatchItem(matches[index])
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

data class Match(val time: String, val home: String, val away: String, val league: String)

fun getMatchesForDate(date: LocalDate): List<Match> {
    return when (date.dayOfMonth) {
        20 -> listOf(
            Match("12:30", "Real Madrid", "Barcelona", "La Liga"),
            Match("14:00", "Napoli", "Juventus", "Serie A"),
            Match("15:45", "Liverpool", "Arsenal", "Premier League"),
            Match("17:30", "Bayern", "PSG", "Champions League"),
            Match("19:00", "Ajax", "Feyenoord", "Eredivisie"),
            Match("21:00", "Chelsea", "Man City", "Premier League")
        )
        21 -> listOf(
            Match("13:00", "Milan", "Inter", "Serie A"),
            Match("15:00", "Leverkusen", "Dortmund", "Bundesliga"),
            Match("17:45", "Lazio", "Roma", "Serie A"),
            Match("20:00", "Tottenham", "Brighton", "Premier League"),
            Match("22:00", "Porto", "Benfica", "Liga Portugal")
        )
        else -> emptyList()
    }
}
