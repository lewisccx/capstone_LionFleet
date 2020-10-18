package com.sit.capstone_lionfleet.business.map.network.response


import com.google.gson.annotations.SerializedName

data class VehicleItem(
    val availability: Boolean,
    val brand: String,
    @SerializedName("_id")
    val id: String,
    val imageUrl: String,
    val isDiesel: Boolean,
    val model: String,
    val plate: String,
    val pricing: Pricing,
    val station: Station
)