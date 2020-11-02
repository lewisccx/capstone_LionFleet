package com.sit.capstone_lionfleet.application

import android.app.Application
import com.mapbox.mapboxsdk.Mapbox
import com.sit.capstone_lionfleet.R
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LionFleetApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Mapbox.getInstance(applicationContext, getString(R.string.mapbox_access_token))

    }
}