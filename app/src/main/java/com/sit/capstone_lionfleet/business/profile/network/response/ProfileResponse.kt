package com.sit.capstone_lionfleet.business.profile.network.response

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @SerializedName("user") val user: User
)