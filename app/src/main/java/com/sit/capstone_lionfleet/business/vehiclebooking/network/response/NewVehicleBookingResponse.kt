package com.sit.capstone_lionfleet.business.vehiclebooking.network.response


import com.google.gson.annotations.SerializedName

data class NewVehicleBookingResponse(
    val booking: Booking,
    val message: String
)