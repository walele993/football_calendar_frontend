package com.walele.footballcalendarapp.data

import android.util.Log

import com.walele.footballcalendarapp.data.local.MatchDao
import com.walele.footballcalendarapp.data.mappers.toCachedMatch
import com.walele.footballcalendarapp.data.mappers.toMatch
import com.walele.footballcalendarapp.network.ApiService
import com.walele.footballcalendarapp.data.Match
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

import java.time.LocalDate

class MatchRepository(
    private val apiService: ApiService,
    private val matchDao: MatchDao
) {

    suspend fun getMatches(
        date: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        selectedLeagues: List<Int> = emptyList()
    ): List<Match> = withContext(Dispatchers.IO) {
        try {
            // Elimina cache più vecchia di 24 ore
            val twentyFourHoursAgo = System.currentTimeMillis() - 24 * 60 * 60 * 1000
            matchDao.deleteExpiredMatches(twentyFourHoursAgo)

            // Verifica la cache esistente solo se sono selezionate le leghe
            if (startDate != null && endDate != null && selectedLeagues.isNotEmpty()) {
                val cached = matchDao.getMatchesBetween(startDate, endDate)
                val filteredCache = cached.filter { it.leagueId in selectedLeagues }

                // Se ci sono dati in cache per la lega selezionata, usali
                if (filteredCache.isNotEmpty()) {
                    Log.d("MatchRepository", "Returning ${filteredCache.size} matches from cache")
                    return@withContext filteredCache.map { it.toMatch() }
                }
            }

            // Richieste parallele per ogni lega selezionata se la cache è vuota o non valida
            val matches = selectedLeagues.map { leagueId ->
                async {
                    Log.d("MatchRepository", "Fetching matches for league $leagueId")
                    val response = apiService.getFilteredMatches(
                        date = date,
                        startDate = startDate,
                        endDate = endDate,
                        league = leagueId
                    )
                    response.map { it.toMatch() }
                }
            }.awaitAll().flatten()

            // Inserisci i nuovi match nella cache
            Log.d("MatchRepository", "Fetched ${matches.size} fresh matches")
            matchDao.insertMatches(matches.map { it.toCachedMatch() })

            return@withContext matches
        } catch (e: Exception) {
            Log.e("MatchRepository", "Error fetching matches", e)
            return@withContext emptyList()
        }
    }

    suspend fun getMatchesForMonth(
        year: Int,
        month: Int,
        selectedLeagues: List<Int> = emptyList()
    ): List<Match> = withContext(Dispatchers.IO) {
        val startDate = LocalDate.of(year, month, 1).toString()
        val endDate = LocalDate.of(year, month, 1).withDayOfMonth(
            LocalDate.of(year, month, 1).lengthOfMonth()
        ).toString()

        getMatches(
            startDate = startDate,
            endDate = endDate,
            selectedLeagues = selectedLeagues
        )
    }

    suspend fun getMatchesForLeagueInMonth(
        leagueId: Int,
        year: Int,
        month: Int
    ): List<Match> = withContext(Dispatchers.IO) {
        val startDate = LocalDate.of(year, month, 1).toString()
        val endDate = LocalDate.of(year, month, 1).withDayOfMonth(
            LocalDate.of(year, month, 1).lengthOfMonth()
        ).toString()

        getMatches(
            startDate = startDate,
            endDate = endDate,
            selectedLeagues = listOf(leagueId)
        )
    }
}
