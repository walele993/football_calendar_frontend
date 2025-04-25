package com.walele.footballcalendarapp.network.models

data class MatchDto(
    val id: Int,
    val home_team: TeamDto,
    val away_team: TeamDto,
    val date: String,
    val time: String,
    val score_home: Int?,
    val score_away: Int?,
    val is_cancelled: Boolean,
    val league: LeagueDto
)
