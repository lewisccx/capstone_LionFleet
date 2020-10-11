package com.sit.capstone_lionfleet.business.bookings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sit.capstone_lionfleet.base.response.Resource
import com.sit.capstone_lionfleet.business.bookings.network.model.Booking
import com.sit.capstone_lionfleet.business.bookings.network.model.BookingStatus
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class BookingsViewModel
@ViewModelInject
constructor(
    private val repository: BookingsRepository
) : ViewModel() {
    private val _reservedBookingDataState: MutableLiveData<Resource<List<Booking>>> =
        MutableLiveData()

    val reservedBookingsDataState: LiveData<Resource<List<Booking>>>
        get() = _reservedBookingDataState

    private val _checkedInBookingDataState: MutableLiveData<Resource<List<Booking>>> =
        MutableLiveData()

    val checkedInBookingDataState: LiveData<Resource<List<Booking>>>
        get() = _checkedInBookingDataState

    fun setBookingsStateEvent(bookingsStateEvent: BookingsStateEvent) {
        viewModelScope.launch {
            when (bookingsStateEvent) {
                is BookingsStateEvent.GetReservedBookings -> {
                    retrieveReservedBookings()
                }

                is BookingsStateEvent.GetCheckInBookings -> {
                    retrieveCheckedInBookings()
                }
            }
        }
    }

    private suspend fun retrieveCheckedInBookings() {
        repository.getBookingByStatus(BookingStatus.BSTATE_CHECKEDIN.status).onEach {
            _checkedInBookingDataState.value = it
        }.launchIn(viewModelScope)
    }

    private suspend fun retrieveReservedBookings() {
        repository.getBookingByStatus(BookingStatus.BSTATE_RESERVED.status).onEach {
            _reservedBookingDataState.value = it
        }.launchIn(viewModelScope)
    }
}