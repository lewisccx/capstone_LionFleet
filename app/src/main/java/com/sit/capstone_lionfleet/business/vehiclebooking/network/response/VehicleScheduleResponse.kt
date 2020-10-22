package com.sit.capstone_lionfleet.business.vehiclebooking.network.response

import com.google.gson.annotations.SerializedName

data class VehicleScheduleResponse(
    @SerializedName("message") val message: String,
    @SerializedName("schedule") val schedule: List<String>
)