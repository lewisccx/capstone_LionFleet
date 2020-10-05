package com.sit.capstone_lionfleet.business.bookings.network.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BookingResponse (
    @SerializedName("_id")
    @Expose
    val id: String,

    @SerializedName("status")
    @Expose
    val status: String
)