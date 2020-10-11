package com.sit.capstone_lionfleet.core.di

import android.content.Context
import android.service.autofill.UserData
import androidx.room.Room
import com.sit.capstone_lionfleet.business.bookings.network.model.Booking
import com.sit.capstone_lionfleet.dataSource.local.AppDatabase
import com.sit.capstone_lionfleet.dataSource.local.dao.BookingDao
import com.sit.capstone_lionfleet.dataSource.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object RoomModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        context.applicationContext.deleteDatabase(AppDatabase.DATABASE_NAME)
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.getUserDao()
    }
    @Singleton
    @Provides
    fun provideBookingDao(appDatabase: AppDatabase): BookingDao {
        return appDatabase.getBookingDao()
    }


}