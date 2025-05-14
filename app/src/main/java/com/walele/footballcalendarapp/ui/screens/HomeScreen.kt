package com.walele.footballcalendarapp.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
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
import com.walele.footballcalendarapp.ui.theme.InterVariableFont
import com.walele.footballcalendarapp.ui.theme.OnestVariableFont
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

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

    val startYear = 2024
    val endYear = 2027
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

    // Recupera il nome della lega selezionata
    val selectedLeagueName = leagues.value.find { it.id == selectedLeagueId.value }?.name

    // Sincronizza il pager con la data selezionata
    LaunchedEffect(selectedDate.value) {
        val targetYearMonth = YearMonth.from(selectedDate.value)
        val targetPage = monthYearList.indexOfFirst { it == targetYearMonth }
        if (targetPage != -1 && targetPage != monthPagerState.currentPage) {
            monthPagerState.animateScrollToPage(targetPage)
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
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
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Select League",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontFamily = OnestVariableFont,
                                    fontWeight = FontWeight.W900
                                ),
                                color = Color(0xFF2a1e17)
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.End
                        ) {
                            leagues.value.forEach { league ->
                                val isSelected = selectedLeagueId.value == league.id
                                TextButton(
                                    onClick = {
                                        selectedLeagueId.value = league.id
                                        coroutineScope.launch { drawerState.close() }
                                    }
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = league.name,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontFamily = OnestVariableFont,
                                                fontWeight = if (isSelected) FontWeight.W900 else FontWeight.W400,
                                                color = if (isSelected) Color(0xFF00A86B) else Color(0xFF2a1e17)
                                            ),
                                        )
                                        if (isSelected) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .background(color = Color(0xFF00A86B), shape = CircleShape)
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(18.dp))
                        }
                    }
                }
            }
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFf4ede8))
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
                                (fadeIn() + scaleIn(initialScale = 0.9f) + slideInVertically(initialOffsetY = { it })) togetherWith
                                        (fadeOut() + scaleOut(targetScale = 0.9f) + slideOutVertically(targetOffsetY = { it }))
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
                                    val currentMonth = monthYearList[page]
                                    val currentMonthMatches = matchesOfMonth.value.filter {
                                        YearMonth.from(LocalDate.parse(it.date)) == currentMonth
                                    }

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 16.dp)
                                    ) {
                                        CalendarView(
                                            yearMonth = currentMonth,
                                            selectedDate = selectedDate.value,
                                            onDateSelected = { date, isCurrentMonth ->
                                                selectedDate.value = date
                                                if (!isCurrentMonth) {
                                                    coroutineScope.launch {
                                                        val targetPage = monthYearList.indexOf(YearMonth.from(date))
                                                        monthPagerState.animateScrollToPage(targetPage)
                                                    }
                                                }
                                            },
                                            matchCountPerDay = currentMonthMatches
                                                .groupingBy { LocalDate.parse(it.date) }
                                                .eachCount(),
                                            maxMatchCount = currentMonthMatches
                                                .groupingBy { LocalDate.parse(it.date) }
                                                .eachCount()
                                                .values.maxOrNull() ?: 1
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(40.dp))

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
                                bottomPadding = bottomPadding,
                                leagueSelected = selectedLeagueId.value != null,
                                selectedLeagueName = selectedLeagueName
                            )
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        try {
            val result = leagueRepository.getAllLeagues()
            val sortedLeagues = result.sortedWith { league1, league2 ->
                when {
                    league1.name.contains("UEFA", true) && !league2.name.contains("UEFA", true) -> -1
                    !league1.name.contains("UEFA", true) && league2.name.contains("UEFA", true) -> 1
                    else -> league1.name.compareTo(league2.name)
                }
            }
            leagues.value = sortedLeagues
        } catch (e: Exception) {
            Log.e("HomeScreen", "Error loading leagues", e)
        }
    }

    LaunchedEffect(monthPagerState.currentPage) {
        val ym = monthYearList[monthPagerState.currentPage]
        if (currentMonthYear.value != ym) {
            currentMonthYear.value = ym
            selectedDate.value = ym.atDay(selectedDate.value.dayOfMonth.coerceAtMost(ym.lengthOfMonth()))
        }
    }

    LaunchedEffect(yearPagerState.currentPage) {
        val selectedYear = yearList[yearPagerState.currentPage]
        selectedDate.value = selectedDate.value.withYear(selectedYear)
    }

    LaunchedEffect(currentMonthYear.value, selectedLeagueId.value) {
        val leagueId = selectedLeagueId.value
        val currentYM = currentMonthYear.value

        if (leagueId == null) {
            matchesOfMonth.value = emptyList()
            matchesOfDay.value = emptyList()
            return@LaunchedEffect
        }

        try {
            // Carica il mese corrente
            val matches = matchRepository.getMatchesForLeagueInMonth(
                leagueId = leagueId,
                year = currentYM.year,
                month = currentYM.monthValue
            )
            Log.d("HomeScreen", "Loaded matches for $currentYM: ${matches.size} matches (current)")
            matchesOfMonth.value = matches
            matchesOfDay.value = matches.filter { it.date == selectedDate.value.toString() }

            // Precarica mesi precedente e successivo
            val previousYM = currentYM.minusMonths(1)
            val nextYM = currentYM.plusMonths(1)

            coroutineScope.launch {
                try {
                    matchRepository.getMatchesForLeagueInMonth(
                        leagueId = leagueId,
                        year = previousYM.year,
                        month = previousYM.monthValue
                    )
                    Log.d("HomeScreen", "Preloaded previous month: $previousYM")
                } catch (e: Exception) {
                    Log.e("HomeScreen", "Error preloading previous month", e)
                }
            }

            coroutineScope.launch {
                try {
                    matchRepository.getMatchesForLeagueInMonth(
                        leagueId = leagueId,
                        year = nextYM.year,
                        month = nextYM.monthValue
                    )
                    Log.d("HomeScreen", "Preloaded next month: $nextYM")
                } catch (e: Exception) {
                    Log.e("HomeScreen", "Error preloading next month", e)
                }
            }

        } catch (e: Exception) {
            Log.e("HomeScreen", "Error fetching matches", e)
            matchesOfMonth.value = emptyList()
            matchesOfDay.value = emptyList()
        }
    }

    LaunchedEffect(selectedDate.value) {
        matchesOfDay.value = matchesOfMonth.value.filter { it.date == selectedDate.value.toString() }
    }
}