package com.sit.capstone_lionfleet.business.vehiclebooking

import com.sit.capstone_lionfleet.base.repository.BaseRepository
import com.sit.capstone_lionfleet.base.response.Resource
import com.sit.capstone_lionfleet.business.bookings.network.VehicleScheduleEntityMapper
import com.sit.capstone_lionfleet.business.bookings.network.model.BookingStatus
import com.sit.capstone_lionfleet.business.vehiclebooking.network.VehicleBookingApi
import com.sit.capstone_lionfleet.business.vehiclebooking.network.model.VehicleSchedule
import com.sit.capstone_lionfleet.business.vehiclebooking.network.request.NewVehicleBookingRequest
import com.sit.capstone_lionfleet.business.vehiclebooking.network.response.NewVehicleBookingResponse
import com.sit.capstone_lionfleet.core.di.PreferenceProvider
import com.sit.capstone_lionfleet.dataSource.local.dao.UserDao
import com.sit.capstone_lionfleet.dataSource.local.dao.VehicleDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import retrofit2.HttpException

class VehicleBookingRepository constructor(
    private val api: VehicleBookingApi,
    private val vehicleDao: VehicleDao,
    private val userDao: UserDao,
    private val vehicleScheduleEntityMapper: VehicleScheduleEntityMapper,
    private val preferenceProvider: PreferenceProvider
) : BaseRepository() {
    suspend fun getVehicleSchedule(): Flow<Resource<VehicleSchedule>> = flow {
        emit(Resource.Loading)
        try {
            val selectedVehicleId = preferenceProvider.getSelectedVehicleId()
            val vehicleScheduleResponse = api.getVehicleBookingSchedule(selectedVehicleId!!)
            val vehicleSchedule =
                vehicleScheduleEntityMapper.mapFromEntity(vehicleScheduleResponse.body()!!)
            emit(Resource.Success(vehicleSchedule))
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

    private fun buildNewVehicleBookingRequest(
        vehicleId: String,
        stationId: String,
        userId: String,
        reservedDate: String
    ): NewVehicleBookingRequest {
        return NewVehicleBookingRequest(
            user = userId,
            vehicle = vehicleId,
            station = stationId,
            reservedDate = reservedDate,
            checkedInTs = "1000-01-01",
            checkedOutTs = "1000-01-01",
            distance = 0,
            status = BookingStatus.BSTATE_RESERVED.status
        )
    }

    suspend fun createNewVehicleBooking(
        vehicleId: String,
        reservedDate: String
    ): Flow<Resource<NewVehicleBookingResponse>> = flow {
        emit(Resource.Loading)
        val userId = preferenceProvider.getLoggedInUserId()
        val stationId = vehicleDao.getVehiclesById(vehicleId).stationId
        val newVehicleBookingRequest =
            buildNewVehicleBookingRequest(vehicleId, stationId, userId!!, reservedDate)
        val newVehicleBookingResponse = safeApiCall {
            api.newVehicleBooking(newVehicleBookingRequest, vehicleId)
        }
        emit(newVehicleBookingResponse)

    }
}
