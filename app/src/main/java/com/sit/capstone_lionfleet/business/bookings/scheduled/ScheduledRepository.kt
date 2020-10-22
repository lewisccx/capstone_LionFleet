package com.sit.capstone_lionfleet.business.bookings.scheduled

import com.sit.capstone_lionfleet.base.repository.BaseRepository
import com.sit.capstone_lionfleet.base.response.Resource
import com.sit.capstone_lionfleet.business.bookings.network.BookingApi
import com.sit.capstone_lionfleet.business.bookings.network.WorldTImeEntityMapper
import com.sit.capstone_lionfleet.business.bookings.network.WorldTimeApi
import com.sit.capstone_lionfleet.business.bookings.network.model.BookingStatus
import com.sit.capstone_lionfleet.business.bookings.network.model.WorldTime
import com.sit.capstone_lionfleet.business.bookings.network.response.UpdateBookingResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import retrofit2.HttpException

class ScheduledRepository
constructor(

    private val api: BookingApi,
    //private val worldTimeApi: WorldTimeApi,
    //private val worldTImeEntityMapper: WorldTImeEntityMapper,
) : BaseRepository() {

    suspend fun updateReservedBookingStatus(status: String): Flow<Resource<UpdateBookingResponse>> =
        flow {
            emit(Resource.Loading)

            val bookingStatusResponse = safeApiCall {
                api.updateReservedBookingStatus(status)
            }

            emit(bookingStatusResponse)
        }

//    suspend fun getCurrentDateTime(): Flow<Resource<WorldTime>> =
//        flow {
//            emit(Resource.Loading)
//            try {
//
//                val worldTimeResponse = worldTimeApi.getSingaporeTime()
//                val singaporeTime = worldTImeEntityMapper.mapFromEntity(worldTimeResponse)
//
//                emit(Resource.Success(singaporeTime))
//            } catch (throwable: Throwable) {
//                when (throwable) {
//                    is HttpException -> {
//                        val httpFailure = Resource.Failure(
//                            false,
//                            throwable.code(),
//                            JSONObject(
//                                throwable.response()?.errorBody()!!.string()
//                            ).getString("message")
//                        )
//                        emit(httpFailure)
//                    }
//                    else -> {
//                        val unknownFailure = Resource.Failure(true, null, throwable.message)
//                        emit(unknownFailure)
//                    }
//                }
//            }
//        }
}