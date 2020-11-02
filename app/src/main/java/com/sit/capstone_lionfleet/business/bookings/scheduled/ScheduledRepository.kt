package com.sit.capstone_lionfleet.business.bookings.scheduled

import com.sit.capstone_lionfleet.base.repository.BaseRepository
import com.sit.capstone_lionfleet.base.response.Resource
import com.sit.capstone_lionfleet.business.bookings.network.BookingApi
import com.sit.capstone_lionfleet.business.bookings.network.request.UpdateBookingRequest
import com.sit.capstone_lionfleet.business.bookings.network.response.UpdateBookingResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ScheduledRepository
constructor(

    private val api: BookingApi,

    ) : BaseRepository() {

    suspend fun updateReservedBookingStatus(
        status: String,
        bookingId: String
    ): Flow<Resource<UpdateBookingResponse>> =
        flow {
            emit(Resource.Loading)

            val updateBookingRequest = UpdateBookingRequest(status)

            val bookingStatusResponse =
                safeApiCall { api.updateReservedBookingStatus(bookingId, updateBookingRequest) }

            emit(bookingStatusResponse)
        }
}