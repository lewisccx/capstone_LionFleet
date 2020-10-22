package com.sit.capstone_lionfleet.business.bookings.scheduled

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
import com.google.android.material.button.MaterialButton
import com.sit.capstone_lionfleet.R
import com.sit.capstone_lionfleet.base.response.Resource
import com.sit.capstone_lionfleet.business.bookings.BookingsStateEvent
import com.sit.capstone_lionfleet.business.bookings.BookingsViewModel
import com.sit.capstone_lionfleet.business.bookings.network.model.Booking
import com.sit.capstone_lionfleet.core.extension.hide
import com.sit.capstone_lionfleet.core.extension.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.scheduled_fragment.*
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

@AndroidEntryPoint
class ScheduledFragment : Fragment(R.layout.scheduled_fragment), ItemClickListener {
    val TAG = "ScheduledFragment"
    private val viewModel: BookingsViewModel by viewModels()
    private lateinit var bookingScheduledAdapter: BookingScheduledAdapter
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
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        bookingScheduledAdapter = BookingScheduledAdapter(itemClickListener = this)
        rvBookings.apply {
            adapter = bookingScheduledAdapter
            layoutManager = LinearLayoutManager(activity)
        }
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
                    if (dataState.value.isEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            resources.getString(R.string.empty_reservedBooking_list),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        dataState.value.let {
                            bookingScheduledAdapter.differ.submitList(it)
                        }
                    }
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

    private fun performUpdateBookingToStartableAction(event: BookingsStateEvent) {
        viewModel.setBookingsStateEvent(event)
    }

    override fun onItemClicked(button: MaterialButton, booking: Booking) {
        when (button.id) {

            R.id.btnStartNow -> {
                view
                Toast.makeText(
                    requireContext(),
                    booking.reservedDate,
                    Toast.LENGTH_SHORT
                ).show()
                val timeValidated = validateCurrentDateTime(booking.reservedDate)
                if (timeValidated) {
                    performUpdateBookingToStartableAction(BookingsStateEvent.UpdateReservedBookingToStartable)
                } else {
                    Toast.makeText(
                        requireContext(),
                        R.string.reserved_to_startable_too_early_error,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            R.id.btnCancel -> {
                Toast.makeText(
                    requireContext(),
                    booking.id,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun validateCurrentDateTime(reservedDate: String): Boolean {
        Log.d(TAG, reservedDate)
        //convert reservedDate
        val formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm")
        val formattedReservedDate = LocalDate.parse(reservedDate, formatter)
        val reservedDateTime: ZonedDateTime =
            formattedReservedDate.atStartOfDay(ZoneId.systemDefault())

        //current datetime
        val currentDateTime = Calendar.getInstance().time.toInstant().atZone(ZoneId.systemDefault())
        // get duration
        val duration = ChronoUnit.MINUTES.between(currentDateTime, reservedDateTime)

        Log.d(TAG, duration.toString())
        Log.d(TAG, " now: $currentDateTime")
        Log.d(TAG, "reservedDateTime: $reservedDateTime")

        return duration < 15
    }
}