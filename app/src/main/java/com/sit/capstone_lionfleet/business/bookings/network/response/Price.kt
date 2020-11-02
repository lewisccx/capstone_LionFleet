package com.sit.capstone_lionfleet.business.bookings.network.response

import com.google.gson.annotations.SerializedName

data class Price(
    @SerializedName("costsPerHour") val costsPerHour: Int,
    @SerializedName("costsPerDay") val costsPerDay: Int,

    @SerializedName("costsPerKm") val costsPerKm: Int,
)