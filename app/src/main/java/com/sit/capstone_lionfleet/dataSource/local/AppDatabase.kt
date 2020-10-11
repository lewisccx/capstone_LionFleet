package com.sit.capstone_lionfleet.dataSource.local

import android.content.Context
import androidx.room.*
import com.sit.capstone_lionfleet.dataSource.local.dao.BookingDao
import com.sit.capstone_lionfleet.dataSource.local.dao.UserDao
import com.sit.capstone_lionfleet.dataSource.local.model.BookingCacheEntity
import com.sit.capstone_lionfleet.dataSource.local.model.UserCacheEntity

@Database(
    entities = [UserCacheEntity::class, BookingCacheEntity::class],
    version = 3,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun getUserDao(): UserDao
    abstract fun getBookingDao(): BookingDao
    companion object {

        val DATABASE_NAME = "LionFleet_db"
    }
}