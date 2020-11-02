package com.sit.capstone_lionfleet.business.bookings.network.request

data class UpdatedCheckedOutBookingRequest (
    val checkedOutTs: String,
    val status: String,
    val actualCost: Int,
    val expectedCost: Int,
val plate: String
)