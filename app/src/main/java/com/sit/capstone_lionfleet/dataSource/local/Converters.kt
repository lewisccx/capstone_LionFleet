package com.sit.capstone_lionfleet.dataSource.local

import androidx.room.TypeConverter
import com.sit.capstone_lionfleet.business.bookings.network.model.BookingStatus

class Converters {
    @TypeConverter
    fun  fromBookingStatusEnum(bookingStatus:BookingStatus): String {
        return  bookingStatus.status
    }
    @TypeConverter
    fun toBookingStatusEnum(value: String?): BookingStatus? {
        return when(value) {
            BookingStatus.BSTATE_RESERVED.status ->BookingStatus.BSTATE_RESERVED
            BookingStatus.BSTATE_CHECKEDIN.status ->BookingStatus.BSTATE_CHECKEDIN
            BookingStatus.BSTATE_CHECKEDOUT.status ->BookingStatus.BSTATE_CHECKEDOUT
            BookingStatus.BSTATE_CANCELED.status ->BookingStatus.BSTATE_CANCELED
            BookingStatus.BSTATE_STARTABLE.status ->BookingStatus.BSTATE_STARTABLE
            else -> BookingStatus.BSTATE_UNKNOWN
        }
    }
}