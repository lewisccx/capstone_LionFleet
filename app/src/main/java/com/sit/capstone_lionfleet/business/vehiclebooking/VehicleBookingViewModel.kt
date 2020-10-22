package com.sit.capstone_lionfleet.business.vehiclebooking

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sit.capstone_lionfleet.base.response.Resource
import com.sit.capstone_lionfleet.business.vehiclebooking.network.VehicleBookingViewState
import com.sit.capstone_lionfleet.business.vehiclebooking.network.model.VehicleSchedule
import com.sit.capstone_lionfleet.business.vehiclebooking.network.response.NewVehicleBookingResponse
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class VehicleBookingViewModel
@ViewModelInject constructor(
    private val repository: VehicleBookingRepository
) : ViewModel() {
    val TAG = "VehicleBookingViewModel"
    private var selectedDate: String = ""
    private val _viewState =
        MutableLiveData<VehicleBookingViewState>().apply { VehicleBookingViewState() }
    val viewState: LiveData<VehicleBookingViewState>
        get() = _viewState

    private val _vehicleScheduleDataState: MutableLiveData<Resource<VehicleSchedule>> =
        MutableLiveData()

    val vehicleScheduleDataState: LiveData<Resource<VehicleSchedule>>
        get() = _vehicleScheduleDataState

    private val _newVehicleBookingDataState: MutableLiveData<Resource<NewVehicleBookingResponse>> =
        MutableLiveData()

    val newVehicleBookingDataState: LiveData<Resource<NewVehicleBookingResponse>>
        get() = _newVehicleBookingDataState

    fun setVehicleBookingEvent(vehicleBookingStateEvent: VehicleBookingStateEvent, vehicleId: String) {

        viewModelScope.launch {
            when (vehicleBookingStateEvent) {
                is VehicleBookingStateEvent.LoadVehicleSchedule -> {
                    retrieveVehicleSchedule()
                }
                is VehicleBookingStateEvent.NewVehicleBooking -> {
                    createVehicleBooking(vehicleId)
                }
            }
        }
    }

    private suspend fun retrieveVehicleSchedule() {

        repository.getVehicleSchedule().onEach {
            _vehicleScheduleDataState.value = it
        }.launchIn(viewModelScope)
    }

    private suspend fun createVehicleBooking(vehicleId: String) {
        repository.createNewVehicleBooking(vehicleId, this.selectedDate).collect {
            _newVehicleBookingDataState.value = it
        }
    }

    fun onSelectedDateChanged(selectedDate: String) {
        this.selectedDate = selectedDate
        _viewState.value =
            VehicleBookingViewState(BookButtonEnabled = selectedDate.isNotBlank() && selectedDate.isNotEmpty())
        Log.d(TAG, "${selectedDate}")
    }
}