package com.walele.footballcalendarapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@Composable
fun YearlyCalendarView(
    startYear: Int = 2020,
    endYear: Int = 2030,
    selectedDate: LocalDate,
    onMonthSelected: (YearMonth) -> Unit,
    onDateSelected: (LocalDate, Boolean) -> Unit,
) {
    val year = selectedDate.year
    val months = (1..12).map { YearMonth.of(year, it) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(months) { ym ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onMonthSelected(ym) }
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = ym.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase(),
                    style = MaterialTheme.typography.labelMedium
                )
                MiniCalendarGrid(ym, selectedDate, onDateSelected)
            }
        }
    }
}

@Composable
private fun MiniCalendarGrid(
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
    val allDays = leadingDays + currentMonthDays + trailingDays

    Column(modifier = Modifier.fillMaxWidth()) {
        for (week in allDays.chunked(7)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { date ->
                    val isCurrentMonth = date.month == yearMonth.month
                    val isToday = date == today

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clickable { onDateSelected(date, isCurrentMonth)},
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = date.dayOfMonth.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = when {
                                isToday -> Color(0xFFFF5722) // Colore per oggi
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