package com.sit.capstone_lionfleet.core.di

import com.sit.capstone_lionfleet.business.bookings.BookingsRepository
import com.sit.capstone_lionfleet.business.bookings.network.BookingApi
import com.sit.capstone_lionfleet.business.bookings.network.BookingEntityMapper
import com.sit.capstone_lionfleet.business.bookings.network.VehicleScheduleEntityMapper
import com.sit.capstone_lionfleet.business.bookings.ongoing.OngoingRepository
import com.sit.capstone_lionfleet.business.bookings.scheduled.ScheduledRepository
import com.sit.capstone_lionfleet.business.map.StationRepository
import com.sit.capstone_lionfleet.business.map.network.StationApi
import com.sit.capstone_lionfleet.business.map.network.StationVehicleAvailabilityMapper
import com.sit.capstone_lionfleet.business.map.network.VehicleEntityMapper
import com.sit.capstone_lionfleet.business.profile.ProfileRepository
import com.sit.capstone_lionfleet.business.profile.network.ProfileApi
import com.sit.capstone_lionfleet.business.profile.network.ProfileEntityMapper
import com.sit.capstone_lionfleet.business.vehiclebooking.VehicleBookingRepository
import com.sit.capstone_lionfleet.business.vehiclebooking.network.VehicleBookingApi
import com.sit.capstone_lionfleet.dataSource.local.dao.BookingDao
import com.sit.capstone_lionfleet.dataSource.local.dao.UserDao
import com.sit.capstone_lionfleet.dataSource.local.dao.VehicleDao
import com.sit.capstone_lionfleet.dataSource.local.model.BookingCacheMapper
import com.sit.capstone_lionfleet.dataSource.local.model.UserCacheMapper
import com.sit.capstone_lionfleet.dataSource.local.model.VehicleCacheMapper
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
        userCacheMapper: UserCacheMapper,
        preferenceProvider: PreferenceProvider

    ): ProfileRepository {
        return ProfileRepository(
            profileApi,
            userDao,
            profileEntityMapper,
            userCacheMapper,
            preferenceProvider
        )
    }

    @Singleton
    @Provides
    fun provideBookingRepository(
        bookingApi: BookingApi,
        bookingDao: BookingDao,
        bookingEntityMapper: BookingEntityMapper,
        bookingCacheMapper: BookingCacheMapper,
        preferenceProvider: PreferenceProvider
    ): BookingsRepository {
        return BookingsRepository(
            bookingApi,
            bookingDao,
            bookingEntityMapper,
            bookingCacheMapper,
            preferenceProvider
        )
    }

    @Singleton
    @Provides
    fun provideStationRepository(
        stationApi: StationApi,
        vehicleDao: VehicleDao,
        vehicleEntityMapper: VehicleEntityMapper,
        stationVehicleAvailabilityMapper: StationVehicleAvailabilityMapper,
        vehicleCacheMapper: VehicleCacheMapper,
        preferenceProvider: PreferenceProvider
    ): StationRepository {
        return StationRepository(
            stationApi,
            vehicleDao,
            vehicleEntityMapper,
            stationVehicleAvailabilityMapper,
            vehicleCacheMapper,
            preferenceProvider

        )
    }

    @Singleton
    @Provides
    fun provideVehicleBookingRepository(
        vehicleBookingApi: VehicleBookingApi,
        vehicleDao: VehicleDao,
        userDao: UserDao,
        vehicleScheduleEntityMapper: VehicleScheduleEntityMapper,
        preferenceProvider: PreferenceProvider
    ): VehicleBookingRepository {
        return VehicleBookingRepository(
            vehicleBookingApi,
            vehicleDao,
            userDao,
            vehicleScheduleEntityMapper,
            preferenceProvider
        )
    }

    @Singleton
    @Provides
    fun provideScheduledRepository(
        api: BookingApi,

        ): ScheduledRepository {
        return ScheduledRepository(api)
    }

    @Singleton
    @Provides
    fun provideOngoingRepository(
        api: BookingApi,
        bookingEntityMapper: BookingEntityMapper
    ): OngoingRepository {
        return OngoingRepository(api, bookingEntityMapper)
    }
}