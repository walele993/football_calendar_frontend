package com.walele.footballcalendarapp.data

import com.walele.footballcalendarapp.network.ApiService
import com.walele.footballcalendarapp.network.models.MatchResponseDto
import com.walele.footballcalendarapp.network.models.MatchDto
import kotlinx.coroutines.withContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import android.util.Log
import kotlinx.coroutines.Dispatchers
import java.time.LocalDate


class MatchRepository(private val apiService: ApiService) {

    // Nuovo metodo che accetta le leghe selezionate
    suspend fun getMatches(date: String? = null, startDate: String? = null, endDate: String? = null, selectedLeagues: List<Int> = listOf(14, 24, 36, 45, 49, 69, 15)): List<Match> {
        return withContext(Dispatchers.IO) {
            try {
                // Log prima della chiamata API
                Log.d("MatchRepository", "Fetching matches with date: $date, startDate: $startDate, endDate: $endDate, leagues: $selectedLeagues")

                // Costruisci i parametri delle leghe
                val leagueParams = selectedLeagues.joinToString(separator = "&") { "league=$it" }

                // Costruisci l'URL con le leghe selezionate
                val url = "https://football-calendar-backend.vercel.app/api/matches/?start_date=$startDate&end_date=$endDate&$leagueParams"

                // Fai la richiesta API con l'URL personalizzato
                val response: MatchResponseDto = apiService.getMatchesByUrl(url)

                // Log della risposta API
                Log.d("MatchRepository", "Fetched matches: ${response.results.size} matches found")

                return@withContext response.results.map { matchDto ->
                    matchDto.toMatch()
                }
            } catch (e: Exception) {
                // Log dell'errore
                Log.e("MatchRepository", "Error fetching matches: ${e.message}", e)
                return@withContext emptyList<Match>()
            }
        }
    }

    // Nuovo metodo per scaricare tutte le partite di un mese con le leghe selezionate
    suspend fun getMatchesForMonth(startDate: String, endDate: String, selectedLeagues: List<Int> = listOf(14, 24, 36, 45, 49, 69, 15)): List<Match> {
        return withContext(Dispatchers.IO) {
            val allMatches = mutableListOf<Match>()

            try {
                // Avvia tutte le richieste in parallelo
                val deferredResults = selectedLeagues.map { leagueId ->
                    async {
                        val url = "https://football-calendar-backend.vercel.app/api/matches/?start_date=$startDate&end_date=$endDate&league=$leagueId"
                        var response = apiService.getMatchesByUrl(url)
                        val matches = mutableListOf<Match>()

                        matches.addAll(response.results.map { it.toMatch() })

                        // Gestione paginazione per la lega corrente
                        while (response.next != null) {
                            response = apiService.getMatchesByUrl(response.next!!)
                            matches.addAll(response.results.map { it.toMatch() })
                        }

                        return@async matches
                    }
                }

                // Ascolta i risultati da tutte le richieste parallele
                deferredResults.awaitAll().forEach { allMatches.addAll(it) }

                Log.d("MatchRepository", "Total matches fetched: ${allMatches.size}")
                return@withContext allMatches
            } catch (e: Exception) {
                Log.e("MatchRepository", "Error in getMatchesForMonth", e)
                return@withContext allMatches
            }
        }
    }
}

// Funzione di estensione per mappare il MatchDto al modello locale Match
fun MatchDto.toMatch(): Match {
    return Match(
        id = this.id,
        homeTeam = Team(this.home_team.id, this.home_team.name),
        awayTeam = Team(this.away_team.id, this.away_team.name),
        date = this.date,
        scoreHome = this.score_home,
        scoreAway = this.score_away,
        isCancelled = this.is_cancelled,
        league = League(this.league.id, this.league.name)
    )
}
