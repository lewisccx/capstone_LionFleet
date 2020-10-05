package com.sit.capstone_lionfleet.business.profile.network.response

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @SerializedName("message") val message: String,
    @SerializedName("user") val user: User
)