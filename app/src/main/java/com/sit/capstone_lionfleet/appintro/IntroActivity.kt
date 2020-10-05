package com.sit.capstone_lionfleet.appintro

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.sit.capstone_cityfleet.appintro.IntroItem
import com.sit.capstone_cityfleet.appintro.IntroVIewPagerAdapter
import com.sit.capstone_cityfleet.appintro.ZoomOutPageTransformer
import com.sit.capstone_lionfleet.R
import com.sit.capstone_lionfleet.databinding.ActivityIntroBinding
import com.sit.capstone_lionfleet.login.ui.LoginActivity
import es.dmoral.toasty.Toasty
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber

class IntroActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks,
    EasyPermissions.RationaleCallbacks {

    private var btnAnim: Animation? = null
    private var position: Int = 0
    private var mList: MutableList<IntroItem> = ArrayList()
    private val TAG = "IntroActivity"
    private val RC_LOCATION_PERM = 124
    private lateinit var binding: ActivityIntroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (restorePrefData()) {
            val mainActivity = Intent(this, LoginActivity::class.java)
            startActivity(mainActivity)
            finish()
        }
        binding = ActivityIntroBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initViewPager()
        initBtns()
    }

    private fun initViewPager() {
        btnAnim = AnimationUtils.loadAnimation(
            this,
            R.anim.appintro_btn_anim
        )

        mList.add(
            IntroItem(
                "Locate Us",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua, consectetur  consectetur adipiscing elit",
                R.drawable.ic_appintro_locate_us
            )
        )
        mList.add(
            IntroItem(
                "CHOOSE THE RIDE",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua, consectetur  consectetur adipiscing elit",
                R.drawable.ic_appintro_choose_your_ride
            )
        )
        mList.add(
            IntroItem(
                "Make Booking with our Chatbot",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua, consectetur  consectetur adipiscing elit",
                R.drawable.ic_appintro_chatbot
            )
        )
        mList.add(
            IntroItem(
                "YOU'RE ON YOUR WAY",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua, consectetur  consectetur adipiscing elit",
                R.drawable.ic_appintro_on_your_way
            )
        )

        binding.screenViewpager.adapter = IntroVIewPagerAdapter(this@IntroActivity, mList)
        binding.screenViewpager.setPageTransformer(true, ZoomOutPageTransformer())
        binding.tabIndicator.setupWithViewPager(binding.screenViewpager)
    }

    private fun initBtns() {
        binding.btnNext.setOnClickListener {
            position = binding.screenViewpager.currentItem
            if (position < mList.size) {
                position++
                binding.screenViewpager.currentItem = position
                loadScreen()
            }
            if (position == (mList.size - 1)) {
                loadLastScreen()
            }
        }

        binding.tabIndicator.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == (mList.size - 1)) {
                    loadLastScreen()
                } else {
                    loadScreen()
                }
            }

        })

        binding.btnGetStarted.setOnClickListener {
            locationTask()
        }

        binding.tvSkip.setOnClickListener {
            binding.screenViewpager.currentItem = mList.size
        }

        binding.cbIntro.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                savePrefsData()
            } else
                restorePrefData()
        }
    }

    private fun hasLocationPermission(): Boolean {
        return EasyPermissions.hasPermissions(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION

        )
    }

    private fun locationTask() {
        if (hasLocationPermission()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.rationale_location),
                RC_LOCATION_PERM,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    private fun loadScreen() {
        binding.btnNext.visibility = View.VISIBLE
        binding.btnGetStarted.visibility = View.INVISIBLE
        binding.tvSkip.visibility = View.VISIBLE
        binding.tabIndicator.visibility = View.VISIBLE
        binding.cbIntro.visibility = View.INVISIBLE
    }

    private fun restorePrefData(): Boolean {
        val pref = applicationContext.getSharedPreferences(
            "introPrefs",
            Context.MODE_PRIVATE
        )
        return pref.getBoolean("isIntroOpened", false)
    }

    private fun savePrefsData() {
        val pref = applicationContext.getSharedPreferences(
            "introPrefs",
            Context.MODE_PRIVATE
        )
        val editor = pref.edit()
        editor.putBoolean("isIntroOpened", true)
        editor.apply()
    }

    // show the GETSTARTED Button and hide the indicator and the next button
    private fun loadLastScreen() {
        binding.btnNext.visibility = View.INVISIBLE
        binding.tvSkip.visibility = View.INVISIBLE
        binding.tabIndicator.visibility = View.INVISIBLE

        binding.cbIntro.animation = btnAnim
        binding.cbIntro.animation.start()
        binding.cbIntro.visibility = View.VISIBLE

        binding.btnGetStarted.animation = btnAnim
        binding.btnGetStarted.animation.start()
        binding.btnGetStarted.visibility = View.VISIBLE


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)


    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Timber.tag(TAG).d("onPermissionsGranted:%s", requestCode)
    }

    override fun onRationaleDenied(requestCode: Int) {
        Timber.tag(TAG).d("onRationaleDenied:%s", requestCode)
        if (requestCode == 124) {
            // Ask for one permission
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.rationale_location),
                RC_LOCATION_PERM,
                Manifest.permission.ACCESS_FINE_LOCATION
            )

            Toasty.warning(this, "permission denied ${requestCode}");
        }

    }


    override fun onRationaleAccepted(requestCode: Int) {
        Timber.tag(TAG).d("onRationaleAccepted:%s", requestCode)
    }
}