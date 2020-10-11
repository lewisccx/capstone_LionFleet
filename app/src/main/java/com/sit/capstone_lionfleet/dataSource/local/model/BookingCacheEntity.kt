package com.sit.capstone_lionfleet.dataSource.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "bookings")
data class BookingCacheEntity (
    @PrimaryKey(autoGenerate = false)
    val id: String,
    @ColumnInfo(name = "checkedIn_ts")
    val checkedInTs: String,
    @ColumnInfo(name = "checkedOut_ts")
    val checkedOutTs: String,
    val createdAt: String,
    val distance: Int,
    @ColumnInfo(name = "reserved_date")
    val reservedDate: String,
    val status: String,
    val updatedAt: String,
    val availability: Boolean,
    val brand: String,
    val imageUrl: String,
    val model: String,
    val plate: String,
    @ColumnInfo(name = "station_name")
    val stationName: String
)