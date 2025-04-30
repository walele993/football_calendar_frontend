package com.walele.footballcalendarapp.data.mappers

import com.walele.footballcalendarapp.data.Match
import com.walele.footballcalendarapp.data.Team
import com.walele.footballcalendarapp.data.League
import com.walele.footballcalendarapp.data.local.CachedMatch
import com.walele.footballcalendarapp.network.models.MatchDto

fun Match.toCachedMatch(): CachedMatch {
    return CachedMatch(
        id = id,
        homeTeamId = homeTeam.id,
        homeTeamName = homeTeam.name,
        awayTeamId = awayTeam.id,
        awayTeamName = awayTeam.name,
        date = date,
        time = time,
        scoreHome = scoreHome,
        scoreAway = scoreAway,
        isCancelled = isCancelled,
        leagueId = league.id,
        leagueName = league.name,
        cachedAt = System.currentTimeMillis()
    )
}

fun CachedMatch.toMatch(): Match {
    return Match(
        id = id,
        homeTeam = Team(homeTeamId, homeTeamName),
        awayTeam = Team(awayTeamId, awayTeamName),
        date = date,
        time = time,
        scoreHome = scoreHome,
        scoreAway = scoreAway,
        isCancelled = isCancelled,
        league = League(leagueId, leagueName)
    )
}

fun MatchDto.toMatch(): Match {
    return Match(
        id = this.id,
        homeTeam = Team(this.home_team.id, this.home_team.name),
        awayTeam = Team(this.away_team.id, this.away_team.name),
        date = this.date,
        time = this.time,
        scoreHome = this.score_home,
        scoreAway = this.score_away,
        isCancelled = this.is_cancelled,
        league = League(this.league.id, this.league.name)
    )
}