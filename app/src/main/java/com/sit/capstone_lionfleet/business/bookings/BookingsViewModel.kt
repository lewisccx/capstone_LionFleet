package com.sit.capstone_lionfleet.business.bookings

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sit.capstone_lionfleet.base.response.Resource
import com.sit.capstone_lionfleet.business.bookings.network.model.Booking
import com.sit.capstone_lionfleet.business.bookings.network.model.BookingStatus
import com.sit.capstone_lionfleet.business.bookings.network.request.UpdatedCheckedOutBookingRequest
import com.sit.capstone_lionfleet.business.bookings.network.response.UpdateBookingResponse
import com.sit.capstone_lionfleet.business.bookings.ongoing.OngoingRepository
import com.sit.capstone_lionfleet.business.bookings.scheduled.ScheduledRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class BookingsViewModel
@ViewModelInject
constructor(
    private val repository: BookingsRepository,
    private val scheduledRepository: ScheduledRepository,
    private val ongoingRepository: OngoingRepository
) : ViewModel() {

    val TAG : String = "BookingsViewModel"
    private val _updatedBookingDataState: MutableLiveData<Resource<UpdateBookingResponse>> =
        MutableLiveData()

    val updatedBookingsDataState: LiveData<Resource<UpdateBookingResponse>>
        get() = _updatedBookingDataState

    private val _reservedBookingDataState: MutableLiveData<Resource<List<Booking>>> =
        MutableLiveData()

    val reservedBookingsDataState: LiveData<Resource<List<Booking>>>
        get() = _reservedBookingDataState

    private val _historyBookingDataState: MutableLiveData<Resource<List<Booking>>> =
        MutableLiveData()

    val completedBookingDataState: LiveData<Resource<List<Booking>>>
        get() = _historyBookingDataState

    private val _ongoingBookingDataState: MutableLiveData<Resource<List<Booking>>> =
        MutableLiveData()

    val ongoingBookingDataState: LiveData<Resource<List<Booking>>>
        get() = _ongoingBookingDataState

    private val _checkedOutBookingDataState: MutableLiveData<Resource<Booking>> =
        MutableLiveData()

    val checkedOutBookingDataState: LiveData<Resource<Booking>>
        get() = _checkedOutBookingDataState

    private val _checkedInBookingDataState: MutableLiveData<Resource<Booking>> =
        MutableLiveData()

    val checkedInBookingDataState: LiveData<Resource<Booking>>
        get() = _checkedInBookingDataState

    fun setBookingsStateEvent(bookingsStateEvent: BookingsStateEvent, bookingId: String, vehicleId:String) {
        viewModelScope.launch {
            when (bookingsStateEvent) {
                is BookingsStateEvent.GetReservedBookings -> {
                    retrieveReservedBookings()
                }

                is BookingsStateEvent.GetHistoryBookings -> {
                    retrieveHistoryBookings()
                }
                is BookingsStateEvent.UpdateReservedBookingToStartable -> {
                    updateReserveBookingToStartable(bookingId)
                }
                is BookingsStateEvent.GetOngoingBooking -> {
                    retrieveOngoingBooking()
                }
                is BookingsStateEvent.UpdateStartableBookingToCheckedOut -> {
                    updateStartableBookingToCheckout(bookingId, vehicleId)
                }
                is BookingsStateEvent.UpdateCheckedOutBookingToCheckedIn -> {

                }
                is BookingsStateEvent.CancelReservedBooking -> {

                }
                is BookingsStateEvent.CancelStartableBooking -> {

                }
            }
        }
    }

      fun updateCheckedOutBookingToCheckedIn(bookingId: String, booking: UpdatedCheckedOutBookingRequest) {
         viewModelScope.launch {
             ongoingRepository.updateCheckedOutBookingStatus(bookingId, booking).onEach {
                 _checkedInBookingDataState.value = it
             }.launchIn(viewModelScope)
         }
    }

    private suspend fun updateStartableBookingToCheckout(bookingId: String,vehicleId: String) {
        val currentDateTime =
            Calendar.getInstance().time.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().toString()
        //Log.d(TAG,currentDateTime)
        val formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
        val formattedCurrentDateTime = LocalDateTime.parse(currentDateTime, formatter1).toString()
        val zoneSpecificDateTime = formattedCurrentDateTime + "Z"
        ongoingRepository.updateStartableBookingStatus(bookingId,vehicleId, zoneSpecificDateTime).onEach {
            _checkedOutBookingDataState.value = it
        }.launchIn(viewModelScope)
    }

    private suspend fun retrieveOngoingBooking() {
        ongoingRepository.getOngoingBooking().onEach {
            _ongoingBookingDataState.value = it
        }.launchIn(viewModelScope)
    }

    private suspend fun retrieveHistoryBookings() {
        repository.getHistoryBookings().onEach {
            _historyBookingDataState.value = it
        }.launchIn(viewModelScope)
    }

    private suspend fun retrieveReservedBookings() {
        repository.getBookingByStatus(BookingStatus.BSTATE_RESERVED.status).onEach {
            _reservedBookingDataState.value = it
        }.launchIn(viewModelScope)
    }

    private suspend fun updateReserveBookingToStartable(bookingId: String) {
        scheduledRepository.updateReservedBookingStatus(
            BookingStatus.BSTATE_STARTABLE.status,
            bookingId
        )
            .onEach {
                _updatedBookingDataState.value = it
            }.launchIn(viewModelScope)
    }


}