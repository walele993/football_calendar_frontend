package com.walele.footballcalendarapp.network

import com.walele.footballcalendarapp.network.models.MatchResponseDto
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {

    @GET("api/matches/")
    suspend fun getMatches(
        @Query("date") date: String? = null,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null
    ): MatchResponseDto

    // Nuovo metodo per seguire la paginazione
    @GET
    suspend fun getMatchesByUrl(@Url url: String): MatchResponseDto
}
