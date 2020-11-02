package com.sit.capstone_lionfleet.business.bookings.service

enum class OngoingTripAction(val action: String) {

    ACTION_TRIP_STARTED("ACTION_TRIP_STARTED"),
    ACTION_CAR_LOCKED("ACTION_CAR_LOCKED"),
    ACTION_CAR_UNLOCKED("ACTION_CAR_UNLOCKED"),
    ACTION_TRIP_END("ACTION_TRIP_END"),

}