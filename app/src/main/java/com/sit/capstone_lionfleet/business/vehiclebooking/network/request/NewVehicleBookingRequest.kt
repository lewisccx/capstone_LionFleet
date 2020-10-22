package com.sit.capstone_lionfleet.business.vehiclebooking.network.request

data class NewVehicleBookingRequest(
    val checkedInTs: String,
    val checkedOutTs: String,
    val distance: Int,
    val reservedDate: String,
    val station: String,
    val status: String,
    val user: String,
    val vehicle: String
)