package com.walele.footballcalendarapp.data

data class Match(
    val id: Int,
    val homeTeam: Team,
    val awayTeam: Team,
    val date: String, // ISO date format
    val scoreHome: Int?,
    val scoreAway: Int?,
    val isCancelled: Boolean,
    val league: League
)
