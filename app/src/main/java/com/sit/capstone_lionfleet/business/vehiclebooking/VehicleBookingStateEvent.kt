package com.sit.capstone_lionfleet.business.vehiclebooking

sealed class VehicleBookingStateEvent {
    object LoadVehicleSchedule: VehicleBookingStateEvent()
    object NewVehicleBooking:VehicleBookingStateEvent()
    object None: VehicleBookingStateEvent()
}