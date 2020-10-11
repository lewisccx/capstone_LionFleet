package com.sit.capstone_lionfleet.business.bookings

sealed class BookingsStateEvent {

    object GetReservedBookings : BookingsStateEvent()
    object GetCheckInBookings : BookingsStateEvent()
    object None : BookingsStateEvent()
}