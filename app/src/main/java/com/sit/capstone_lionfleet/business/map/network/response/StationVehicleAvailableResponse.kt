package com.sit.capstone_lionfleet.business.map.network.response


import com.google.gson.annotations.SerializedName

data class StationVehicleAvailableResponse(
    @SerializedName("message")val message: String,
    @SerializedName("total")val total: Int,
    @SerializedName("available")val available: Int
)