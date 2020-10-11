package com.sit.capstone_lionfleet.business

import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sit.capstone_lionfleet.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class BusinessActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var navView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_business)
        initNavView()
    }

    private fun initNavView() {
        navController = findNavController(R.id.navHostFragment)
        navView = findViewById(R.id.navView)
        navView.setupWithNavController(navController)
//        navController.addOnDestinationChangedListener { _, destination, _ ->
//            when (destination.id) {
//                R.id.navigation_map -> {
//                    changeStatusBarColor()
//                }
//                R.id.navigation_profile -> {
//                   // changeStatusBarColor(R.color.primary_color)
//                }
//                else -> changeStatusBarColor()
//            }
//        }
    }

//    private fun changeStatusBarColor(@ColorRes colorRes: Int = R.color.white) {
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//        window.statusBarColor = getColor(colorRes)
//    }

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