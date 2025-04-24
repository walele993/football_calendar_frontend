package com.walele.footballcalendarapp.data

import com.walele.footballcalendarapp.network.ApiService
import com.walele.footballcalendarapp.network.models.MatchResponseDto
import com.walele.footballcalendarapp.network.models.MatchDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MatchRepository(private val apiService: ApiService) {

    // Recupera tutti i match, con possibilità di filtrare per data
    suspend fun getMatches(date: String? = null, startDate: String? = null, endDate: String? = null): List<Match> {
        return withContext(Dispatchers.IO) {
            // Chiamata all'API per ottenere i dati, con eventuali parametri di filtro
            val response: MatchResponseDto = apiService.getMatches(
                date = date,
                startDate = startDate,
                endDate = endDate
            )

            // Mappa la risposta dell'API ai modelli locali
            response.results.map { matchDto ->
                matchDto.toMatch()
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
