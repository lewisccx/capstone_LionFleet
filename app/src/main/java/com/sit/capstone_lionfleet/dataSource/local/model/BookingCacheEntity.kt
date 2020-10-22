package com.sit.capstone_lionfleet.dataSource.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "bookings")
data class BookingCacheEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    @ColumnInfo(name = "checkedIn_ts")
    val checkedInTs: Date?,
    @ColumnInfo(name = "checkedOut_ts")
    val checkedOutTs: Date?,
    val createdAt: Date,
    val distance: Int,
    @ColumnInfo(name = "reserved_date")
    val reservedDate: Date,
    val status: String,
    val updatedAt: Date,
    val availability: Boolean,
    val brand: String,
    val imageUrl: String,
    val model: String,
    val plate: String,
    @ColumnInfo(name = "station_name")
    val stationName: String,
    val actualCost: Double,
    val expectedCost: Double
)