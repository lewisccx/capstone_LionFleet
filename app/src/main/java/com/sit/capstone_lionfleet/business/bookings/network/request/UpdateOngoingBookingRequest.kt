package com.sit.capstone_lionfleet.business.bookings.network.request

data class UpdateOngoingBookingRequest (
    val status: String,
    val vehicleId: String,
    val checkedInTs: String
)