package com.sit.capstone_lionfleet.business.bookings

import com.sit.capstone_lionfleet.base.repository.BaseRepository
import com.sit.capstone_lionfleet.base.response.Resource
import com.sit.capstone_lionfleet.business.bookings.network.BookingApi
import com.sit.capstone_lionfleet.business.bookings.network.BookingEntityMapper
import com.sit.capstone_lionfleet.business.bookings.network.model.Booking
import com.sit.capstone_lionfleet.business.bookings.network.model.BookingStatus
import com.sit.capstone_lionfleet.core.di.PreferenceProvider
import com.sit.capstone_lionfleet.dataSource.local.dao.BookingDao
import com.sit.capstone_lionfleet.dataSource.local.model.BookingCacheMapper
import com.sit.capstone_lionfleet.utils.Constants
import com.sit.capstone_lionfleet.utils.isFetchNeeded
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import retrofit2.HttpException
import java.time.LocalDateTime

class BookingsRepository
constructor(
    private val api: BookingApi,
    private val bookingDao: BookingDao,
    private val bookingEntityMapper: BookingEntityMapper,
    private val bookingCacheMapper: BookingCacheMapper,
    private val preferenceProvider: PreferenceProvider
) : BaseRepository() {
    suspend fun getBookingByStatus(bookingStatus: String): Flow<Resource<List<Booking>>> = flow {
        emit(Resource.Loading)
        try {
            val timeStamp = preferenceProvider.geValueFromPref(Constants.BOOKING_API_CALL_TIMESTAMP)
            if (timeStamp == null || isFetchNeeded(
                    LocalDateTime.parse(timeStamp),
                    Constants.BOOKING_API_CALL_LIMIT
                )
            ) {
                val bookingsResponse = api.getUserBookingsByStatus(bookingStatus)
                val bookings = bookingEntityMapper.mapFromEntityResponse(bookingsResponse.body()!!.bookings)
                for(booking in bookings){
                    bookingDao.insert(bookingCacheMapper.mapToEntity(booking))
                }
                preferenceProvider.saveAsPref(
                    Constants.BOOKING_API_CALL_TIMESTAMP,
                    LocalDateTime.now().toString()
                )
            }
            val cachedBookings  = bookingDao.getBookingsByStatus(bookingStatus)
            emit(Resource.Success(bookingCacheMapper.mapFromEntityList(cachedBookings)))
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