package com.sit.capstone_lionfleet.login.repository

import com.sit.capstone_lionfleet.base.repository.BaseRepository
import com.sit.capstone_lionfleet.base.response.Resource
import com.sit.capstone_lionfleet.core.di.PreferenceProvider
import com.sit.capstone_lionfleet.login.network.LoginApi
import com.sit.capstone_lionfleet.login.network.request.LoginRequest
import com.sit.capstone_lionfleet.login.network.response.LoginResponse
import com.sit.capstone_lionfleet.utils.DataState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import javax.inject.Inject

class LoginRepository
@Inject
constructor(
    private val api: LoginApi,
    private val preferences: PreferenceProvider
) :BaseRepository() {

    suspend fun login(
        loginRequest: LoginRequest
    ): Flow<Resource<LoginResponse>> = flow {
        emit(Resource.Loading)
        delay(1000)
        val loginResponse = safeApiCall {
            api.login(loginRequest)
        }
        emit(loginResponse)
    }

    fun saveAuthToken(token: String) {
        preferences.saveAuthToken(token)
    }
}