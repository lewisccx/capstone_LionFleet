package com.sit.capstone_lionfleet.utils

import android.content.Context
import com.sit.capstone_lionfleet.R
import java.text.SimpleDateFormat

class DateUtils {
    companion object {
        private const val MINUTES_PER_HOUR = 60
        val mongoDateTimeFormatter: SimpleDateFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val ObjectDateTimeFormatter: SimpleDateFormat =
            SimpleDateFormat("EEE, d MMM yyyy HH:mm")

        fun getFormattedDuration(duration: Long, context: Context): String {
            return context.getString(
                R.string.bookings_duration_pattern,
                duration / MINUTES_PER_HOUR,
                duration % MINUTES_PER_HOUR
            )
        }
    }
}