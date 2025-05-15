package com.walele.footballcalendarapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.walele.footballcalendarapp.ui.theme.OnestVariableFont
import com.walele.footballcalendarapp.ui.theme.PinkAccent
import com.walele.footballcalendarapp.ui.theme.PrimaryTextLight
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@Composable
fun YearlyCalendarView(
    startYear: Int = 2024,
    endYear: Int = 2027,
    selectedDate: LocalDate,
    onMonthSelected: (YearMonth) -> Unit,
    onDateSelected: (LocalDate, Boolean) -> Unit,
) {
    val year = selectedDate.year
    val months = remember(year) { (1..12).map { YearMonth.of(year, it) } }

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
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontFamily = OnestVariableFont,
                        fontWeight = FontWeight.W400,
                        color = PrimaryTextLight
                    ),
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
    val firstDayOfWeek = (firstDayOfMonth.dayOfWeek.value % 7)
    val daysInMonth = yearMonth.lengthOfMonth()

    val currentMonthDays = remember(yearMonth) {
        (1..daysInMonth).map {
            LocalDate.of(yearMonth.year, yearMonth.month, it)
        }
    }

    val totalCells = firstDayOfWeek + daysInMonth
    val rows = totalCells / 7 + if (totalCells % 7 != 0) 1 else 0

    Column(modifier = Modifier.fillMaxWidth()) {
        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0..6) {
                    val index = row * 7 + col
                    val date = if (index >= firstDayOfWeek && index < firstDayOfWeek + daysInMonth) {
                        currentMonthDays[index - firstDayOfWeek]
                    } else {
                        null
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clickable(enabled = date != null) {
                                date?.let { onDateSelected(it, true) }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (date != null) {
                            val isToday = date == today
                            Text(
                                text = date.dayOfMonth.toString(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = OnestVariableFont,
                                    fontWeight = FontWeight.W400
                                ),
                                color = when {
                                    isToday -> PinkAccent
                                    else -> PrimaryTextLight
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
