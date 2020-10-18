package com.sit.capstone_lionfleet.business.map.network

import com.sit.capstone_lionfleet.business.map.network.response.StationResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface StationApi {

    @GET("station/{stationId}")
    suspend fun getStationById(@Path("stationId") stationId: String): StationResponse
}