package com.sit.capstone_lionfleet.business.map.network.response


import com.google.gson.annotations.SerializedName

data class StationVehicles(
    @SerializedName("_id")
    val id: String,
    val name: String,
    val vehicles: List<VehicleItem>
)