package com.walele.footballcalendarapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.consumePositionChange
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

    val initialMonthPage = monthYearList.indexOf(YearMonth.from(selectedDate.value))
    val monthPagerState = rememberPagerState(
        initialPage = initialMonthPage,
        pageCount = { monthYearList.size }
    )

    val currentMonthYear = remember { mutableStateOf(monthYearList[initialMonthPage]) }
    val coroutineScope = rememberCoroutineScope()

    val bottomPadding = WindowInsets.navigationBars
        .asPaddingValues()
        .calculateBottomPadding()

    // Stato per alternare vista mensile / annuale
    val isYearlyView = remember { mutableStateOf(false) }

    // Sync pager -> data
    LaunchedEffect(monthPagerState.currentPage) {
        currentMonthYear.value = monthYearList[monthPagerState.currentPage]
        val ym = monthYearList[monthPagerState.currentPage]
        val newDate = if (YearMonth.from(selectedDate.value) == ym)
            selectedDate.value
        else ym.atDay(1)
        selectedDate.value = newDate
    }

    // Sync data -> pager
    LaunchedEffect(selectedDate.value) {
        val newMonthIdx = monthYearList.indexOf(YearMonth.from(selectedDate.value))
        if (newMonthIdx != monthPagerState.currentPage) {
            coroutineScope.launch {
                monthPagerState.animateScrollToPage(newMonthIdx)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                WindowInsets.systemBars
                    .only(WindowInsetsSides.Top)
                    .asPaddingValues()
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            TopBar(
                currentMonthYear = currentMonthYear.value,
                onViewToggle = { isYearlyView.value = !isYearlyView.value }
            )

            AnimatedContent(
                targetState = isYearlyView.value,
                transitionSpec = {
                    (fadeIn() + scaleIn(initialScale = 0.9f)) togetherWith
                            (fadeOut() + scaleOut(targetScale = 0.9f))
                },
                label = "CalendarSwitch"
            ) { showYear ->
                if (showYear) {
                    YearlyCalendarView(
                        selectedDate = selectedDate.value,
                        onDateSelected = { date, _ ->
                            selectedDate.value = date
                            isYearlyView.value = false
                        },
                        startYear = startYear,
                        endYear = endYear
                    )
                } else {
                    HorizontalPager(
                        state = monthPagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) { page ->
                        CalendarView(
                            yearMonth = monthYearList[page],
                            selectedDate = selectedDate.value,
                            onDateSelected = { date, isCurrentMonth ->
                                coroutineScope.launch {
                                    selectedDate.value = date
                                    if (!isCurrentMonth) {
                                        val mi = monthYearList.indexOf(YearMonth.from(date))
                                        monthPagerState.animateScrollToPage(mi)
                                    }
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .pointerInput(selectedDate.value) {
                        var totalDragX = 0f
                        detectHorizontalDragGestures(
                            onHorizontalDrag = { change, dragAmount ->
                                totalDragX += dragAmount
                                change.consumePositionChange()
                            },
                            onDragEnd = {
                                val threshold = 100f
                                if (totalDragX > threshold) {
                                    selectedDate.value = selectedDate.value.minusDays(1)
                                } else if (totalDragX < -threshold) {
                                    selectedDate.value = selectedDate.value.plusDays(1)
                                }
                                totalDragX = 0f
                            }
                        )
                    }
            ) {
                MatchList(
                    matches = getMatchesForDate(selectedDate.value),
                    selectedDate = selectedDate.value,
                    bottomPadding = bottomPadding
                )
            }
        }
    }
}

