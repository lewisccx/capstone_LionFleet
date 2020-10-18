package com.sit.capstone_lionfleet.business.map.network.response


import com.google.gson.annotations.SerializedName

data class StationResponse(
    @SerializedName("message") val message: String,
    @SerializedName("stationVehicles") val stationVehicles: StationVehicles
)