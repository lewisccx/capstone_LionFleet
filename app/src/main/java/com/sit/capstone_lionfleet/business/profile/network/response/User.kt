package com.sit.capstone_lionfleet.business.profile.network.response


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("address")
    val address: Address,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("_id")
    val id: String,
    @SerializedName("isActivated")
    val isActivated: Boolean,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("phone")
    val phone: Phone,
    @SerializedName("updatedAt")
    val updatedAt: String
)