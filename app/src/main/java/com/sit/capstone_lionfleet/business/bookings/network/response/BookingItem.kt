package com.sit.capstone_lionfleet.business.bookings.network.response

import com.google.gson.annotations.SerializedName

data class BookingItem(
    val distance: Int,
    @SerializedName("_id") val id: String,
    @SerializedName("vehicle") val vehicle: Vehicle,
    @SerializedName("station") val station: Station,

    val reservedDate: String,
    val checkedInTs: String,
    val checkedOutTs: String,
    val status: String,
    val createdAt: String,
    val updatedAt: String,
    val actualCost: Double,
    val expectedCost: Double

    )