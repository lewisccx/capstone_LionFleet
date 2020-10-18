package com.sit.capstone_lionfleet.business

import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.alan.alansdk.AlanCallback
import com.alan.alansdk.AlanConfig
import com.alan.alansdk.button.AlanButton
import com.alan.alansdk.events.EventCommand
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sit.capstone_lionfleet.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class BusinessActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var navView: BottomNavigationView
    private lateinit var fabNavigate: FloatingActionButton
    private lateinit var alanBtn: AlanButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business)
        initNavView()
        initAlan()
    }

    private fun initAlan() {
        // Alan config object
        alanBtn = findViewById(R.id.alan_button)
        val config = AlanConfig.builder()
            .setProjectId(resources.getString(R.string.alan_access_token))
            .build()

        alanBtn.initWithConfig(config)

        val myCallback: AlanCallback = object : AlanCallback() {
            override fun onCommandReceived(eventCommand: EventCommand) {
                super.onCommandReceived(eventCommand)
                //Handle command here
            }
        }
        alanBtn.registerCallback(myCallback)
    }

    private fun initNavView() {

        navController = findNavController(R.id.navHostFragment)
        navView = findViewById(R.id.navView)
        navView.background = null
        navView.setupWithNavController(navController)
        fabNavigate = findViewById(R.id.fab_navigate)
        fabNavigate.setOnClickListener {
            when (navController.currentDestination?.id) {
                R.id.navigation_bookings, R.id.navigation_ongoing, R.id.navigation_profile -> {

                    navController.navigate(
                        R.id.navigation_map

                    )
                    val param = alanBtn.layoutParams as ViewGroup.MarginLayoutParams
                    param.bottomMargin = 135
                    alanBtn.layoutParams = param
                }
            }
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_map -> {
                   changeStatusBarColor()

                }
                R.id.navigation_profile, R.id.navigation_ongoing, R.id.navigation_bookings -> {
                    val param = alanBtn.layoutParams as ViewGroup.MarginLayoutParams
                    param.bottomMargin = 135
                    alanBtn.layoutParams = param
                }
                else -> changeStatusBarColor()
            }
        }
    }

    private fun changeStatusBarColor(@ColorRes colorRes: Int = R.color.white) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = getColor(colorRes)
    }

    override fun onBackPressed() {
        when (navController.currentDestination?.id ?: super.onBackPressed()) {
            R.id.navigation_map -> finish()
            R.id.navigation_bookings -> navController.navigate(
                R.id.navigation_map
            )
            else -> super.onBackPressed()
        }
    }
}