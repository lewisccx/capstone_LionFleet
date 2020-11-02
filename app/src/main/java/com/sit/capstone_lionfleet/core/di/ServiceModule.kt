package com.sit.capstone_lionfleet.core.di

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.sit.capstone_lionfleet.R
import com.sit.capstone_lionfleet.business.BusinessActivity
import com.sit.capstone_lionfleet.business.bookings.service.OngoingTripService
import com.sit.capstone_lionfleet.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @ExperimentalCoroutinesApi
    @ServiceScoped
    @Provides
    fun provideBusinessActivityPendingIntent(
        @ApplicationContext app: Context
    ) = PendingIntent.getActivity(
        app,
        0,
        Intent(app, BusinessActivity::class.java).also {
            it.action = Constants.ACTION_SHOW_ONGOING_TRIP_FRAGMENT

        },
        FLAG_UPDATE_CURRENT
    )!!

    @ServiceScoped
    @Provides
    fun provideBaseNotificationBuilder(
        @ApplicationContext app: Context,
        pendingIntent: PendingIntent
    ) = NotificationCompat.Builder(
        app, OngoingTripService.NOTIFICATION_CHANNEL_ID
    ).setAutoCancel(false)
        .setOngoing(true)
        .setSmallIcon(R.drawable.ic_launcher_icon)
        .setContentTitle("Your trip has started")
        .setContentText("Enjoy your trip! Tap here to view your current trip")
        .setContentIntent(pendingIntent)!!
}