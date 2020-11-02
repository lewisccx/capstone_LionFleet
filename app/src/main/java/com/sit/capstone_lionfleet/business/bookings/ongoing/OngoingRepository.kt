package com.sit.capstone_lionfleet.business.bookings.ongoing

import com.sit.capstone_lionfleet.base.repository.BaseRepository
import com.sit.capstone_lionfleet.base.response.Resource
import com.sit.capstone_lionfleet.business.bookings.network.BookingApi
import com.sit.capstone_lionfleet.business.bookings.network.BookingEntityMapper
import com.sit.capstone_lionfleet.business.bookings.network.model.Booking
import com.sit.capstone_lionfleet.business.bookings.network.model.BookingStatus
import com.sit.capstone_lionfleet.business.bookings.network.request.UpdateOngoingBookingRequest
import com.sit.capstone_lionfleet.business.bookings.network.request.UpdatedCheckedOutBookingRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import retrofit2.HttpException

class OngoingRepository
constructor(
    private val api: BookingApi,
    private val bookingEntityMapper: BookingEntityMapper,
) : BaseRepository() {
    suspend fun getOngoingBooking(): Flow<Resource<List<Booking>>> = flow {
        emit(Resource.Loading)
        try {
            val ongoingBookingResponse =
                api.getUserOngoingBooking()


            val booking =
                bookingEntityMapper.mapFromEntityResponse(ongoingBookingResponse.body()!!.bookings)

            emit(Resource.Success(booking))
        } catch (throwable: Throwable) {
            when (throwable) {
                is HttpException -> {
                    val httpFailure = Resource.Failure(
                        false,
                        throwable.code(),
                        JSONObject(
                            throwable.response()?.errorBody()!!.string()
                        ).getString("message")
                    )
                    emit(httpFailure)
                }
                else -> {
                    val unknownFailure = Resource.Failure(true, null, throwable.message)
                    emit(unknownFailure)
                }
            }
        }

    }

    suspend fun updateStartableBookingStatus(
        bookingId: String,
        vehicleId: String,
        checkedOutTime: String
    ): Flow<Resource<Booking>> = flow {
        emit(Resource.Loading)
        try {
            val updateOngoingBookingRequest =
                UpdateOngoingBookingRequest(BookingStatus.BSTATE_CHECKEDOUT.status, vehicleId,checkedOutTime)
            val startableBookingResponse =
                api.updateStartableBookingStatus(bookingId, updateOngoingBookingRequest)
            val booking =
                bookingEntityMapper.mapFromEntity(startableBookingResponse.booking)

            emit(Resource.Success(booking))
        } catch (throwable: Throwable) {
            when (throwable) {
                is HttpException -> {
                    val httpFailure = Resource.Failure(
                        false,
                        throwable.code(),
                        JSONObject(
                            throwable.response()?.errorBody()!!.string()
                        ).getString("message")
                    )
                    emit(httpFailure)
                }
                else -> {
                    val unknownFailure = Resource.Failure(true, null, throwable.message)
                    emit(unknownFailure)
                }
            }
        }

    }

    suspend fun updateCheckedOutBookingStatus(
        bookingId: String,
        update: UpdatedCheckedOutBookingRequest
    ): Flow<Resource<Booking>> = flow {
        emit(Resource.Loading)
        try {
            val bookingResponse =
                api.updateCheckedOutBookingStatus(bookingId, update)
            val booking =
                bookingEntityMapper.mapFromEntity(bookingResponse.booking)

            emit(Resource.Success(booking))
        } catch (throwable: Throwable) {
            when (throwable) {
                is HttpException -> {
                    val httpFailure = Resource.Failure(
                        false,
                        throwable.code(),
                        JSONObject(
                            throwable.response()?.errorBody()!!.string()
                        ).getString("message")
                    )
                    emit(httpFailure)
                }
                else -> {
                    val unknownFailure = Resource.Failure(true, null, throwable.message)
                    emit(unknownFailure)
                }
            }
        }

    }
}