package com.sit.capstone_lionfleet.business.vehiclebooking.network.response


import com.google.gson.annotations.SerializedName

data class Booking(
    val actualCost: Int,
    val checkedInTs: String,
    val checkedOutTs: String,
    val createdAt: String,
    val distance: Int,
    val expectedCost: Int,
    @SerializedName("_id")
    val id: String,
    val reservedDate: String,
    val station: String,
    val status: String,
    val updatedAt: String,
    val user: String,
    val vehicle: String
)