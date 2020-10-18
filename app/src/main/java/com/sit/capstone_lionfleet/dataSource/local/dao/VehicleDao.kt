package com.sit.capstone_lionfleet.dataSource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sit.capstone_lionfleet.dataSource.local.model.VehicleCacheEntity

@Dao
interface VehicleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vehicleCacheEntity: VehicleCacheEntity): Long

    @Query("SELECT * FROM vehicles WHERE station_id = :stationId")
    suspend fun getVehiclesByStationId(stationId: String): List<VehicleCacheEntity>

    @Query("SELECT * FROM vehicles WHERE id = :id")
    suspend fun getVehiclesById(id: String): VehicleCacheEntity
}