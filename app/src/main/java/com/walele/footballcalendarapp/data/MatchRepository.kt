package com.walele.footballcalendarapp.data

import com.walele.footballcalendarapp.network.ApiService
import com.walele.footballcalendarapp.network.models.MatchDto
import com.walele.footballcalendarapp.network.models.MatchResponseDto
import com.walele.footballcalendarapp.data.local.MatchDao
import com.walele.footballcalendarapp.data.local.CachedMatch
import com.walele.footballcalendarapp.data.local.AppDatabase
import com.walele.footballcalendarapp.data.mappers.toCachedMatch
import com.walele.footballcalendarapp.data.mappers.toMatch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import android.util.Log
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class MatchRepository(
    private val apiService: ApiService,
    private val matchDao: MatchDao
) {

    suspend fun getMatches(
        date: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        selectedLeagues: List<Int> = listOf()
    ): List<Match> = withContext(Dispatchers.IO) {
        try {
            val twentyFourHoursAgo = System.currentTimeMillis() - 24 * 60 * 60 * 1000
            matchDao.deleteExpiredMatches(twentyFourHoursAgo)

            if (startDate != null && endDate != null) {
                val cachedMatches = matchDao.getMatchesBetween(startDate, endDate)
                if (cachedMatches.isNotEmpty()) {
                    Log.d("MatchRepository", "Returning ${cachedMatches.size} matches from cache")
                    return@withContext cachedMatches.map { it.toMatch() }
                }
            }

            val queryParams = mutableListOf<String>()
            if (startDate != null) queryParams.add("start_date=$startDate")
            if (endDate != null) queryParams.add("end_date=$endDate")
            if (date != null) queryParams.add("date=$date")
            selectedLeagues.forEach { queryParams.add("league=$it") }

            val queryString = queryParams.joinToString("&")
            val url = "https://football-calendar-backend.vercel.app/api/matches/?$queryString"

            Log.d("MatchRepository", "Request URL: $url")

            var response = apiService.getMatchesByUrl(url)
            val allMatches = mutableListOf<Match>()
            allMatches.addAll(response.results.map { it.toMatch() })

            while (response.next != null) {
                response = apiService.getMatchesByUrl(response.next!!)
                allMatches.addAll(response.results.map { it.toMatch() })
            }

            Log.d("MatchRepository", "Fetched ${allMatches.size} fresh matches")

            matchDao.insertMatches(allMatches.map { it.toCachedMatch() })

            return@withContext allMatches
        } catch (e: Exception) {
            Log.e("MatchRepository", "Error fetching matches: ${e.message}", e)
            return@withContext emptyList()
        }
    }

    suspend fun getMatchesForMonth(
        year: Int,
        month: Int,
        selectedLeagues: List<Int> = listOf()
    ): List<Match> = withContext(Dispatchers.IO) {
        try {
            val startDate = LocalDate.of(year, month, 1).toString()
            val endDate = LocalDate.of(year, month, 1).withDayOfMonth(
                LocalDate.of(year, month, 1).lengthOfMonth()
            ).toString()

            val twentyFourHoursAgo = System.currentTimeMillis() - 24 * 60 * 60 * 1000
            matchDao.deleteExpiredMatches(twentyFourHoursAgo)

            val cachedMatches = matchDao.getMatchesBetween(startDate, endDate)
            if (cachedMatches.isNotEmpty()) {
                Log.d("MatchRepository", "Returning matches from cache")
                return@withContext cachedMatches.map { it.toMatch() }
            }

            val queryParams = mutableListOf(
                "start_date=$startDate",
                "end_date=$endDate"
            )
            selectedLeagues.forEach { queryParams.add("league=$it") }

            val queryString = queryParams.joinToString("&")
            val url = "https://football-calendar-backend.vercel.app/api/matches/?$queryString"

            Log.d("MatchRepository", "Fetching matches for month: $url")

            var response = apiService.getMatchesByUrl(url)
            val allMatches = mutableListOf<Match>()
            allMatches.addAll(response.results.map { it.toMatch() })

            while (response.next != null) {
                response = apiService.getMatchesByUrl(response.next!!)
                allMatches.addAll(response.results.map { it.toMatch() })
            }

            matchDao.insertMatches(allMatches.map { it.toCachedMatch() })

            return@withContext allMatches
        } catch (e: Exception) {
            Log.e("MatchRepository", "Error fetching matches for month", e)
            return@withContext emptyList()
        }
    }

    suspend fun getMatchesForLeagueInMonth(
        leagueId: Int,
        year: Int,
        month: Int
    ): List<Match> = withContext(Dispatchers.IO) {
        try {
            val startDate = LocalDate.of(year, month, 1).toString()
            val endDate = LocalDate.of(year, month, 1).withDayOfMonth(
                LocalDate.of(year, month, 1).lengthOfMonth()
            ).toString()

            val twentyFourHoursAgo = System.currentTimeMillis() - 24 * 60 * 60 * 1000
            matchDao.deleteExpiredMatches(twentyFourHoursAgo)

            val cachedMatches = matchDao.getMatchesBetween(startDate, endDate)
                .filter { it.leagueId == leagueId }

            if (cachedMatches.isNotEmpty()) {
                Log.d("MatchRepository", "Returning ${cachedMatches.size} matches for league $leagueId from cache")
                return@withContext cachedMatches.map { it.toMatch() }
            }

            val queryParams = listOf(
                "start_date=$startDate",
                "end_date=$endDate",
                "league=$leagueId"
            ).joinToString("&")

            val url = "https://football-calendar-backend.vercel.app/api/matches/?$queryParams"

            Log.d("MatchRepository", "Fetching matches for league $leagueId: $url")

            var response = apiService.getMatchesByUrl(url)
            val allMatches = mutableListOf<Match>()
            allMatches.addAll(response.results.map { it.toMatch() })

            while (response.next != null) {
                response = apiService.getMatchesByUrl(response.next!!)
                allMatches.addAll(response.results.map { it.toMatch() })
            }

            matchDao.insertMatches(allMatches.map { it.toCachedMatch() })

            return@withContext allMatches
        } catch (e: Exception) {
            Log.e("MatchRepository", "Error fetching matches for league $leagueId", e)
            return@withContext emptyList()
        }
    }
}