package com.walele.footballcalendarapp.data

import android.util.Log
import com.walele.footballcalendarapp.network.ApiService
import com.walele.footballcalendarapp.data.local.LeagueDao
import com.walele.footballcalendarapp.network.models.LeagueResponseDto
import com.walele.footballcalendarapp.data.mappers.toCachedLeague
import com.walele.footballcalendarapp.data.mappers.toLeague
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class LeagueRepository(private val apiService: ApiService, private val leagueDao: LeagueDao) {

    suspend fun getAllLeagues(): List<League> {
        return withContext(Dispatchers.IO) {
            val currentTime = System.currentTimeMillis()
            val cachedLeagues = leagueDao.getAllLeagues()
            val cachedTime = leagueDao.getLastCachedTime()

            // Se la cache è più vecchia di una settimana, ricarichiamo i dati
            if (cachedLeagues.isNotEmpty() && (currentTime - cachedTime) < WEEK_IN_MILLIS) {
                return@withContext cachedLeagues.map { it.toLeague() }
            }

            // Se la cache è scaduta o vuota, ricarichiamo da API
            return@withContext fetchLeaguesFromApi()
        }
    }

    private suspend fun fetchLeaguesFromApi(): List<League> {
        val allLeagues = mutableListOf<League>()
        var nextUrl: String? = "https://football-calendar-backend.vercel.app/api/leagues/"

        try {
            while (nextUrl != null) {
                val response: LeagueResponseDto = apiService.getLeaguesByUrl(nextUrl)
                allLeagues.addAll(response.results.map { it.toLeague() })
                nextUrl = response.next
            }

            // Salviamo le leghe nel database e aggiorniamo il timestamp
            leagueDao.insertLeagues(allLeagues.map { it.toCachedLeague() })
            leagueDao.updateLastCachedTime(System.currentTimeMillis())

            Log.d("LeagueRepository", "Fetched leagues: ${allLeagues.size}")
            return allLeagues
        } catch (e: Exception) {
            Log.e("LeagueRepository", "Error fetching leagues: ${e.message}", e)
            return emptyList()
        }
    }

    companion object {
        private const val WEEK_IN_MILLIS = 7 * 24 * 60 * 60 * 1000L
    }
}