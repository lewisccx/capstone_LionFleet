package com.sit.capstone_lionfleet.business.bookings.network.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Booking(
    val checkedInTs: String,
    val checkedOutTs: String,
    val createdAt: String,
    val distance: Int,
    @SerializedName("_id")
    val id: String,
    val reservedDate: String,
    val status: String,
    val updatedAt: String,
    val availability: Boolean,
    val brand: String,
    val imageUrl: String,
    val model: String,
    val plate: String,
    val stationName: String,
    val actualCost: Double,
    val expectedCost: Double,
    val costsPerDay: Int,
    val costsPerKm: Int,
    val costsPerHour: Int
) : Parcelable