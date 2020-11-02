package com.sit.capstone_lionfleet.business.map

import android.location.Location
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.mapboxsdk.geometry.LatLng
import com.sit.capstone_lionfleet.base.response.Resource
import com.sit.capstone_lionfleet.business.map.network.model.StationVehicleStatus
import com.sit.capstone_lionfleet.business.map.network.model.Vehicle
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MapViewModel
@ViewModelInject
constructor(
    private val repository: StationRepository
) : ViewModel() {
    private val _stationVehiclesDataState: MutableLiveData<Resource<List<Vehicle>>> =
        MutableLiveData()

    val stationVehiclesDataState: LiveData<Resource<List<Vehicle>>>
        get() = _stationVehiclesDataState


    private val _stationVehicleAvailableDataState: MutableLiveData<Resource<StationVehicleStatus>> =
        MutableLiveData()

    val stationVehicleAvailableDataState: LiveData<Resource<StationVehicleStatus>>
        get() = _stationVehicleAvailableDataState

    val userLocationLatLng = MutableLiveData<Location>()


    fun setMapEvent(mapStateEvent: MapStateEvent, stationId: String) {
        viewModelScope.launch {
            when (mapStateEvent) {
                is MapStateEvent.GetVehiclesByStationId -> {
                    retrieveVehiclesByStationId(stationId)
                }

                is MapStateEvent.GetStationVehicleStatus -> {
                    retrieveVehicleAvailability(stationId)
                }
            }
        }
    }

    private suspend fun retrieveVehicleAvailability(stationId: String) {
        repository.getStationVehicleAvailability(stationId).onEach {
            _stationVehicleAvailableDataState.value = it
        }.launchIn(viewModelScope)
    }

    fun saveSelectedVehicleIdAsPref(vehicleId: String) = viewModelScope.launch {
        repository.saveSelectedVehicleIdAsPref(vehicleId)
    }

    fun getSavedSelectedVehicleId() = repository.getSavedSelectedVehicleId()


    private suspend fun retrieveVehiclesByStationId(stationId: String) {
        repository.getVehiclesByStationId(stationId).onEach {
            _stationVehiclesDataState.value = it
        }.launchIn(viewModelScope)
    }
}