package com.sit.capstone_lionfleet.business.bookings.network

import com.sit.capstone_lionfleet.business.bookings.network.model.BookingStatus
import com.sit.capstone_lionfleet.business.bookings.network.response.BookingsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface BookingApi {

    @GET("bookings/status/{status}")
    suspend fun getUserBookingsByStatus(@Path("status") status: String) : Response<BookingsResponse>
}