package com.walele.footballcalendarapp.data.local

import androidx.room.*

@Dao
interface MatchDao {

    @Query("SELECT * FROM cached_matches WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getMatchesBetween(startDate: String, endDate: String): List<CachedMatch>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatches(matches: List<CachedMatch>)

    @Query("DELETE FROM cached_matches WHERE cachedAt < :expirationTime")
    suspend fun deleteExpiredMatches(expirationTime: Long)
}