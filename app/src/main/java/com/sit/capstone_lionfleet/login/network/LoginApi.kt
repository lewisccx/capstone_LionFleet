package com.sit.capstone_lionfleet.login.network

import com.sit.capstone_lionfleet.login.network.request.LoginRequest
import com.sit.capstone_lionfleet.login.network.response.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {

    @POST("auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ) : LoginResponse
}