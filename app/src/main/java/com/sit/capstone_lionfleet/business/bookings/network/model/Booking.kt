package com.sit.capstone_lionfleet.business.bookings.network.model

import com.google.gson.annotations.SerializedName

data class Booking(
    val checkedInTs: String,
    val checkedOutTs: String,
    val createdAt: String,
    val distance: Int,
    @SerializedName("_id")
    val id: String,
    val reservedDate: String,
    val status: String,
    val updatedAt: String,
    val availability: Boolean,
    val brand: String,
    val imageUrl: String,
    val model: String,
    val plate: String,
    val stationName: String,
    val actualCost: Double,
    val expectedCost: Double
)