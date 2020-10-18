package com.sit.capstone_lionfleet.business.map.network.response

import com.google.gson.annotations.SerializedName

data class Station (
    @SerializedName("_id") val id: String,
    val name: String
)