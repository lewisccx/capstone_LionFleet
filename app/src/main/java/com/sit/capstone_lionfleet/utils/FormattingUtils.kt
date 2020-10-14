package com.sit.capstone_lionfleet.utils

import android.content.Context
import com.sit.capstone_lionfleet.R

class FormattingUtils {

    companion object {
        const val NUMBER_OF_DECIMALS = 2

        fun getFormattedStationDistance(distance: String, context: Context): String {
            return context.getString(
                R.string.station_distance_pattern, distance
            )
        }

        fun getFormattedBookingDistance(distance: String, context: Context): String {
            return context.getString(
                R.string.bookings_distance_pattern, distance
            )
        }

        fun getFormattedModelBrand(brand: String?, model: String?, context: Context): String {
            return context.getString(
                R.string.bookings_model_pattern, brand, model
            )
        }

        fun getFormattedCostsPerKm(cost: Double?, context: Context): String {
            return context.getString(
                R.string.bookings_price_per_km_pattern, cost
            )
        }

        fun getFormattedBookingPrice(
            actualCost: Double?,
            expectedCost: Double?,
            context: Context
        ): String {
            return context.getString(
                R.string.bookings_price_pattern,
                actualCost ?: expectedCost ?: -1.0
            )
        }

        fun getFormattedTimeLeft(time: String, context: Context): String {
            return context.getString(
                R.string.est_dist_time_left_txt, time
            )
        }
    }
}
