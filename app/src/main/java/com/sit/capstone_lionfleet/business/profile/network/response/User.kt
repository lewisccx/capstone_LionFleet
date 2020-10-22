package com.sit.capstone_lionfleet.business.profile.network.response

import com.google.gson.annotations.SerializedName

data class User(

    @SerializedName("isActivated")
    val isActivated: Boolean,
    @SerializedName("licenseActivated")
    val licenseActivated: Boolean,
    @SerializedName("email")
    val email: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("phone")
    val phone: Phone,
    @SerializedName("address")
    val address: Address,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String
)