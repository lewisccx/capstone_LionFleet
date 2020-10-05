package com.sit.capstone_lionfleet.profile.network.model

data class User(

        val postcode: String,
        val street: String,
        val streetNumber: String,
        val city: String,
        val email: String,
        val firstName: String,
        val isActivated: Boolean,
        val lastName: String,
        val countryCode: String,
        val phoneNumber: String

)