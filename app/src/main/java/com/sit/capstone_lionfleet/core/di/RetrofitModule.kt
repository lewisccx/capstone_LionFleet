package com.sit.capstone_lionfleet.core.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sit.capstone_lionfleet.BuildConfig
import com.sit.capstone_lionfleet.business.bookings.network.BookingApi
import com.sit.capstone_lionfleet.business.map.network.StationApi
import com.sit.capstone_lionfleet.business.profile.network.ProfileApi
import com.sit.capstone_lionfleet.business.vehiclebooking.network.VehicleBookingApi
import com.sit.capstone_lionfleet.login.network.LoginApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object RetrofitModule {

    @Singleton
    @Provides
    fun provideGsonBuilder(): Gson {
        return GsonBuilder()
            .create()
    }

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson, preferenceProvider: PreferenceProvider): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl("https://capstone-cityfleet-api.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
    }

//    @Singleton
//    @Provides
//    fun provideWorldTimeRetrofit(gson: Gson): Retrofit.Builder {
//        return Retrofit.Builder()
//            .baseUrl("http://worldtimeapi.org/api/timezone/")
//            .addConverterFactory(GsonConverterFactory.create(gson))
//    }

    @Singleton
    @Provides
    fun provideLoginApi(retrofit: Retrofit.Builder): LoginApi {
        return retrofit.build()
            .create(LoginApi::class.java)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(preferenceProvider: PreferenceProvider) = if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        val authToken = preferenceProvider.getAuthToken()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient
            .Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                chain.proceed(chain.request().newBuilder().also {
                    it.addHeader("Authorization", "Bearer $authToken")
                }.build())
            }
            .build()
    } else OkHttpClient
        .Builder()
        .addInterceptor { chain ->
            chain.proceed(chain.request().newBuilder().also {
                it.addHeader("Authorization", preferenceProvider.getAuthToken()!!)
            }.build())
        }
        .build()

    @Singleton
    @Provides
    fun provideProfileApi(retrofit: Retrofit.Builder, okHttpClient: OkHttpClient): ProfileApi {
        return retrofit.client(
            okHttpClient
        ).build().create(ProfileApi::class.java)
    }

    @Singleton
    @Provides
    fun provideBookingApi(retrofit: Retrofit.Builder, okHttpClient: OkHttpClient): BookingApi {
        return retrofit.client(
            okHttpClient
        ).build().create(BookingApi::class.java)
    }

    @Singleton
    @Provides
    fun provideStationApi(retrofit: Retrofit.Builder, okHttpClient: OkHttpClient): StationApi {
        return retrofit.client(
            okHttpClient
        ).build().create(StationApi::class.java)
    }
    @Singleton
    @Provides
    fun provideVehicleBookingApi(retrofit: Retrofit.Builder, okHttpClient: OkHttpClient): VehicleBookingApi{
        return retrofit.client(
            okHttpClient
        ).build().create(VehicleBookingApi::class.java)
    }

//    @Singleton
//    @Provides
//    fun provideWorldTimeApi(retrofit: Retrofit.Builder, okHttpClient: OkHttpClient): WorldTimeApi{
//        return retrofit.client(
//            okHttpClient
//        ).build().create(WorldTimeApi::class.java)
//    }
}