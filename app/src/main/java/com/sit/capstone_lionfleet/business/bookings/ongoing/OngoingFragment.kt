package com.sit.capstone_lionfleet.business.bookings.ongoing

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.alan.alansdk.button.AlanButton
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sit.capstone_lionfleet.R
import com.sit.capstone_lionfleet.base.response.Resource
import com.sit.capstone_lionfleet.business.bookings.BookingsStateEvent
import com.sit.capstone_lionfleet.business.bookings.BookingsViewModel
import com.sit.capstone_lionfleet.business.bookings.network.model.Booking
import com.sit.capstone_lionfleet.business.bookings.network.model.BookingStatus
import com.sit.capstone_lionfleet.business.bookings.service.OngoingTripAction
import com.sit.capstone_lionfleet.business.bookings.service.OngoingTripService
import com.sit.capstone_lionfleet.core.di.ImageService
import com.sit.capstone_lionfleet.core.extension.hide
import com.sit.capstone_lionfleet.core.extension.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.ongoing_fragment.*
import kotlinx.android.synthetic.main.ongoing_fragment.startDateAndTimeText
import kotlinx.android.synthetic.main.view_booking_trip_edit_button.*
import kotlinx.android.synthetic.main.view_booking_trip_endnow.*
import kotlinx.android.synthetic.main.view_car_trip_started.*
import kotlinx.android.synthetic.main.view_confirm_end_now.*
import kotlinx.android.synthetic.main.view_error.*
import kotlinx.android.synthetic.main.view_error.btnTryAgain
import kotlinx.android.synthetic.main.view_general_error.*

import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class OngoingFragment : Fragment(R.layout.ongoing_fragment) {

    val TAG = "OngoingFragment"
    private val viewModel: BookingsViewModel by viewModels();
    private var ongoingBookingId: String? = "0"
    private var ongoingBookingVehicleId: String? = "0"
    private var reservedDateTime: String? = null
    private lateinit var ongoingBooking: Booking
    private var confirmEndBookingBottomSheet: ConfirmEndBookingBottomSheet? = null
    @Inject
    lateinit var imageService: ImageService
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.ongoing_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setVisualState()
        observeViewModel(view)
        performBookingsAction(BookingsStateEvent.GetOngoingBooking)
        initBtns(view)

    }

    private fun initBtns(view: View) {
        btnKey.setOnClickListener {
            findNavController().navigate(R.id.carKeyFragment)
        }
        btnStartNow.setOnClickListener {
            if (validateCurrentDateTime(reservedDateTime!!)) {
                if (ongoingBookingId != "0") {
                    Log.d(TAG, "btnStartNow clicked")
                    performBookingsAction(BookingsStateEvent.UpdateStartableBookingToCheckedOut)
                } else {
                    Toast.makeText(
                        requireContext(),
                        R.string.ongoing_booking_id_not_found,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    R.string.ongoing_booking_past_reserved_datetime,
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        btnEndTrip.setOnClickListener {

            showConfirmEndNowBottomSheetDialogFragment()
        }


        btnOkay.setOnClickListener {
            car_trip_started_view.hide()
            performBookingsAction(BookingsStateEvent.GetOngoingBooking)
        }

        btnTryAgain.setOnClickListener {
            findNavController().navigate(R.id.navigation_ongoing)
        }
    }

    private fun showConfirmEndNowBottomSheetDialogFragment() {
        Log.d(TAG, "showConfirmEndNowBottomSheetDialogFragment")
        Log.d(TAG, ongoingBooking.toString())
        val bundle = Bundle()
        bundle.putParcelable("ONGOING_BOOKING_KEY", ongoingBooking)
        confirmEndBookingBottomSheet = ConfirmEndBookingBottomSheet()
        confirmEndBookingBottomSheet!!.arguments = bundle
        confirmEndBookingBottomSheet!!.show(
            childFragmentManager,
            "confirmEndBookingBottomSheet",
        )

    }

    private fun performBookingsAction(event: BookingsStateEvent) {
        viewModel.setBookingsStateEvent(event, ongoingBookingId!!, ongoingBookingVehicleId!!)
    }

    private fun observeViewModel(view: View) {

        viewModel.checkedOutBookingDataState.observe(viewLifecycleOwner, Observer { dataState ->
            when (dataState) {
                is Resource.Success -> {

                    Log.d(TAG, "checkedOutBookingDataState")
                    ongoing_loading_view.hide()
                    runSimulation()
                    startDateAndTimeText.text =
                        reservedDateTimeFormatter(dataState.value.checkedInTs)
                    sendCommandToService(OngoingTripAction.ACTION_TRIP_STARTED.action)
                }
                is Resource.Failure -> {
                    ongoing_loading_view.hide()
                    Toast.makeText(requireContext(), dataState.errorBody, Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    ongoing_loading_view.show()
                }
            }

        })

        viewModel.ongoingBookingDataState.observe(viewLifecycleOwner, Observer { dataState ->
            when (dataState) {
                is Resource.Success -> {
                    ongoing_loading_view.hide()
                    if(dataState.value.isEmpty()){
                        no_ongoing_booking_view.show()
                    }else{
                        Log.d(TAG, "ongoingBookingDataState")

                        ongoingBookingId = dataState.value[0].id
                        ongoingBookingVehicleId = dataState.value[0].plate
                        reservedDateTime = dataState.value[0].reservedDate
                        ongoingBooking = buildConfirmEndNowRequest(dataState.value[0])
                        updateUI(view, dataState.value[0])
                    }

                }
                is Resource.Failure -> {
                    ongoing_error_view.show()
                }
                is Resource.Loading -> {
                    ongoing_loading_view.show()
                }
            }
        })
    }

    private fun sendCommandToService(action: String) =
        Intent(requireContext(), OngoingTripService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    private fun buildConfirmEndNowRequest(booking: Booking) : Booking {
       return  Booking(
           id = booking.id,
           checkedInTs = reservedDateTimeFormatter(booking.checkedInTs),
           checkedOutTs = booking.checkedOutTs,
           createdAt = booking.createdAt,
           updatedAt = booking.updatedAt,
           distance = booking.distance,
           reservedDate = reservedDateTimeFormatter(booking.reservedDate),
           model = booking.model,
           brand = booking.brand,
           imageUrl = booking.imageUrl,
           plate = booking.plate,
           stationName = booking.stationName,
           actualCost = booking.actualCost,
           expectedCost = booking.expectedCost,
           availability = booking.availability,
           costsPerKm = booking.costsPerKm,
           costsPerDay = booking.costsPerDay,
           costsPerHour = booking.costsPerHour,
           status = booking.status
       )
    }

    private fun hideBlueToothView() {
        connecting_car_view.hide()
        opening_car_view.show()
    }

    private fun runSimulation() {
        connecting_car_view.show()
        requireActivity().runOnUiThread {
            val delayedHandler1 = Handler()
            delayedHandler1.postDelayed({
                hideBlueToothView()

            }, 2000)

            val delayedHandler2 = Handler()
            delayedHandler2.postDelayed({
                opening_car_view.hide()
                car_trip_started_view.show()
            }, 4000)
        }
    }

    private fun updateUI(view: View, booking: Booking) {
        val imgVehicle = view.findViewById<ImageView>(R.id.vehicleImage)
        val vehicleLicense = view.findViewById<TextView>(R.id.vehicleLicense)
        val vehicleModel = view.findViewById<TextView>(R.id.vehicleModelBrand)
        val stationName = view.findViewById<TextView>(R.id.stationName)
        val reservedStartDateAndTime =
            view.findViewById<TextView>(R.id.reservedStartDateAndTimeText)
        val checkedInDateTime = view.findViewById<TextView>(R.id.startDateAndTimeText)
        val reservedEndDateAndTime = view.findViewById<TextView>(R.id.reservedEndDateAndTimeText)
        val modelAndPlateNum = view.findViewById<TextView>(R.id.modelAndPlateText)
        //booking card
        reservedStartDateAndTime.text = reservedDateTimeFormatter(booking.reservedDate)
        reservedEndDateAndTime.text = reservedEndDateTimeFormatter(booking.reservedDate)
        modelAndPlateNum.text = "${booking.model} ${booking.plate}"


        //vehicle information
        vehicleLicense.text = booking.plate
        vehicleModel.text = booking.model
        stationName.text = booking.stationName
        Glide.with(this).load(booking.imageUrl).into(imgVehicle)
        Log.d(TAG, booking.status)
        if (booking.status == BookingStatus.BSTATE_STARTABLE.status) {

            ongoingEndNowActionView.hide()
        } else {
            checkedInDateTime.text = reservedDateTimeFormatter(booking.checkedInTs)
            ongoingStartableActionView.hide()
            ongoingEndNowActionView.show()
        }
    }

    private fun setVisualState() {
        var visualState: JSONObject? = null
        try {
            visualState = JSONObject("{\"fragment\":\"ongoing\"}")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        requireActivity().findViewById<AlanButton>(R.id.alan_button)
            .setVisualState(visualState.toString())
    }

    private fun reservedEndDateTimeFormatter(reservedDate: String): String {
        Log.d(TAG, reservedDate)
        //convert reservedDate
        val formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val formattedEndDate = LocalDateTime.parse(reservedDate, formatter1)
        val convertedEndDate = formattedEndDate.plusDays(1)
        val formatter2 = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm")
        return convertedEndDate.format(formatter2)
    }

    private fun reservedDateTimeFormatter(reservedDate: String): String {
        val formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val formattedStartDate = LocalDateTime.parse(reservedDate, formatter1)
        val formatter2 = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm")
        return formattedStartDate.format(formatter2)
    }

    private fun validateCurrentDateTime(dateTime: String): Boolean {
        Log.d(TAG, dateTime)
        //convert reservedDate
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val formattedReservedDate = LocalDate.parse(dateTime, formatter)
        val reservedDateTime: ZonedDateTime =
            formattedReservedDate.atStartOfDay(ZoneId.systemDefault())

        //current datetime
        val currentDateTime = Calendar.getInstance().time.toInstant().atZone(ZoneId.systemDefault())
        // get duration
        val duration = ChronoUnit.MINUTES.between(currentDateTime, reservedDateTime)

        Log.d(TAG, duration.toString())
        Log.d(TAG, "now: $currentDateTime")
        Log.d(TAG, "reservedDateTime: $reservedDateTime")

        return duration in -1400..15
    }

    private fun validateCheckedDateTime(dateTime: String, reservedDateTime: String): Boolean {
        Log.d(TAG, dateTime)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        //convert reservedDateTime

        val formattedReservedDate = LocalDate.parse(reservedDateTime, formatter)
        val reservedDateTime: ZonedDateTime =
            formattedReservedDate.atStartOfDay(ZoneId.systemDefault())

        //convert checkedInDateTime
        val formattedCheckInDate = LocalDate.parse(dateTime, formatter)
        val checkedInDateTime: ZonedDateTime =
            formattedCheckInDate.atStartOfDay(ZoneId.systemDefault())
        // get duration
        val duration = ChronoUnit.MINUTES.between(checkedInDateTime, reservedDateTime)

        Log.d(TAG, duration.toString())
        Log.d(TAG, "now: $checkedInDateTime")
        Log.d(TAG, "reservedDateTime: $reservedDateTime")

        return duration in -1400..15
    }



}