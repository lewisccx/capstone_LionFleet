package com.sit.capstone_lionfleet.business.bookings.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.sit.capstone_lionfleet.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OngoingTripService : LifecycleService() {

    val TAG = "OngoingTripService"

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    lateinit var currentNotificationBuilder: NotificationCompat.Builder

    companion object {
        //val timeInMilliSecond = MutableLiveData<Long>()
        const val NOTIFICATION_CHANNEL_ID = "ONGOING_TRIP_CHANNEL"
        const val NOTIFICATION_CHANNEL_NAME = "ONGOING_TRIP"

        //AT LEAST 1
        const val NOTIFICATION_ID = 1

        val isVehicleLocked = MutableLiveData<Boolean>()


    }
    override fun onCreate() {
        super.onCreate()
        postInitialValue()

        currentNotificationBuilder = baseNotificationBuilder

        isVehicleLocked.observe(this, Observer {
            updateNotificationForVehicleStatus(it)
        })
    }

    private fun postInitialValue(){
        isVehicleLocked.postValue(true)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            when (it.action) {
                OngoingTripAction.ACTION_TRIP_STARTED.action -> {
                    startForegroundService()
                    Log.d(TAG, "ACTION_TRIP_STARTED")
                }
                OngoingTripAction.ACTION_CAR_LOCKED.action -> {
                    val title = "Your vehicle has locked......"
                    //updateNotification(title)
                    lockVehicle()
                }
                OngoingTripAction.ACTION_CAR_UNLOCKED.action -> {
                    val title = "Your vehicle has unlocked......"
                   // updateNotification(title)
                    unlockVehicle()
                }
                OngoingTripAction.ACTION_TRIP_END.action -> {
                    stopSelf()
                }
                else -> Log.d(TAG, "ACTION_TRIP_IDLE")
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
    private fun lockVehicle(){
        isVehicleLocked.postValue(true)
    }
    private fun unlockVehicle(){
        isVehicleLocked.postValue(false)
    }
    private fun updateNotification(vehicleLock: String) {
        //set pending intent here for notification if wish to change to another landing destination on click
        // define your intent in business activity also
        //set global action of your intent in mobile_navigation

        val notification = currentNotificationBuilder
            .setContentText(vehicleLock)


        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }

    private fun startForegroundService() {
        isVehicleLocked.postValue(false)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }
        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())
    }

    private fun updateNotificationForVehicleStatus(isVehicleLocked: Boolean) {
        val notificationTitleText =
            if (isVehicleLocked) "Your vehicle has locked" else "Your vehicle has unlocked"
        val notificationContentText = if (isVehicleLocked) "Do not leave your valuables in the vehicle" else "Enjoy your trip"

        val notificationActionText = if (isVehicleLocked) "Unlock Vehicle" else "Lock Vehicle"
        val pendingIntent = if (isVehicleLocked) {
            val unlockIntent = Intent(this, OngoingTripService::class.java).apply {
                action = OngoingTripAction.ACTION_CAR_UNLOCKED.action
            }
            PendingIntent.getService(this, 1, unlockIntent, FLAG_UPDATE_CURRENT)
        } else {
            val lockIntent = Intent(this, OngoingTripService::class.java).apply {
                action = OngoingTripAction.ACTION_CAR_LOCKED.action
            }
            PendingIntent.getService(this, 2, lockIntent, FLAG_UPDATE_CURRENT)
        }
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        currentNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(currentNotificationBuilder, ArrayList<NotificationCompat.Action>())
        }

        currentNotificationBuilder = baseNotificationBuilder
            .addAction(R.drawable.ic_lock_icon, notificationActionText, pendingIntent)
            .setContentTitle(notificationTitleText)
            .setContentText(notificationContentText)

        notificationManager.notify(NOTIFICATION_ID, currentNotificationBuilder.build())
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
    }
}