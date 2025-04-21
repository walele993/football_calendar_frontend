package com.walele.footballcalendarapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.ui.platform.LocalDensity
import com.walele.footballcalendarapp.ui.components.CalendarView
import com.walele.footballcalendarapp.ui.components.MatchList
import com.walele.footballcalendarapp.components.TopBar
import com.walele.footballcalendarapp.data.getMatchesForDate
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

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

    // Calcolo padding per la barra inferiore
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    LaunchedEffect(pagerState.currentPage) {
        currentMonthYear.value = monthYearList[pagerState.currentPage]
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                WindowInsets.systemBars
                    .only(WindowInsetsSides.Top) // solo padding superiore
                    .asPaddingValues()
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(currentMonthYear.value)

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

            // MatchList con bottomPadding passato correttamente
            Column(modifier = Modifier.weight(1f)) {
                MatchList(
                    matches = getMatchesForDate(selectedDate.value),
                    selectedDate = selectedDate.value,
                    bottomPadding = bottomPadding // Passiamo il bottomPadding qui
                )
            }
        }
    }
}