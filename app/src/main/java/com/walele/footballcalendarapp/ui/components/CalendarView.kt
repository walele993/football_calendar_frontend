package com.walele.footballcalendarapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.draw.alpha
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarView(
    yearMonth: YearMonth,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate, Boolean) -> Unit,
    matchCountPerDay: Map<LocalDate, Int> = emptyMap(),
    maxMatchCount: Int = 1
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
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
                    val matchCount = matchCountPerDay[date] ?: 0

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
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
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

                            if (matchCount > 0) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(
                                            color = Color(0xFF2196F3),
                                            shape = CircleShape
                                        )
                                        .alpha((matchCount.toFloat() / maxMatchCount).coerceIn(0.3f, 1f))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
