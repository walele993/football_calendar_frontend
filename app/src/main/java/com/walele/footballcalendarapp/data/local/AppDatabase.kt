package com.walele.footballcalendarapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CachedMatch::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun matchDao(): MatchDao
}
