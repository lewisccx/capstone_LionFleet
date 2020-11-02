package com.sit.capstone_lionfleet.business.map.network

import com.sit.capstone_lionfleet.business.map.network.response.StationResponse
import com.sit.capstone_lionfleet.business.map.network.response.StationVehicleAvailableResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface StationApi {

    @GET("station/{stationId}")
    suspend fun getStationById(@Path("stationId") stationId: String): StationResponse

    @GET("station/{stationId}/availableVehicles")
    suspend fun getStationVehicleAvailability(@Path("stationId") stationId: String): StationVehicleAvailableResponse
}