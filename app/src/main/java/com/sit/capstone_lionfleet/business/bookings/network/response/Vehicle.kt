package com.sit.capstone_lionfleet.business.bookings.network.response

data class Vehicle(
    val plate: String,
    val model: String,
    val brand: String,
    val imageUrl: String,
    val availability: Boolean

)