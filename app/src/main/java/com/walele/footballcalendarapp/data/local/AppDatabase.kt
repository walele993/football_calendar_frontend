package com.walele.footballcalendarapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CachedMatch::class, CachedLeague::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun matchDao(): MatchDao
    abstract fun leagueDao(): LeagueDao
}