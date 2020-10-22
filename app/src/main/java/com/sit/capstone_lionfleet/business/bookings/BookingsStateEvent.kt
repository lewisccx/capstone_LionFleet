package com.sit.capstone_lionfleet.business.bookings

import com.sit.capstone_lionfleet.business.bookings.scheduled.ScheduleBookingStateEvent

sealed class BookingsStateEvent {

    object GetReservedBookings : BookingsStateEvent()
    object GetCheckInBookings : BookingsStateEvent()
    object GetCurrentDateTime: BookingsStateEvent()
    object UpdateReservedBookingToStartable: BookingsStateEvent()
    object CancelReservedBooking: BookingsStateEvent()
    object None : BookingsStateEvent()
}