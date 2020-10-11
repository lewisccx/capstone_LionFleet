package com.sit.capstone_lionfleet.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sit.capstone_lionfleet.R
import com.sit.capstone_lionfleet.business.BusinessActivity
import com.sit.capstone_lionfleet.core.di.PreferenceProvider
import com.sit.capstone_lionfleet.login.ui.LoginActivity
import com.sit.capstone_lionfleet.utils.startNewActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var preferenceProvider: PreferenceProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        if (preferenceProvider.getAuthToken() != null){
           this@SplashActivity.startNewActivity(BusinessActivity::class.java)
        }else{
            this@SplashActivity.startNewActivity(LoginActivity::class.java)
        }
    }
}