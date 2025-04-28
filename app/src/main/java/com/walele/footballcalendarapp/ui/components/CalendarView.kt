package com.walele.footballcalendarapp.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth

fun calculateOpacity(matchCount: Int, maxMatchCount: Int): Float {
    val opacity = matchCount.toFloat() / maxMatchCount
    val adjustedOpacity = ((opacity * opacity * opacity) * 0.9f + 0.1f).coerceIn(0.25f, 1f)
    return adjustedOpacity
}

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
    val allDays = leadingDays + currentMonthDays + trailingDays
    val weeks = allDays.chunked(7)

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

        weeks.forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { date ->
                    DayCell(
                        modifier = Modifier.weight(1f),
                        date = date,
                        isSelected = date == selectedDate,
                        isCurrentMonth = date.month == yearMonth.month,
                        isToday = date == today,
                        matchCount = matchCountPerDay[date] ?: 0,
                        maxMatchCount = maxMatchCount,
                        onClick = { onDateSelected(date, date.month == yearMonth.month) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    modifier: Modifier = Modifier,
    date: LocalDate,
    isSelected: Boolean,
    isCurrentMonth: Boolean,
    isToday: Boolean,
    matchCount: Int,
    maxMatchCount: Int,
    onClick: () -> Unit
) {
    val selectionColor = Color(0xFFFF6B00) // arancione intenso
    val animatedCircleSize: Dp by animateDpAsState(
        targetValue = if (isSelected) 48.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "SelectionCircleSize"
    )

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (animatedCircleSize > 0.dp) {
            Box(
                modifier = Modifier
                    .size(animatedCircleSize)
                    .background(color = selectionColor, shape = CircleShape)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodySmall,
                color = when {
                    isSelected -> Color.White
                    isToday -> Color(0xFFFF6B00)
                    isCurrentMonth -> Color.Black
                    else -> Color.Gray
                }
            )

            if (matchCount > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                val opacity = calculateOpacity(matchCount, maxMatchCount)
                val ballColor = if (isSelected) Color.White else Color(0xFF00A86B).copy(alpha = opacity)

                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(color = ballColor, shape = CircleShape)
                )
            }
        }
    }
}
