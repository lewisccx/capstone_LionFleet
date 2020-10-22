package com.sit.capstone_lionfleet.business.bookings.scheduled

sealed class ScheduleBookingStateEvent {

    object GetCurrentDateTime: ScheduleBookingStateEvent()
    object UpdateReservedBookingToStartable: ScheduleBookingStateEvent()
    object CancelReservedBooking: ScheduleBookingStateEvent()
    object None: ScheduleBookingStateEvent()
}