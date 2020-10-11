package com.sit.capstone_lionfleet.business.bookings.scheduled

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.mapbox.mapboxsdk.style.expressions.Expression.`object`
import com.sit.capstone_lionfleet.R
import com.sit.capstone_lionfleet.base.response.Resource
import com.sit.capstone_lionfleet.business.bookings.BookingsStateEvent
import com.sit.capstone_lionfleet.business.bookings.BookingsViewModel
import com.sit.capstone_lionfleet.core.extension.hide
import com.sit.capstone_lionfleet.core.extension.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_bookings.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.scheduled_fragment.*

@AndroidEntryPoint
class ScheduledFragment : Fragment(R.layout.scheduled_fragment) {
    val TAG = "ScheduledFragment"
    private val viewModel: BookingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.scheduled_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        performBookingsAction(BookingsStateEvent.GetReservedBookings)
    }

    private fun performBookingsAction(event: BookingsStateEvent) {
        viewModel.setBookingsStateEvent(BookingsStateEvent.GetReservedBookings)
    }

    private fun observeViewModel() {
        viewModel.reservedBookingsDataState.observe(viewLifecycleOwner, Observer { dataState ->
            when (dataState) {
                is Resource.Success -> {
                    scheduledProgressBar.hide()
                    Toast.makeText(requireContext(), dataState.value.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
                is Resource.Failure -> {
                     scheduledProgressBar.hide()
                    displayError()
                }
                is Resource.Loading -> {
                    scheduledProgressBar.show()
                }
            }
        })
    }

    private fun displayError() {
        Toast.makeText(
            requireContext(),
            resources.getString(R.string.something_went_wrong_error),
            Toast.LENGTH_SHORT
        ).show()

    }


}