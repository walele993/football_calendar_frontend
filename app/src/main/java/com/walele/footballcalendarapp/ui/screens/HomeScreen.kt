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
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import com.walele.footballcalendarapp.ui.components.YearlyCalendarView
import com.walele.footballcalendarapp.ui.components.CalendarView
import com.walele.footballcalendarapp.ui.components.MatchList
import com.walele.footballcalendarapp.ui.components.TopBar
import com.walele.footballcalendarapp.data.getMatchesForDate
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.Month

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

    val yearList = remember { (startYear..endYear).toList() } // Lista di anni
    val initialMonthPage = monthYearList.indexOf(YearMonth.from(selectedDate.value))
    val initialYearPage = yearList.indexOf(selectedDate.value.year) // Indice iniziale della yearly view
    val monthPagerState = rememberPagerState(
        initialPage = initialMonthPage,
        pageCount = { monthYearList.size }
    )
    val yearPagerState = rememberPagerState(
        initialPage = initialYearPage,
        pageCount = { yearList.size }
    )

    val currentMonthYear = remember { mutableStateOf(monthYearList[initialMonthPage]) }
    val coroutineScope = rememberCoroutineScope()

    val bottomPadding = WindowInsets.navigationBars
        .asPaddingValues()
        .calculateBottomPadding()

    // Stato per alternare vista mensile / annuale
    val isYearlyView = remember { mutableStateOf(false) }

    // Sync pager -> data per la vista mensile
    LaunchedEffect(monthPagerState.currentPage) {
        currentMonthYear.value = monthYearList[monthPagerState.currentPage]
        val ym = monthYearList[monthPagerState.currentPage]
        val newDate = if (YearMonth.from(selectedDate.value) == ym)
            selectedDate.value
        else ym.atDay(1)
        selectedDate.value = newDate
    }

    // Sync pager -> data per la vista annuale
    LaunchedEffect(yearPagerState.currentPage) {
        val selectedYear = yearList[yearPagerState.currentPage]
        selectedDate.value = LocalDate.of(selectedYear, selectedDate.value.monthValue, selectedDate.value.dayOfMonth)
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
            // Top bar con visualizzazione dell'anno solo nella yearly view
            TopBar(
                currentMonthYear = if (isYearlyView.value) {
                    YearMonth.of(yearList[yearPagerState.currentPage], 1) // Mostra solo l'anno
                } else {
                    currentMonthYear.value // Mostra mese e anno
                },
                isYearlyView = isYearlyView.value,  // Passa lo stato isYearlyView
                onViewToggle = { isYearlyView.value = !isYearlyView.value },  // Cambia la vista
                onMonthClick = { isYearlyView.value = true } // Vai alla vista annuale
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
                    HorizontalPager(
                        state = yearPagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) { page ->
                        YearlyCalendarView(
                            selectedDate = selectedDate.value,
                            onMonthSelected = { selectedYearMonth ->
                                val currentYear = yearList[yearPagerState.currentPage]
                                selectedDate.value = LocalDate.of(
                                    currentYear,
                                    selectedYearMonth.monthValue,  // Use monthValue (Int)
                                    1
                                )
                                isYearlyView.value = false
                            },
                            onDateSelected = { date, _ ->
                                selectedDate.value = date
                                isYearlyView.value = false
                            },
                            startYear = startYear,
                            endYear = endYear
                        )
                    }
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
