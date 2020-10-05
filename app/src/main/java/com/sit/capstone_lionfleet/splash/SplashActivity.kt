package com.sit.capstone_lionfleet.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.sit.capstone_lionfleet.R
import com.sit.capstone_lionfleet.dataSource.local.UserPreferences
import com.sit.capstone_lionfleet.business.BusinessActivity
import com.sit.capstone_lionfleet.login.ui.LoginActivity
import com.sit.capstone_lionfleet.utils.startNewActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }
}