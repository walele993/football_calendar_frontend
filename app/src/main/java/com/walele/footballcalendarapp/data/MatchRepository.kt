package com.walele.footballcalendarapp.data

import android.util.Log
import com.walele.footballcalendarapp.network.ApiService
import com.walele.footballcalendarapp.network.models.MatchResponseDto
import com.walele.footballcalendarapp.network.models.MatchDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MatchRepository(private val apiService: ApiService) {

    suspend fun getMatches(date: String? = null, startDate: String? = null, endDate: String? = null): List<Match> {
        return withContext(Dispatchers.IO) {
            try {
                // Log prima della chiamata API
                Log.d("MatchRepository", "Fetching matches with date: $date, startDate: $startDate, endDate: $endDate")

                val response: MatchResponseDto = apiService.getMatches(
                    date = date,
                    startDate = startDate,
                    endDate = endDate
                )

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

    // Nuovo metodo per scaricare tutte le partite di un mese, gestendo la paginazione
    suspend fun getMatchesForMonth(startDate: String, endDate: String): List<Match> {
        return withContext(Dispatchers.IO) {
            val allMatches = mutableListOf<Match>()
            try {
                // Log della richiesta iniziale
                Log.d("MatchRepository", "Initial request for $startDate to $endDate")

                // Prima richiesta
                var response = apiService.getMatches(
                    startDate = startDate,
                    endDate = endDate
                )
                allMatches.addAll(response.results.map { it.toMatch() })

                // Log della prima risposta
                Log.d("MatchRepository", "First page: ${response.results.size} matches, next: ${response.next}")

                // Paginazione
                while (response.next != null) {
                    Log.d("MatchRepository", "Fetching next page: ${response.next}")
                    response = apiService.getMatchesByUrl(response.next!!)
                    allMatches.addAll(response.results.map { it.toMatch() })
                    Log.d("MatchRepository", "Added ${response.results.size} more matches, next: ${response.next}")
                }

                Log.d("MatchRepository", "Total matches fetched: ${allMatches.size}")
                return@withContext allMatches
            } catch (e: Exception) {
                Log.e("MatchRepository", "Error in getMatchesForMonth", e)
                return@withContext allMatches // Restituisci comunque quelli recuperati
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
