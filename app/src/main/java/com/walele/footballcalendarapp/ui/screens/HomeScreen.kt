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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
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
    val matchesOfMonth = remember { mutableStateOf<List<Match>>(emptyList()) } // 👈 Aggiunto
    val selectedLeagueId = remember { mutableStateOf<Int?>(null) }

    val isYearlyView = remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }

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

    // Carica tutte le leghe
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

    // Quando cambia mese
    LaunchedEffect(monthPagerState.currentPage) {
        val ym = monthYearList[monthPagerState.currentPage]
        if (currentMonthYear.value != ym) {
            currentMonthYear.value = ym
            selectedDate.value = ym.atDay(selectedDate.value.dayOfMonth.coerceAtMost(ym.lengthOfMonth()))
        }
    }

    // Quando cambia anno
    LaunchedEffect(yearPagerState.currentPage) {
        val selectedYear = yearList[yearPagerState.currentPage]
        selectedDate.value = selectedDate.value.withYear(selectedYear)
    }

    // 👇 FETCH SOLO AL CAMBIO MESE O LEGA
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

            Log.d("HomeScreen", "Fetching matches for league $leagueId in $year-$month")

            val matches = matchRepository.getMatchesForLeagueInMonth(
                leagueId = leagueId,
                year = year,
                month = month
            )

            matchesOfMonth.value = matches

            // Dopo aver caricato il mese, aggiorna anche il giorno selezionato
            matchesOfDay.value = matches.filter { it.date == selectedDate.value.toString() }

            Log.d("HomeScreen", "Loaded ${matches.size} matches for the month")
        } catch (e: Exception) {
            Log.e("HomeScreen", "Error fetching monthly matches", e)
            matchesOfMonth.value = emptyList()
            matchesOfDay.value = emptyList()
        }
    }

    // 👇 FILTRA I MATCH LOCALI QUANDO CAMBIA GIORNO
    LaunchedEffect(selectedDate.value) {
        matchesOfDay.value = matchesOfMonth.value.filter { it.date == selectedDate.value.toString() }
    }

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
                    matches = matchesOfDay.value,
                    selectedDate = selectedDate.value,
                    bottomPadding = bottomPadding
                )
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Select League",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                leagues.value.forEach { league ->
                    TextButton(
                        onClick = {
                            selectedLeagueId.value = league.id
                            showBottomSheet = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = league.name,
                            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onBackground)
                        )
                    }
                }
            }
        }
    }
}
