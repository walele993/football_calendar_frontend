package com.walele.footballcalendarapp.data

import java.time.LocalDate

data class Match(val time: String, val home: String, val away: String, val league: String)

fun getMatchesForDate(date: LocalDate): List<Match> {
    return when (date.dayOfMonth) {
        20 -> listOf(
            Match("12:30", "Real Madrid", "Barcelona", "La Liga"),
            Match("14:00", "Napoli", "Juventus", "Serie A"),
            Match("15:45", "Liverpool", "Arsenal", "Premier League"),
            Match("17:30", "Bayern", "PSG", "Champions League"),
            Match("19:00", "Ajax", "Feyenoord", "Eredivisie"),
            Match("21:00", "Chelsea", "Man City", "Premier League")
        )
        21 -> listOf(
            Match("13:00", "Milan", "Inter", "Serie A"),
            Match("15:00", "Leverkusen", "Dortmund", "Bundesliga"),
            Match("17:45", "Lazio", "Roma", "Serie A"),
            Match("20:00", "Tottenham", "Brighton", "Premier League"),
            Match("22:00", "Porto", "Benfica", "Liga Portugal")
        )
        else -> emptyList()
    }
}
