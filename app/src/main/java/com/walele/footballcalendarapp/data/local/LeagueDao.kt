package com.walele.footballcalendarapp.data.local

import androidx.room.*

@Dao
interface LeagueDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeagues(leagues: List<CachedLeague>)

    @Query("SELECT * FROM cached_league")
    suspend fun getAllLeagues(): List<CachedLeague>

    @Query("SELECT lastCachedTime FROM cached_league LIMIT 1")
    suspend fun getLastCachedTime(): Long

    @Query("UPDATE cached_league SET lastCachedTime = :timestamp")
    suspend fun updateLastCachedTime(timestamp: Long)
}
