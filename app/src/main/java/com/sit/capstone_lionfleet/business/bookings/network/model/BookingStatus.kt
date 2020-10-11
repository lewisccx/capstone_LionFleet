package com.sit.capstone_lionfleet.business.bookings.network.model

enum class BookingStatus(val status: String) {
    BSTATE_CANCELED("BSTATE_CANCELED"),
    BSTATE_RESERVED("BSTATE_RESERVED"),
    BSTATE_CHECKEDIN("BSTATE_CHECKEDIN"),
    BSTATE_CHECKEDOUT("BSTATE_CHECKEDOUT"),
    BSTATE_STARTABLE("BSTATE_STARTABLE"),
    BSTATE_UNKNOWN("BSTATE_UNKNOWN")
}