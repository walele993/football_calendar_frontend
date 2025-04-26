package com.walele.footballcalendarapp.data

import com.walele.footballcalendarapp.network.ApiService
import com.walele.footballcalendarapp.network.models.MatchDto
import com.walele.footballcalendarapp.network.models.MatchResponseDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import android.util.Log
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class MatchRepository(private val apiService: ApiService) {

    suspend fun getMatches(
        date: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        selectedLeagues: List<Int> = listOf()
    ): List<Match> {
        return withContext(Dispatchers.IO) {
            try {
                // Costruisci parametri dinamicamente
                val queryParams = mutableListOf<String>()

                if (startDate != null) queryParams.add("start_date=$startDate")
                if (endDate != null) queryParams.add("end_date=$endDate")
                if (selectedLeagues.isNotEmpty()) {
                    selectedLeagues.forEach { leagueId ->
                        queryParams.add("league=$leagueId")
                    }
                }

                val queryString = queryParams.joinToString("&")
                val url = "https://football-calendar-backend.vercel.app/api/matches/?" + queryString

                Log.d("MatchRepository", "Request URL: $url")

                val response: MatchResponseDto = apiService.getMatchesByUrl(url)

                Log.d("MatchRepository", "Fetched matches: ${response.results.size} matches found")

                return@withContext response.results.map { it.toMatch() }
            } catch (e: Exception) {
                Log.e("MatchRepository", "Error fetching matches: ${e.message}", e)
                return@withContext emptyList()
            }
        }
    }


    suspend fun getMatchesForMonth(
        startDate: String,
        endDate: String,
        selectedLeagues: List<Int> = listOf(14, 24, 36, 45, 49, 69, 15)
    ): List<Match> {
        return withContext(Dispatchers.IO) {
            val allMatches = mutableListOf<Match>()

            try {
                val deferredResults = selectedLeagues.map { leagueId ->
                    async {
                        val queryParams = listOf(
                            "start_date=$startDate",
                            "end_date=$endDate",
                            "league=$leagueId"
                        ).joinToString("&")

                        val initialUrl = "https://football-calendar-backend.vercel.app/api/matches/?$queryParams"

                        Log.d("MatchRepository", "Requesting URL: $initialUrl")

                        var response = apiService.getMatchesByUrl(initialUrl)
                        val matches = mutableListOf<Match>()

                        matches.addAll(response.results.map { it.toMatch() })

                        // Continua a seguire la paginazione
                        while (response.next != null) {
                            Log.d("MatchRepository", "Fetching next page: ${response.next}")
                            response = apiService.getMatchesByUrl(response.next!!)
                            matches.addAll(response.results.map { it.toMatch() })
                        }

                        return@async matches
                    }
                }

                deferredResults.awaitAll().forEach { matches ->
                    allMatches.addAll(matches)
                }

                Log.d("MatchRepository", "Total matches fetched for month: ${allMatches.size}")
                return@withContext allMatches
            } catch (e: Exception) {
                Log.e("MatchRepository", "Error in getMatchesForMonth", e)
                return@withContext allMatches
            }
        }
    }

    suspend fun getMatchesForLeagueByDay(
        startDate: String,
        endDate: String,
        leagueId: Int
    ): List<Match> {
        return withContext(Dispatchers.IO) {
            val allMatches = mutableListOf<Match>()

            try {
                // Calcola tutti i giorni tra startDate ed endDate
                val start = LocalDate.parse(startDate)
                val end = LocalDate.parse(endDate)
                val days = ChronoUnit.DAYS.between(start, end).toInt() + 1

                val deferredResults = (0 until days).map { offset ->
                    async {
                        val currentDate = start.plusDays(offset.toLong()).toString()

                        val queryParams = listOf(
                            "date=$currentDate",
                            "league=$leagueId"
                        ).joinToString("&")

                        val url = "https://football-calendar-backend.vercel.app/api/matches/?$queryParams"

                        Log.d("MatchRepository", "Fetching URL: $url")

                        try {
                            val response = apiService.getMatchesByUrl(url)
                            response.results.map { it.toMatch() }
                        } catch (e: Exception) {
                            Log.e("MatchRepository", "Error fetching matches for $currentDate", e)
                            emptyList()
                        }
                    }
                }

                deferredResults.awaitAll().forEach { matches ->
                    allMatches.addAll(matches)
                }

                Log.d("MatchRepository", "Total matches fetched for league $leagueId: ${allMatches.size}")
                return@withContext allMatches
            } catch (e: Exception) {
                Log.e("MatchRepository", "Error in getMatchesForLeagueByDay", e)
                return@withContext allMatches
            }
        }
    }

    suspend fun getMatchesForLeagueInMonth(
        leagueId: Int,
        year: Int,
        month: Int
    ): List<Match> {
        return withContext(Dispatchers.IO) {
            val allMatches = mutableListOf<Match>()

            try {
                val startDate = LocalDate.of(year, month, 1).toString() // tipo "2025-04-01"
                val endDate = LocalDate.of(year, month, 1)
                    .withDayOfMonth(LocalDate.of(year, month, 1).lengthOfMonth())
                    .toString() // tipo "2025-04-30"

                val queryParams = listOf(
                    "start_date=$startDate",
                    "end_date=$endDate",
                    "league=$leagueId"
                ).joinToString("&")

                val url = "https://football-calendar-backend.vercel.app/api/matches/?$queryParams"

                Log.d("MatchRepository", "Fetching matches for league $leagueId, URL: $url")

                var response = apiService.getMatchesByUrl(url)
                allMatches.addAll(response.results.map { it.toMatch() })

                // Gestione paginazione
                while (response.next != null) {
                    Log.d("MatchRepository", "Fetching next page: ${response.next}")
                    response = apiService.getMatchesByUrl(response.next!!)
                    allMatches.addAll(response.results.map { it.toMatch() })
                }

                Log.d("MatchRepository", "Total matches fetched for league $leagueId in month: ${allMatches.size}")

                return@withContext allMatches
            } catch (e: Exception) {
                Log.e("MatchRepository", "Error fetching matches for league $leagueId in month", e)
                return@withContext allMatches
            }
        }
    }

}

// Estensione per convertire MatchDto in Match
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
