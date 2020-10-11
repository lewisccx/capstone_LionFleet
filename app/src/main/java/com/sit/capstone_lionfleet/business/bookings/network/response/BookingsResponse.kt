package com.sit.capstone_lionfleet.business.bookings.network.response


import com.google.gson.annotations.SerializedName

data class BookingsResponse(
    val bookings: List<BookingItem>,
    val message: String
)