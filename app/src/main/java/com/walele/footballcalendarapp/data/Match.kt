package com.walele.footballcalendarapp.data

data class Match(
    val id: Int,
    val homeTeam: Team,
    val awayTeam: Team,
    val date: String,
    val time: String,
    val scoreHome: Int?,
    val scoreAway: Int?,
    val isCancelled: Boolean,
    val league: League
)
