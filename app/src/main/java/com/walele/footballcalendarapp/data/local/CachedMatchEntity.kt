package com.walele.footballcalendarapp.data.local

import androidx.room.*
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_matches")
data class CachedMatch(
    @PrimaryKey val id: Int,
    val homeTeamId: Int,
    val homeTeamName: String,
    val awayTeamId: Int,
    val awayTeamName: String,
    val date: String,
    val time: String?,
    val scoreHome: Int?,
    val scoreAway: Int?,
    val isCancelled: Boolean,
    val leagueId: Int,
    val leagueName: String,
    val matchday: String,
    val cachedAt: Long // timestamp
)
