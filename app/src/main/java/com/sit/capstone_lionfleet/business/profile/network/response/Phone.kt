package com.sit.capstone_lionfleet.business.profile.network.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Phone(
    @SerializedName("countryCode")
    val countryCode: String,

    @SerializedName("phoneNumber")
    val phoneNumber: String
)