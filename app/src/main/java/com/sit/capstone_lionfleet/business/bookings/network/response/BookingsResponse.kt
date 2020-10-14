package com.sit.capstone_lionfleet.business.bookings.network.response


import com.google.gson.annotations.SerializedName

data class BookingsResponse(
    @SerializedName("message")val message: String,
    @SerializedName("bookings") val bookings: List<BookingItem>
)