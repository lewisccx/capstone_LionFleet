package com.sit.capstone_lionfleet.business.bookings.network.response

import com.google.gson.annotations.SerializedName

data class OngoingBookingResponse (
    @SerializedName("message")val message: String,
    @SerializedName("booking") val booking: BookingItem
)