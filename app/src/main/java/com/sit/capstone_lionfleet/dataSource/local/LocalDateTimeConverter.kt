package com.sit.capstone_lionfleet.dataSource.local

import androidx.room.TypeConverter
import java.util.*

class LocalDateTimeConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}