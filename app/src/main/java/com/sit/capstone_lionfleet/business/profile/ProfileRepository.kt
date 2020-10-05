package com.sit.capstone_lionfleet.business.profile

import android.content.Context
import com.sit.capstone_lionfleet.base.repository.BaseRepository
import com.sit.capstone_lionfleet.base.response.Resource
import com.sit.capstone_lionfleet.business.profile.network.ProfileApi
import com.sit.capstone_lionfleet.business.profile.network.ProfileEntityMapper
import com.sit.capstone_lionfleet.business.profile.network.response.ProfileResponse
import com.sit.capstone_lionfleet.dataSource.local.dao.UserDao
import com.sit.capstone_lionfleet.dataSource.local.model.UserCacheMapper
import com.sit.capstone_lionfleet.profile.network.model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import retrofit2.HttpException

class ProfileRepository
constructor(
    private val api: ProfileApi,
    private val userDao: UserDao,
    private val profileEntityMapper: ProfileEntityMapper,
    private val userCacheMapper: UserCacheMapper
) : BaseRepository() {
    suspend fun getProfile(): Flow<Resource<User>> = flow {
        emit(Resource.Loading)
        delay(1000)
        try {
            val profileResponse = api.getUserProfile()
            val userProfile = profileEntityMapper.mapFromEntity(profileResponse)
            userDao.insert(userCacheMapper.mapToEntity(userProfile))
            val cacheUserProfile = userDao.getUser()
            emit(Resource.Success(userCacheMapper.mapFromEntity(cacheUserProfile)))
        } catch (throwable: Throwable) {
            when (throwable) {
                is HttpException -> {
                    val httpFailure = Resource.Failure(
                        false,
                        throwable.code(),
                        JSONObject(
                            throwable.response()?.errorBody()!!.string()
                        ).getString("message")
                    )
                    emit(httpFailure)
                }
                else -> {
                    val unknownFailure = Resource.Failure(true,   null, throwable.message)
                    emit(unknownFailure)
                }
            }
        }

    }
}