package com.sit.capstone_lionfleet.business.map

import com.sit.capstone_lionfleet.base.repository.BaseRepository
import com.sit.capstone_lionfleet.base.response.Resource
import com.sit.capstone_lionfleet.business.map.network.StationApi
import com.sit.capstone_lionfleet.business.map.network.StationVehicleAvailabilityMapper
import com.sit.capstone_lionfleet.business.map.network.VehicleEntityMapper
import com.sit.capstone_lionfleet.business.map.network.model.StationVehicleStatus
import com.sit.capstone_lionfleet.business.map.network.model.Vehicle
import com.sit.capstone_lionfleet.core.di.PreferenceProvider
import com.sit.capstone_lionfleet.dataSource.local.dao.VehicleDao
import com.sit.capstone_lionfleet.dataSource.local.model.VehicleCacheMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import retrofit2.HttpException

class StationRepository
constructor(
    private val api: StationApi,
    private val vehicleDao: VehicleDao,
    private val vehicleEntityMapper: VehicleEntityMapper,
    private val stationVehicleAvailabilityMapper: StationVehicleAvailabilityMapper,
    private val vehicleCacheMapper: VehicleCacheMapper,
    private val preferenceProvider: PreferenceProvider
) : BaseRepository() {
    suspend fun getVehiclesByStationId(stationId: String): Flow<Resource<List<Vehicle>>> = flow {
        emit(Resource.Loading)
        try {
            val stationInfoResponse = api.getStationById(stationId)
            val stationVehicles =
                vehicleEntityMapper.mapFromEntityResponse(stationInfoResponse.stationVehicles.vehicles)

            for (vehicle in stationVehicles) {
                vehicleDao.insert(vehicleCacheMapper.mapToEntity(vehicle))
            }
            val cacheVehicleList = vehicleDao.getVehiclesByStationId(stationId)
            emit(Resource.Success(vehicleCacheMapper.mapFromEntityList(cacheVehicleList)))
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

    suspend fun getStationVehicleAvailability(stationId: String): Flow<Resource<StationVehicleStatus>> =
        flow {
            try {
                val stationVehicleAvailableResponse = api.getStationVehicleAvailability(stationId)
                val stationVehicleStatus =
                    stationVehicleAvailabilityMapper.mapFromEntity(stationVehicleAvailableResponse)
                emit(Resource.Success(stationVehicleStatus))
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

    fun saveSelectedVehicleIdAsPref(vehicleId: String) {
        preferenceProvider.saveSelectedVehicleIdAsPref(vehicleId)
    }

    fun getSavedSelectedVehicleId() {
        preferenceProvider.getSelectedVehicleId()
    }
}