package com.walele.footballcalendarapp.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.walele.footballcalendarapp.data.League
import com.walele.footballcalendarapp.data.LeagueRepository
import com.walele.footballcalendarapp.data.Match
import com.walele.footballcalendarapp.data.MatchRepository
import com.walele.footballcalendarapp.ui.components.CalendarView
import com.walele.footballcalendarapp.ui.components.MatchList
import com.walele.footballcalendarapp.ui.components.TopBar
import com.walele.footballcalendarapp.ui.components.YearlyCalendarView
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    matchRepository: MatchRepository,
    leagueRepository: LeagueRepository
) {
    val coroutineScope = rememberCoroutineScope()

    val selectedDate = remember { mutableStateOf(LocalDate.now()) }
    val matchesOfDay = remember { mutableStateOf<List<Match>>(emptyList()) }
    val matchesOfMonth = remember { mutableStateOf<List<Match>>(emptyList()) }
    val selectedLeagueId = remember { mutableStateOf<Int?>(null) }

    val isYearlyView = remember { mutableStateOf(false) }

    val startYear = 2020
    val endYear = 2026
    val monthYearList = remember {
        (startYear..endYear).flatMap { year -> (1..12).map { month -> YearMonth.of(year, month) } }
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

    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    val leagues = remember { mutableStateOf<List<League>>(emptyList()) }

    val drawerState = rememberDrawerState(DrawerValue.Closed)

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {  // Cambia da Rtl a Ltr
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = drawerState.isOpen,
            drawerContent = {
                ModalDrawerSheet {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp)
                    ) {
                        // Titolo fisso con più spazio sotto
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),  // Aumenta lo spazio sotto il titolo
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Select League",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        // Lista scrollabile di leghe
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.End
                        ) {
                            leagues.value.forEach { league ->
                                TextButton(
                                    onClick = {
                                        selectedLeagueId.value = league.id
                                        coroutineScope.launch { drawerState.close() }
                                    }
                                ) {
                                    Text(
                                        text = league.name,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            color = MaterialTheme.colorScheme.onBackground
                                        ),
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))
                        }
                    }
                }
            }

        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                // --- HomeScreen normale qui ---
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(WindowInsets.systemBars.only(WindowInsetsSides.Top).asPaddingValues())
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
                            onFilterClick = {
                                coroutineScope.launch { drawerState.open() }
                            }
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
                                    modifier = Modifier.fillMaxWidth()
                                ) { page ->
                                    YearlyCalendarView(
                                        selectedDate = selectedDate.value,
                                        onMonthSelected = { selectedYearMonth ->
                                            val year = yearList[page]
                                            val date = LocalDate.of(year, selectedYearMonth.monthValue, 1)
                                            selectedDate.value = date
                                            isYearlyView.value = false

                                            coroutineScope.launch {
                                                val index = monthYearList.indexOf(YearMonth.from(date))
                                                monthPagerState.animateScrollToPage(index)
                                            }
                                        },
                                        onDateSelected = { date, _ ->
                                            selectedDate.value = date
                                            isYearlyView.value = false

                                            coroutineScope.launch {
                                                val index = monthYearList.indexOf(YearMonth.from(date))
                                                monthPagerState.animateScrollToPage(index)
                                            }
                                        },
                                        startYear = startYear,
                                        endYear = endYear
                                    )
                                }
                            } else {
                                HorizontalPager(
                                    state = monthPagerState,
                                    modifier = Modifier.fillMaxWidth()
                                ) { page ->
                                    val currentMonthMatches = matchesOfMonth.value.filter {
                                        YearMonth.from(LocalDate.parse(it.date)) == monthYearList[page]
                                    }

                                    CalendarView(
                                        yearMonth = monthYearList[page],
                                        selectedDate = selectedDate.value,
                                        onDateSelected = { date, isCurrentMonth ->
                                            selectedDate.value = date
                                            if (!isCurrentMonth) {
                                                coroutineScope.launch {
                                                    val index = monthYearList.indexOf(YearMonth.from(date))
                                                    monthPagerState.animateScrollToPage(index)
                                                }
                                            }
                                        },
                                        matchCountPerDay = currentMonthMatches
                                            .groupingBy { LocalDate.parse(it.date) }
                                            .eachCount(),
                                        maxMatchCount = currentMonthMatches
                                            .groupingBy { LocalDate.parse(it.date) }
                                            .eachCount()
                                            .values
                                            .maxOrNull() ?: 1
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
                            Log.d("HomeScreen", "Selected league ID: ${selectedLeagueId.value}")
                            MatchList(
                                matches = matchesOfDay.value,
                                selectedDate = selectedDate.value,
                                bottomPadding = bottomPadding,
                                leagueSelected = selectedLeagueId.value != null,
                            )
                        }
                    }
                }
            }
        }
    }

    // Caricamento leghe
    LaunchedEffect(Unit) {
        try {
            val result = leagueRepository.getAllLeagues()

            val sortedLeagues = result.sortedWith { league1, league2 ->
                when {
                    league1.name.contains("UEFA", ignoreCase = true) && !league2.name.contains("UEFA", ignoreCase = true) -> -1
                    !league1.name.contains("UEFA", ignoreCase = true) && league2.name.contains("UEFA", ignoreCase = true) -> 1
                    else -> league1.name.compareTo(league2.name)
                }
            }

            leagues.value = sortedLeagues
        } catch (e: Exception) {
            Log.e("HomeScreen", "Error loading leagues", e)
        }
    }

    // Cambio mese
    LaunchedEffect(monthPagerState.currentPage) {
        val ym = monthYearList[monthPagerState.currentPage]
        if (currentMonthYear.value != ym) {
            currentMonthYear.value = ym
            selectedDate.value = ym.atDay(selectedDate.value.dayOfMonth.coerceAtMost(ym.lengthOfMonth()))
        }
    }

    // Cambio anno
    LaunchedEffect(yearPagerState.currentPage) {
        val selectedYear = yearList[yearPagerState.currentPage]
        selectedDate.value = selectedDate.value.withYear(selectedYear)
    }

    // Fetch match del mese
    LaunchedEffect(currentMonthYear.value, selectedLeagueId.value) {
        val leagueId = selectedLeagueId.value
        if (leagueId == null) {
            matchesOfMonth.value = emptyList()
            matchesOfDay.value = emptyList()
            return@LaunchedEffect
        }

        try {
            val year = currentMonthYear.value.year
            val month = currentMonthYear.value.monthValue

            val matches = matchRepository.getMatchesForLeagueInMonth(
                leagueId = leagueId,
                year = year,
                month = month
            )

            matchesOfMonth.value = matches
            matchesOfDay.value = matches.filter { it.date == selectedDate.value.toString() }

        } catch (e: Exception) {
            Log.e("HomeScreen", "Error fetching monthly matches", e)
            matchesOfMonth.value = emptyList()
            matchesOfDay.value = emptyList()
        }
    }

    // Cambio giorno
    LaunchedEffect(selectedDate.value) {
        matchesOfDay.value = matchesOfMonth.value.filter { it.date == selectedDate.value.toString() }
    }
}