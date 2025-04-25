package com.walele.footballcalendarapp.data

import android.util.Log
import com.walele.footballcalendarapp.network.ApiService
import com.walele.footballcalendarapp.network.models.LeagueDto
import com.walele.footballcalendarapp.network.models.LeagueResponseDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LeagueRepository(private val apiService: ApiService) {

    suspend fun getAllLeagues(): List<League> {
        return withContext(Dispatchers.IO) {
            val allLeagues = mutableListOf<League>()
            var nextUrl: String? = "https://football-calendar-backend.vercel.app/api/leagues/"

            try {
                while (nextUrl != null) {
                    val response: LeagueResponseDto = apiService.getLeaguesByUrl(nextUrl)
                    allLeagues.addAll(response.results.map { it.toLeague() })
                    nextUrl = response.next
                }

                Log.d("LeagueRepository", "Fetched leagues: ${allLeagues.size}")
                return@withContext allLeagues
            } catch (e: Exception) {
                Log.e("LeagueRepository", "Error fetching leagues: ${e.message}", e)
                return@withContext emptyList()
            }
        }
    }
}

// Estensione per mappare LeagueDto al modello locale League
fun LeagueDto.toLeague(): League {
    return League(
        id = this.id,
        name = this.name
    )
}
