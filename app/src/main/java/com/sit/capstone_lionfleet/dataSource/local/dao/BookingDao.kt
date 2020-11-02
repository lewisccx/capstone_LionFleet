package com.sit.capstone_lionfleet.dataSource.local.dao

import androidx.room.*
import com.sit.capstone_lionfleet.dataSource.local.model.BookingCacheEntity

@Dao
interface BookingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookingCacheEntity: BookingCacheEntity): Long

    @Query("SELECT * FROM bookings WHERE status = :status Order by reserved_date asc")
    suspend fun getBookingsByStatusAsc(status: String): List<BookingCacheEntity>

    @Query("SELECT * FROM bookings WHERE status = :status Order by reserved_date desc")
    suspend fun getBookingsByStatusDesc(status: String): List<BookingCacheEntity>

    @Query("SELECT * FROM bookings WHERE status = 'BSTATE_CANCELED' OR  status = 'BSTATE_OVERDUE' OR status = 'BSTATE_CHECKEDIN' Order by reserved_date desc")
    suspend fun getHistoryBookings(): List<BookingCacheEntity>

    @Query("SELECT * FROM bookings WHERE status = 'BSTATE_CANCELED' Order by reserved_date desc")
    suspend fun getCanceledBookings(): List<BookingCacheEntity>

    @Query("SELECT * FROM bookings WHERE status = 'BSTATE_OVERDUE' Order by reserved_date desc")
    suspend fun getOverdueBookings(): List<BookingCacheEntity>

    @Query("SELECT * FROM bookings WHERE status = 'BSTATE_CHECKEDIN' Order by reserved_date desc")
    suspend fun getCheckedInBookings(): List<BookingCacheEntity>

    @Delete
    suspend fun deleteBooking(bookingCacheEntity: BookingCacheEntity)

    @Query("DELETE FROM bookings")
    suspend fun deleteAllBooking()
}