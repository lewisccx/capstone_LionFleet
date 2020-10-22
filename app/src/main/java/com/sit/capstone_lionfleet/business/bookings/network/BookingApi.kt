package com.sit.capstone_lionfleet.business.bookings.network

import com.sit.capstone_lionfleet.business.bookings.network.response.BookingsResponse
import com.sit.capstone_lionfleet.business.bookings.network.response.UpdateBookingResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BookingApi {

    @GET("bookings/status/{status}")
    suspend fun getUserBookingsByStatus(@Path("status") status: String): Response<BookingsResponse>

    @POST("ongoing/{bookingId}")
    suspend fun updateOngoingBookingStatus(@Path("bookingId") bookingId: String): UpdateBookingResponse

    @POST("reserved/{bookingId}")
    suspend fun updateReservedBookingStatus(@Path("bookingId") bookingId: String): UpdateBookingResponse

    @POST("startable/{bookingId}")
    suspend fun updateStartableBookingStatus(@Path("bookingId") bookingId: String): UpdateBookingResponse
}