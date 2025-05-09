package com.walele.footballcalendarapp.network

import com.walele.footballcalendarapp.network.models.MatchResponseDto
import com.walele.footballcalendarapp.network.models.LeagueResponseDto
import com.walele.footballcalendarapp.network.models.MatchDto
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {

    @GET("api/matches-mongo/filter/")
    suspend fun getFilteredMatches(
        @Query("date") date: String? = null,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("league") league: Int? = null
    ): List<MatchDto>

    // Nuovo metodo per seguire la paginazione
    @GET
    suspend fun getMatchesByUrl(@Url url: String): MatchResponseDto

    @GET("api/leagues-mongo/")
    suspend fun getLeagues(): LeagueResponseDto
}
