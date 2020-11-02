package com.sit.capstone_lionfleet.business.bookings.network.response

import com.google.gson.annotations.SerializedName

data class Vehicle(
    val plate: String,
    val model: String,
    val brand: String,
    val imageUrl: String,
    val availability: Boolean,
    @SerializedName("pricing") val price: Price,
)