package com.sit.capstone_lionfleet.business.bookings.network.response


import com.google.gson.annotations.SerializedName

data class WorldTimeResponse(
    val abbreviation: String,
    @SerializedName("client_ip")
    val clientIp: String,
    val datetime: String,
    @SerializedName("day_of_week")
    val dayOfWeek: Int,
    @SerializedName("day_of_year")
    val dayOfYear: Int,
    val dst: Boolean,
    @SerializedName("dst_from")
    val dstFrom: Any,
    @SerializedName("dst_offset")
    val dstOffset: Int,
    @SerializedName("dst_until")
    val dstUntil: Any,
    @SerializedName("raw_offset")
    val rawOffset: Int,
    val timezone: String,
    val unixtime: Int,
    @SerializedName("utc_datetime")
    val utcDatetime: String,
    @SerializedName("utc_offset")
    val utcOffset: String,
    @SerializedName("week_number")
    val weekNumber: Int
)