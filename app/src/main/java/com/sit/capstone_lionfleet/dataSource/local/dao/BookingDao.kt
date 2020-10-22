package com.sit.capstone_lionfleet.dataSource.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sit.capstone_lionfleet.business.bookings.network.model.BookingStatus
import com.sit.capstone_lionfleet.dataSource.local.model.BookingCacheEntity
import com.sit.capstone_lionfleet.dataSource.local.model.UserCacheEntity

@Dao
interface BookingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookingCacheEntity: BookingCacheEntity): Long

    @Query("SELECT * FROM bookings WHERE status = :status Order by reserved_date asc")
    suspend fun getBookingsByStatus(status: String): List<BookingCacheEntity>

    @Delete
    suspend fun deleteBooking(bookingCacheEntity: BookingCacheEntity)
}