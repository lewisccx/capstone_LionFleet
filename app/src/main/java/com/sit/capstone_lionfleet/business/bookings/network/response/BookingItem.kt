package com.sit.capstone_lionfleet.business.bookings.network.response


import com.google.gson.annotations.SerializedName

data class BookingItem(
    val checkedInTs: String,
    val checkedOutTs: String,
    val createdAt: String,
    val distance: Int,
    @SerializedName("_id")
    val id: String,
    val reservedDate: String,
    val station: Station,
    val status: String,
    val updatedAt: String,
    val vehicle: Vehicle
)