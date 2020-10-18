package com.sit.capstone_lionfleet.dataSource.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicles")
data class VehicleCacheEntity(

    @PrimaryKey(autoGenerate = false)
    val id: String,
    @ColumnInfo(name = "plate")
    val plate: String,
    @ColumnInfo(name = "brand")
    val brand: String,
    @ColumnInfo(name = "model")
    val model: String,
    @ColumnInfo(name = "image_url")
    val imageUrl: String,
    @ColumnInfo(name = "availability")
    val availability: Boolean,
    @ColumnInfo(name = "is_diesel")
    val isDiesel: Boolean,
    @ColumnInfo(name = "costs_per_km")
    val costsPerKm: Int,
    @ColumnInfo(name = "costs_per_hour")
    val costsPerHour: Int,
    @ColumnInfo(name = "costs_per_day")
    val costsPerDay: Int,
    @ColumnInfo(name = "station_id")
    val stationId: String,
    @ColumnInfo(name = "station_name")
    val stationName: String
)