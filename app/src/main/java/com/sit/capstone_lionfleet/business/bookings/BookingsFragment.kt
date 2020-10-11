package com.sit.capstone_lionfleet.business.bookings

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.sit.capstone_lionfleet.R
import com.sit.capstone_lionfleet.base.response.Resource
import com.sit.capstone_lionfleet.business.bookings.adapter.ViewPagerAdapter
import com.sit.capstone_lionfleet.business.bookings.history.HistoryFragment
import com.sit.capstone_lionfleet.business.bookings.ongoing.OngoingFragment
import com.sit.capstone_lionfleet.business.bookings.scheduled.ScheduledFragment
import com.sit.capstone_lionfleet.business.profile.ProfileViewModel
import com.sit.capstone_lionfleet.core.extension.hide
import com.sit.capstone_lionfleet.core.extension.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_bookings.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.scheduled_fragment.*

@AndroidEntryPoint
class BookingsFragment : Fragment(R.layout.fragment_bookings) {


    val TAG = "BookingsFragment"
    private val viewModel: BookingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bookings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "onViewCreated")
        setUpTabs()
    }

    private fun setUpTabs() {
        val adapter = ViewPagerAdapter(supportFragmentManager = this.childFragmentManager)
        adapter.addFragment(ScheduledFragment(), resources.getString(R.string.scheduled_title))
        adapter.addFragment(HistoryFragment(), resources.getString(R.string.history_title))
        bookingViewPager.adapter = adapter
        bookingTabLayout.setupWithViewPager(bookingViewPager)
        bookingTabLayout.getTabAt(0)!!.setIcon(R.drawable.ic_schedule)
        bookingTabLayout.getTabAt(1)!!.setIcon(R.drawable.ic_booking_history)

    }


}