package com.sit.capstone_lionfleet.business.profile

import com.sit.capstone_lionfleet.base.repository.BaseRepository
import com.sit.capstone_lionfleet.base.response.Resource
import com.sit.capstone_lionfleet.business.profile.network.ProfileApi
import com.sit.capstone_lionfleet.business.profile.network.ProfileEntityMapper
import com.sit.capstone_lionfleet.core.di.PreferenceProvider
import com.sit.capstone_lionfleet.dataSource.local.dao.UserDao
import com.sit.capstone_lionfleet.dataSource.local.model.UserCacheMapper
import com.sit.capstone_lionfleet.profile.network.model.User
import com.sit.capstone_lionfleet.utils.Constants
import com.sit.capstone_lionfleet.utils.isFetchNeeded
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import retrofit2.HttpException
import java.time.LocalDateTime

class ProfileRepository
constructor(
    private val api: ProfileApi,
    private val userDao: UserDao,
    private val profileEntityMapper: ProfileEntityMapper,
    private val userCacheMapper: UserCacheMapper,
    private val preferenceProvider: PreferenceProvider
) : BaseRepository() {
    suspend fun getProfile(): Flow<Resource<User>> = flow {
        emit(Resource.Loading)
        try {
            val timeStamp = preferenceProvider.geValueFromPref(Constants.USER_API_CALL_TIMESTAMP)
            if (timeStamp == null || isFetchNeeded(
                    LocalDateTime.parse(timeStamp),
                    Constants.USER_API_CALL_LIMIT
                )
            ) {

                val profileResponse = api.getUserProfile()
                val userProfile = profileEntityMapper.mapFromEntity(profileResponse)
                userDao.insert(userCacheMapper.mapToEntity(userProfile))
                preferenceProvider.saveAsPref(
                    Constants.USER_API_CALL_TIMESTAMP,
                    LocalDateTime.now().toString()
                )
            }
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
                    val unknownFailure = Resource.Failure(true, null, throwable.message)
                    emit(unknownFailure)
                }
            }
        }

    }
}