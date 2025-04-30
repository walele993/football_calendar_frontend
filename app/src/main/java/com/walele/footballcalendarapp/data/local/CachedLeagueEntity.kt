package com.walele.footballcalendarapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.walele.footballcalendarapp.data.League

@Entity(tableName = "cached_league")
data class CachedLeague(
    @PrimaryKey val id: Int,
    val name: String,
    val lastCachedTime: Long
)

fun League.toCachedLeague(): CachedLeague {
    return CachedLeague(
        id = this.id,
        name = this.name,
        lastCachedTime = System.currentTimeMillis()
    )
}

fun CachedLeague.toLeague(): League {
    return League(
        id = this.id,
        name = this.name
    )
}