package com.sit.capstone_lionfleet.business

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.alan.alansdk.AlanCallback
import com.alan.alansdk.AlanConfig
import com.alan.alansdk.button.AlanButton
import com.alan.alansdk.events.EventCommand
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mapbox.android.core.permissions.PermissionsListener
import com.sit.capstone_lionfleet.R
import com.sit.capstone_lionfleet.business.map.LOCATION_PERMISSIONS_REQUEST_CODE
import com.sit.capstone_lionfleet.business.map.LocationPermissionsHelper
import com.sit.capstone_lionfleet.core.extension.enable
import com.sit.capstone_lionfleet.utils.Constants.Companion.ACTION_SHOW_ONGOING_TRIP_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_business.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*



@ExperimentalCoroutinesApi
@AndroidEntryPoint
class BusinessActivity : AppCompatActivity(), PermissionsListener {
    private val permissionsHelper = LocationPermissionsHelper(this)

    val TAG = "BusinessActivity"
    private lateinit var navController: NavController
    private lateinit var navView: BottomNavigationView
    private lateinit var fabNavigate: FloatingActionButton
    private  var alanBtn: AlanButton?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business)
        initNavView()
        initAlan()
        navigateToOngoingFragmentIfNeeded(intent)

    }


    private fun initAlan() {
        // Alan config object
        alanBtn = findViewById(R.id.alan_button)
        val config = AlanConfig.builder()
            .setProjectId(resources.getString(R.string.alan_access_token))
            .build()

        alanBtn!!.initWithConfig(config)

        val myCallback: AlanCallback = object : AlanCallback() {
            @SuppressLint("LogNotTimber")
            override fun onCommandReceived(eventCommand: EventCommand) {
                super.onCommandReceived(eventCommand)
                eventCommand?.data?.let {
                    Log.d(TAG, it.toString())
                    val commandObject = it.optJSONObject("data")
                    Log.d(TAG, "${commandObject.optString("command")}")
                    if (commandObject.optString("command") == "navigation") {
                        if (commandObject.optString("route") == "forward") {
                            when (navController.currentDestination?.id) {
                                R.id.navigation_map -> {
                                    navController.navigate(R.id.navigation_ongoing)
                                }

                                R.id.navigation_ongoing -> {
                                    navController.navigate(R.id.navigation_bookings)
                                }
                                R.id.navigation_bookings -> {
                                    navController.navigate(R.id.navigation_profile)
                                }
                                R.id.navigation_profile -> {
                                    navController.navigate(R.id.navigation_map)
                                }

                            }
                        } else if (commandObject.optString("route") == "back") {
                            when (navController.currentDestination?.id) {
                                R.id.navigation_map -> {
                                    Log.d(TAG, "map")
                                    navController.navigate(R.id.navigation_profile)
                                }
                                R.id.navigation_ongoing -> {

                                    navController.navigate(R.id.navigation_map)
                                }
                                R.id.navigation_bookings -> {
                                    navController.navigate(R.id.navigation_ongoing)
                                }
                                R.id.navigation_profile -> {
                                    navController.navigate(R.id.navigation_bookings)
                                }

                            }
                        }
                    }
                }
            }
        }
        alanBtn!!.registerCallback(myCallback)
    }

    private fun initNavView() {

        navController = findNavController(R.id.navHostFragment)
        navView = findViewById(R.id.navView)
        navView.background = null
        navView.setupWithNavController(navController)
        fabNavigate = findViewById(R.id.fab_navigate)
        fabNavigate.setOnClickListener {
            when (navController.currentDestination?.id) {
                R.id.navigation_bookings, R.id.navigation_ongoing, R.id.navigation_profile, R.id.vehicleBookingFragment -> {

                    navController.navigate(
                        R.id.navigation_map

                    )
                    val param = alanBtn!!.layoutParams as ViewGroup.MarginLayoutParams
                    param.bottomMargin = 135
                    alanBtn!!.layoutParams = param
                }

            }
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_map -> {
                    changeStatusBarColor()
                    fabNavigate.enable(true)
                }
                R.id.navigation_profile, R.id.navigation_ongoing, R.id.navigation_bookings, R.id.vehicleBookingFragment -> {

                    val param = alanBtn!!.layoutParams as ViewGroup.MarginLayoutParams
                    param.bottomMargin = 135
                    alanBtn!!.layoutParams = param
                    fabNavigate.enable(false)
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
            R.id.carKeyFragment -> navController.navigate(
                R.id.navigation_ongoing
            )
            else -> super.onBackPressed()
        }
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Toast
            .makeText(
                this,
                "This app needs location and storage permissions" +
                        "in order to show its functionality.",
                Toast.LENGTH_LONG
            ).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            requestPermissionIfNotGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else {
            Toast.makeText(this, "You didn't grant location permissions.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSIONS_REQUEST_CODE) {
            permissionsHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
        } else {
            if (
                !(grantResults.isNotEmpty() &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED)
            ) {
                Toast.makeText(
                    this,
                    "You didn't grant storage or location permissions.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun requestPermissionIfNotGranted(permission: String) {
        val permissionsNeeded = ArrayList<String>()
        if (ContextCompat
                .checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsNeeded.add(permission)
            ActivityCompat.requestPermissions(this, permissionsNeeded.toTypedArray(), 10)
        }
    }

    private fun navigateToOngoingFragmentIfNeeded(intent: Intent?){
        if(intent?.action == ACTION_SHOW_ONGOING_TRIP_FRAGMENT){
            navController.navigate(R.id.action_global_to_navigation_ongoing)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToOngoingFragmentIfNeeded(intent)
    }
}

