package com.walele.footballcalendarapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
            (1..12).map { month ->
                YearMonth.of(year, month)
            }
        }
    }
    val initialPage = monthYearList.indexOf(YearMonth.of(selectedDate.value.year, selectedDate.value.month))
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { monthYearList.size })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        TopBar()

        // Calendar (top half)
        Column(modifier = Modifier.weight(1f)) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val yearMonth = monthYearList[page]
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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
        }

        // Match List (bottom half)
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            MatchList(
                matches = getMatchesForDate(selectedDate.value),
                selectedDate = selectedDate.value
            )
        }
    }
}

@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween, // Changes to space between to put filter on the right
        verticalAlignment = Alignment.CenterVertically
    ) {
        val selectedDate = LocalDate.now() // Placeholder for selected date in the top bar
        val formattedMonth = selectedDate.month.name.lowercase().replaceFirstChar { it.uppercase() }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$formattedMonth", // Month only, no year here
                style = MaterialTheme.typography.headlineSmall.copy(color = Color(0xFF1F1F1F)), // Dark month color
            )
            Spacer(modifier = Modifier.width(4.dp)) // Adds some space between month and year
            Text(
                text = "${selectedDate.year}",
                style = MaterialTheme.typography.headlineSmall.copy(color = Color(0xFFB0B0B0)), // Lighter year color (gray)
            )
        }

        // Filter Icon on the right
        IconButton(onClick = { /* TODO: Add filter */ }) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Filter",
                tint = Color.Black
            )
        }
    }
}

@Composable
fun CalendarView(
    yearMonth: YearMonth,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate, Boolean) -> Unit
) {
    val firstDayOfMonth = yearMonth.atDay(1)
    val lastDayOfMonth = yearMonth.atEndOfMonth()
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
    val daysInMonth = yearMonth.lengthOfMonth()

    val previousMonth = yearMonth.minusMonths(1)
    val daysInPreviousMonth = previousMonth.lengthOfMonth()
    val leadingDays = (daysInPreviousMonth - firstDayOfWeek + 1..daysInPreviousMonth).map { day ->
        LocalDate.of(previousMonth.year, previousMonth.month, day)
    }
    val currentMonthDays = (1..daysInMonth).map { day ->
        LocalDate.of(yearMonth.year, yearMonth.month, day)
    }
    val totalDays = leadingDays.size + currentMonthDays.size
    val trailingDaysCount = (7 - (totalDays % 7)).takeIf { it < 7 } ?: 0
    val nextMonth = yearMonth.plusMonths(1)
    val trailingDays = (1..trailingDaysCount).map { day ->
        LocalDate.of(nextMonth.year, nextMonth.month, day)
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
                            color = if (isCurrentMonth) Color.Black else Color.Gray
                        )
                    }
                }
            }
        }
    }
}

data class Match(val time: String, val home: String, val away: String, val league: String)

fun getMatchesForDate(date: LocalDate): List<Match> {
    return when (date.dayOfMonth) {
        20 -> listOf(
            Match("12:30", "Real Madrid", "Barcelona", "La Liga"),
            Match("18:45", "Arsenal", "Chelsea", "Premier League")
        )
        21 -> listOf(
            Match("15:00", "Milan", "Inter", "Serie A")
        )
        else -> emptyList()
    }
}

@Composable
fun MatchList(matches: List<Match>, selectedDate: LocalDate) {
    Text(
        text = "Matches on ${selectedDate.dayOfMonth}/${selectedDate.monthValue}/${selectedDate.year}",
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(vertical = 8.dp)
    )

    matches.forEach { match ->
        MatchItem(match)
        Spacer(modifier = Modifier.height(8.dp))
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
