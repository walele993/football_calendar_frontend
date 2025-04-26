package com.walele.footballcalendarapp.ui.screens

import android.util.Log
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import com.walele.footballcalendarapp.ui.components.YearlyCalendarView
import com.walele.footballcalendarapp.ui.components.CalendarView
import com.walele.footballcalendarapp.ui.components.MatchList
import com.walele.footballcalendarapp.ui.components.TopBar
import com.walele.footballcalendarapp.data.Match
import com.walele.footballcalendarapp.data.League
import com.walele.footballcalendarapp.data.MatchRepository
import com.walele.footballcalendarapp.data.LeagueRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.Month

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(matchRepository: MatchRepository, leagueRepository: LeagueRepository) {
    val selectedDate = remember { mutableStateOf(LocalDate.now()) }
    val matchesOfMonth = remember { mutableStateOf<List<Match>>(emptyList()) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var showBottomSheet by remember { mutableStateOf(false) }

    val startYear = 2020
    val endYear = 2026
    val monthYearList = remember {
        (startYear..endYear).flatMap { year ->
            (1..12).map { month -> YearMonth.of(year, month) }
        }
    }

    val yearList = remember { (startYear..endYear).toList() }
    val initialMonthPage = monthYearList.indexOf(YearMonth.from(selectedDate.value))
    val initialYearPage = yearList.indexOf(selectedDate.value.year)

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

    val isYearlyView = remember { mutableStateOf(false) }

    // Sync pager -> selectedDate
    LaunchedEffect(monthPagerState.currentPage) {
        val ym = monthYearList[monthPagerState.currentPage]
        currentMonthYear.value = ym

        val newDate = ym.atDay(selectedDate.value.dayOfMonth.coerceAtMost(ym.lengthOfMonth()))

        Log.d("HomeScreen", "monthPagerState.currentPage changed: $ym, newDate: $newDate")

        if (selectedDate.value != newDate) {
            selectedDate.value = newDate
        }
    }

    LaunchedEffect(yearPagerState.currentPage) {
        val selectedYear = yearList[yearPagerState.currentPage]
        selectedDate.value = LocalDate.of(selectedYear, selectedDate.value.monthValue, selectedDate.value.dayOfMonth)

        Log.d("HomeScreen", "yearPagerState.currentPage changed: $selectedYear, selectedDate: $selectedDate")
    }

    // Scarica la lista di leghe
    val leagues = remember { mutableStateOf<List<League>>(emptyList()) }

    LaunchedEffect(Unit) {
        try {
            val result = leagueRepository.getAllLeagues()

            // Ordina le leghe: prima quelle con "UEFA" e poi le altre in ordine alfabetico
            val sortedLeagues = result.sortedWith { league1, league2 ->
                when {
                    league1.name.contains("UEFA", ignoreCase = true) && !league2.name.contains("UEFA", ignoreCase = true) -> -1
                    !league1.name.contains("UEFA", ignoreCase = true) && league2.name.contains("UEFA", ignoreCase = true) -> 1
                    else -> league1.name.compareTo(league2.name)
                }
            }

            leagues.value = sortedLeagues
            Log.d("HomeScreen", "Loaded ${sortedLeagues.size} leagues")
        } catch (e: Exception) {
            Log.e("HomeScreen", "Error loading leagues", e)
        }
    }

    // Scarica i match per il mese corrente
    LaunchedEffect(currentMonthYear.value) {
        try {
            val startOfMonth = currentMonthYear.value.atDay(1)
            val endOfMonth = currentMonthYear.value.atEndOfMonth()

            // Log prima della chiamata
            Log.d("HomeScreen", "Fetching matches from ${startOfMonth} to ${endOfMonth}")

            val monthlyMatches = matchRepository.getMatchesForMonth(
                startDate = startOfMonth.toString(),
                endDate = endOfMonth.toString()
            )

            // Log dopo la chiamata
            Log.d("HomeScreen", "Received ${monthlyMatches.size} matches")

            matchesOfMonth.value = monthlyMatches

            // Log per verificare le partite per la data selezionata
            val matchesForSelectedDate = monthlyMatches.filter {
                LocalDate.parse(it.date) == selectedDate.value
            }
            Log.d("HomeScreen", "Matches for ${selectedDate.value}: ${matchesForSelectedDate.size}")
        } catch (e: Exception) {
            Log.e("HomeScreen", "Error fetching matches", e)
            matchesOfMonth.value = emptyList()
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
                currentMonthYear = if (isYearlyView.value) {
                    YearMonth.of(yearList[yearPagerState.currentPage], 1)
                } else {
                    currentMonthYear.value
                },
                isYearlyView = isYearlyView.value,
                onViewToggle = { isYearlyView.value = !isYearlyView.value },
                onMonthClick = { isYearlyView.value = true },
                onFilterClick = { showBottomSheet = true }
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
                                    selectedYearMonth.monthValue,
                                    1
                                )
                                isYearlyView.value = false

                                // Aggiorna monthPagerState
                                val mi = monthYearList.indexOf(YearMonth.of(currentYear, selectedYearMonth.monthValue))
                                coroutineScope.launch {
                                    monthPagerState.animateScrollToPage(mi)
                                }
                            },
                            onDateSelected = { date, _ ->
                                selectedDate.value = date
                                isYearlyView.value = false

                                // Aggiorna monthPagerState
                                val mi = monthYearList.indexOf(YearMonth.from(date))
                                coroutineScope.launch {
                                    monthPagerState.animateScrollToPage(mi)
                                }
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
                    matches = matchesOfMonth.value.filter {
                        LocalDate.parse(it.date) == selectedDate.value
                    },
                    selectedDate = selectedDate.value,
                    bottomPadding = bottomPadding
                )
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 0.dp)
                    .wrapContentHeight()
            ) {
                Text(
                    text = "Select Leagues",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(bottom = 5.dp)
                )

                if (leagues.value.isEmpty()) {
                    Text(
                        text = "No leagues available.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    // Lista scrollabile di leghe
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        leagues.value.forEach { league ->
                            Text(
                                text = league.name,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}