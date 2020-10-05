package com.sit.capstone_lionfleet.business.profile.network.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Address(
    @SerializedName("city")
    val city: String,
    @SerializedName("postcode")
    val postcode: String,
    @SerializedName("street")
    val street: String,
    @SerializedName("streetNumber")
    val streetNumber: String
)