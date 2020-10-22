package com.sit.capstone_lionfleet.business.bookings.scheduled

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sit.capstone_lionfleet.base.response.Resource
import com.sit.capstone_lionfleet.business.bookings.BookingsStateEvent
import com.sit.capstone_lionfleet.business.bookings.network.model.Booking
import com.sit.capstone_lionfleet.business.bookings.network.model.BookingStatus
import com.sit.capstone_lionfleet.business.bookings.network.response.UpdateBookingResponse
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ScheduledViewModel
@ViewModelInject
constructor(
    private val repository: ScheduledRepository
) : ViewModel() {
    private val _updatedBookingDataState: MutableLiveData<Resource<UpdateBookingResponse>> =
        MutableLiveData()

    val updatedBookingsDataState: LiveData<Resource<UpdateBookingResponse>>
        get() = _updatedBookingDataState

    fun setScheduledBookingEvent(scheduleBookingStateEvent: ScheduleBookingStateEvent){
        viewModelScope.launch {
            when (scheduleBookingStateEvent) {
                is ScheduleBookingStateEvent.UpdateReservedBookingToStartable -> {
                    updateReserveBookingToStartable()
                }


            }
        }

    }

    private suspend fun updateReserveBookingToStartable() {
        repository.updateReservedBookingStatus(BookingStatus.BSTATE_STARTABLE.status).onEach {
            _updatedBookingDataState.value = it
        }.launchIn(viewModelScope)
    }
}