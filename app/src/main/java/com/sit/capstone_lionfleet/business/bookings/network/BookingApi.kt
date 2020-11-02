package com.sit.capstone_lionfleet.business.bookings.network

import com.sit.capstone_lionfleet.business.bookings.network.request.UpdateBookingRequest
import com.sit.capstone_lionfleet.business.bookings.network.request.UpdateOngoingBookingRequest
import com.sit.capstone_lionfleet.business.bookings.network.request.UpdatedCheckedOutBookingRequest
import com.sit.capstone_lionfleet.business.bookings.network.response.BookingsResponse
import com.sit.capstone_lionfleet.business.bookings.network.response.OngoingBookingResponse
import com.sit.capstone_lionfleet.business.bookings.network.response.UpdateBookingResponse
import retrofit2.Response
import retrofit2.http.*

interface BookingApi {

    @GET("bookings/status/{status}")
    suspend fun getUserBookingsByStatus(@Path("status") status: String): Response<BookingsResponse>

    @GET("bookings/history")
    suspend fun getHistoryBookings(): Response<BookingsResponse>

    @GET("bookings/ongoing")
    suspend fun getUserOngoingBooking(): Response<BookingsResponse>

    @PATCH("bookings/ongoing/{bookingId}")
    suspend fun updateOngoingBookingStatus(
        @Path("bookingId") bookingId: String,
        @Body updateOngoingBookingRequest: UpdateOngoingBookingRequest
    ): UpdateBookingResponse

    @PATCH("bookings/reserved/{bookingId}")
    suspend fun updateReservedBookingStatus(
        @Path("bookingId") bookingId: String,
        @Body updateBookingRequest: UpdateBookingRequest
    ): UpdateBookingResponse

    @PATCH("bookings/startable/{bookingId}")
    suspend fun updateStartableBookingStatus(
        @Path("bookingId") bookingId: String,
        @Body updateOngoingBookingRequest: UpdateOngoingBookingRequest
    ): UpdateBookingResponse

    @PATCH("bookings/ongoing/{bookingId}")
    suspend fun updateCheckedOutBookingStatus(
        @Path("bookingId") bookingId: String,
        @Body updatedCheckedOutBookingRequest: UpdatedCheckedOutBookingRequest
    ): UpdateBookingResponse
}