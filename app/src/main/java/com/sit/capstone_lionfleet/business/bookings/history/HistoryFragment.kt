package com.sit.capstone_lionfleet.business.bookings.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.sit.capstone_lionfleet.R
import com.sit.capstone_lionfleet.base.response.Resource
import com.sit.capstone_lionfleet.business.bookings.BookingsStateEvent
import com.sit.capstone_lionfleet.business.bookings.BookingsViewModel
import com.sit.capstone_lionfleet.core.extension.hide
import com.sit.capstone_lionfleet.core.extension.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.history_fragment.*

@AndroidEntryPoint
class HistoryFragment : Fragment() {
    val TAG = "HistoryFragment"

    private val viewModel: BookingsViewModel by viewModels()
    private lateinit var bookingHistoryAdapter: BookingHistoryAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.history_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")
        observeViewModel()
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        bookingHistoryAdapter = BookingHistoryAdapter()
        rvHistoryBookings.apply {
            adapter = bookingHistoryAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        performBookingsAction(BookingsStateEvent.GetCheckInBookings)
    }

    private fun performBookingsAction(event: BookingsStateEvent) {
        viewModel.setBookingsStateEvent(BookingsStateEvent.GetCheckInBookings)
    }

    private fun observeViewModel() {
        viewModel.checkedInBookingDataState.observe(viewLifecycleOwner, Observer { dataState ->
            when (dataState) {
                is Resource.Success -> {
                    HistoryProgressBar.hide()
                    if (dataState.value.isEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            resources.getString(R.string.empty_checkdIn_list),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }else{
                        dataState.value.let {
                            bookingHistoryAdapter.differ.submitList(it)
                        }
                    }

                }
                is Resource.Failure -> {
                    HistoryProgressBar.hide()
                    displayError()
                }
                is Resource.Loading -> {
                    HistoryProgressBar.show()
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