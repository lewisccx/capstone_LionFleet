package com.sit.capstone_lionfleet.business.profile.network

import com.sit.capstone_lionfleet.business.profile.network.response.ProfileResponse
import retrofit2.http.GET

interface ProfileApi {

    @GET("user")
    suspend fun getUserProfile(): ProfileResponse
}