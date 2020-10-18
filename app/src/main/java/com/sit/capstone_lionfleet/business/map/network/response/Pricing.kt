package com.sit.capstone_lionfleet.business.map.network.response


import com.google.gson.annotations.SerializedName

data class Pricing(
    val costsPerDay: Int,
    val costsPerHour: Int,
    val costsPerKm: Int
)