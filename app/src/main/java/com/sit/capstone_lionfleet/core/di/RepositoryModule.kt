package com.sit.capstone_lionfleet.core.di

import com.sit.capstone_lionfleet.business.profile.ProfileRepository
import com.sit.capstone_lionfleet.business.profile.network.ProfileApi
import com.sit.capstone_lionfleet.business.profile.network.ProfileEntityMapper
import com.sit.capstone_lionfleet.dataSource.local.dao.UserDao
import com.sit.capstone_lionfleet.dataSource.local.model.UserCacheMapper
import com.sit.capstone_lionfleet.login.network.LoginApi
import com.sit.capstone_lionfleet.login.repository.LoginRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object RepositoryModule {

    @Singleton
    @Provides
    fun provideLoginRepository(
        loginApi: LoginApi,
        preferenceProvider: PreferenceProvider
    ): LoginRepository {
        return LoginRepository(loginApi, preferenceProvider)
    }

    @Singleton
    @Provides
    fun provideProfileRepository(
        profileApi: ProfileApi,
        userDao: UserDao,
        profileEntityMapper: ProfileEntityMapper,
        userCacheMapper: UserCacheMapper

    ): ProfileRepository {
        return ProfileRepository(profileApi, userDao, profileEntityMapper, userCacheMapper)
    }
}