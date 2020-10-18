package com.sit.capstone_lionfleet.business.map.network.model

import com.google.gson.annotations.SerializedName
import com.sit.capstone_lionfleet.business.map.network.response.Pricing

data class Vehicle (
    val availability: Boolean,
    val brand: String,
    @SerializedName("_id")
    val id: String,
    val imageUrl: String,
    val isDiesel: Boolean,
    val model: String,
    val plate: String,
    val costsPerKm: Int,
    val costsPerHour: Int,
    val costsPerDay: Int,
    val stationId: String,
    val stationName: String
)