package com.walele.footballcalendarapp.network

import com.walele.footballcalendarapp.network.models.MatchResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("api/matches/")
    suspend fun getMatches(
        @Query("date") date: String? = null,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null
    ): MatchResponseDto
}
