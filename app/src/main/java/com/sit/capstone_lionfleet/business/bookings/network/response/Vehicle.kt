package com.sit.capstone_lionfleet.business.bookings.network.response


import com.google.gson.annotations.SerializedName

data class Vehicle(
    val availability: Boolean,
    val brand: String,
    val imageUrl: String,
    val model: String,
    val plate: String
)