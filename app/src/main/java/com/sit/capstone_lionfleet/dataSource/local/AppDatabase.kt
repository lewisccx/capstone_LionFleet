package com.sit.capstone_lionfleet.dataSource.local

import androidx.room.*
import com.sit.capstone_lionfleet.dataSource.local.dao.BookingDao
import com.sit.capstone_lionfleet.dataSource.local.dao.UserDao
import com.sit.capstone_lionfleet.dataSource.local.dao.VehicleDao
import com.sit.capstone_lionfleet.dataSource.local.model.BookingCacheEntity
import com.sit.capstone_lionfleet.dataSource.local.model.UserCacheEntity
import com.sit.capstone_lionfleet.dataSource.local.model.VehicleCacheEntity

@Database(
    entities = [UserCacheEntity::class, BookingCacheEntity::class, VehicleCacheEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getUserDao(): UserDao
    abstract fun getBookingDao(): BookingDao
    abstract fun getVehicleDao(): VehicleDao
    companion object {

        val DATABASE_NAME = "LionFleet_db"
    }
}