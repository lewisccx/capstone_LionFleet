package com.sit.capstone_lionfleet.business.bookings

import com.sit.capstone_lionfleet.business.bookings.scheduled.ScheduleBookingStateEvent

sealed class BookingsStateEvent {

    object GetReservedBookings : BookingsStateEvent()
    object GetOngoingBooking : BookingsStateEvent()
    object GetHistoryBookings : BookingsStateEvent()
    object GetCurrentDateTime: BookingsStateEvent()
    object UpdateReservedBookingToStartable: BookingsStateEvent()
    object UpdateStartableBookingToCheckedOut: BookingsStateEvent()
    object UpdateCheckedOutBookingToCheckedIn: BookingsStateEvent()
    object CancelReservedBooking: BookingsStateEvent()
    object CancelStartableBooking: BookingsStateEvent()
    object None : BookingsStateEvent()
}