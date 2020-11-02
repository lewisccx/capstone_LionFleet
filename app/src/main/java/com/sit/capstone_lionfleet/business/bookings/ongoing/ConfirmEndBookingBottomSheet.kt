package com.sit.capstone_lionfleet.business.bookings.ongoing

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sit.capstone_lionfleet.R
import com.sit.capstone_lionfleet.base.response.Resource
import com.sit.capstone_lionfleet.business.bookings.BookingsViewModel
import com.sit.capstone_lionfleet.business.bookings.network.model.Booking
import com.sit.capstone_lionfleet.business.bookings.network.model.BookingStatus
import com.sit.capstone_lionfleet.business.bookings.network.request.UpdatedCheckedOutBookingRequest
import com.sit.capstone_lionfleet.business.bookings.service.OngoingTripAction
import com.sit.capstone_lionfleet.business.bookings.service.OngoingTripService
import com.sit.capstone_lionfleet.core.di.PreferenceProvider
import com.sit.capstone_lionfleet.core.extension.hide
import com.sit.capstone_lionfleet.core.extension.show
import com.sit.capstone_lionfleet.utils.DateUtils
import com.sit.capstone_lionfleet.utils.FormattingUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.view_confirm_end_now.*
import kotlinx.android.synthetic.main.view_error.*
import kotlinx.android.synthetic.main.view_payable_amount.*
import kotlinx.android.synthetic.main.view_success.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@AndroidEntryPoint
class ConfirmEndBookingBottomSheet : BottomSheetDialogFragment() {
    private val TAG = "ConfirmEndBookingBottomSheet"
    private lateinit var booking: Booking
    private val viewModel: BookingsViewModel by viewModels()
    private var estCost: Int? = null
    private var isVehicleLocked: Boolean? = null

    @Inject
    lateinit var preferenceProvider: PreferenceProvider
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        booking = requireArguments().getParcelable<Booking>("ONGOING_BOOKING_KEY")!!
        return inflater.inflate(R.layout.view_confirm_end_now, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initView()
        initBtns()
        observeViewModel()
        subscribeToObserver()
    }

    private fun subscribeToObserver() {
        OngoingTripService.isVehicleLocked.observe(viewLifecycleOwner, { dataState ->

            isVehicleLocked = dataState

        })
    }

    private fun sendCommandToService(action: String) =
        Intent(requireContext(), OngoingTripService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    private fun observeViewModel() {

        viewModel.checkedInBookingDataState.observe(viewLifecycleOwner, Observer { dataState ->
            when (dataState) {
                is Resource.Success -> {
                    confirm_end_now_loading_view.hide()
                    if (isVehicleLocked!!) {
                        runSimulation()
                    }
                    sendCommandToService(OngoingTripAction.ACTION_TRIP_END.action)
                }
                is Resource.Failure -> {
                    confirm_end_now_loading_view.hide()
                    confirm_end_now_error_view.show()
                }
                is Resource.Loading -> {
                    bookingInfoWindow.hide()
                    confirm_end_now_loading_view.show()
                }
            }
        })
    }

    private fun initView() {
        reservedStartDateAndTimeText.text = reservedDateTimeFormatter(booking.reservedDate)
        startDateAndTimeText.text = booking.checkedInTs
        modelAndPlateText.text = booking.model
        reservedEndDateAndTimeText.text = reservedEndDateTimeFormatter(booking.reservedDate)
        endDateAndTimeText.text = getCurrentDateTime()
        durationText.text = DateUtils.getFormattedDuration(
            getDuration(booking.checkedInTs, getCurrentDateTime()),
            requireContext()
        )
        estCost =
            getAmount(
                getDuration(
                    booking.checkedInTs,
                    getCurrentDateTime()
                ), booking.costsPerDay
            )

        totalAmountPayText.text =
            FormattingUtils.getFormattedEndBookingPrice(estCost!!.toDouble(), requireContext())
    }

    private fun runSimulation() {
        //carKey_loading_view.show()
        confirm_end_now_lock_car_view.show()
        // btnLock.hide()
        requireActivity().runOnUiThread {
            val delayedHandler1 = Handler()
            delayedHandler1.postDelayed({
                confirm_end_now_lock_car_view.hide()
                confirm_end_now_success_view.show()

            }, 2000)

        }
    }

    private fun buildRequest(
        checkedInTs: String,
        status: String,
        estCost: Int,
        actualCost: Int,
        plate: String
    ): UpdatedCheckedOutBookingRequest {
        return UpdatedCheckedOutBookingRequest(
            checkedInTs,
            status,
            estCost,
            actualCost,
            plate
        )
    }

    private fun getAmount(duration: Long, price: Int): Int? {
        if (duration.toInt() > 24) {
            return price + (price * (duration / 60 / 24)).toInt()
        }
        return price
    }

    private fun initBtns() {
        btnConfirmEndTrip.setOnClickListener {
            val amt = getAmount(
                getDuration(
                    booking.checkedInTs,
                    getCurrentDateTime()
                ), booking.costsPerDay
            )
            val currentTime = getCurrentTimeInMongo()
            val request =
                buildRequest(
                    currentTime,
                    BookingStatus.BSTATE_CHECKEDIN.status,
                    estCost!!,
                    amt!!,
                    booking.plate
                )

            viewModel.updateCheckedOutBookingToCheckedIn(booking.id, request)
            //this.dismiss()
        }

        btnOkay.setOnClickListener {
            findNavController().navigate(R.id.navigation_ongoing)
            this.dismiss()
        }

        btnTryAgain.setOnClickListener {
            findNavController().navigate(R.id.navigation_ongoing)
            this.dismiss()
        }
    }

    private fun getDuration(checkedInTs: String, currentDateTime: String): Long {

        val formatter1 = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm")
        val formattedCheckedInDateTime = LocalDateTime.parse(checkedInTs, formatter1)
        val formattedCurrentDateTime = LocalDateTime.parse(currentDateTime, formatter1)

        return ChronoUnit.MINUTES.between(formattedCheckedInDateTime, formattedCurrentDateTime)
    }

    private fun getCurrentTimeInMongo(): String {
        //current datetime
        val currentDateTime = LocalDateTime.now()
        val formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        return formatter1.format(currentDateTime)
    }

    private fun getCurrentDateTime(): String {
        //current datetime
        val currentDateTime = LocalDateTime.now()
        val formatter1 = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm")
        return formatter1.format(currentDateTime)
    }

    override fun getTheme(): Int {
        return R.style.MaterialBottomSheetDialog
    }

    private fun reservedEndDateTimeFormatter(reservedDate: String): String {
        Log.d(TAG, reservedDate)
        //convert reservedDate
        val formatter1 = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm")
        val formattedEndDate = LocalDateTime.parse(reservedDate, formatter1)
        val convertedEndDate = formattedEndDate.plusDays(1)
        //  val formatter2 = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm")
        return convertedEndDate.format(formatter1)
    }

    private fun reservedDateTimeFormatter(reservedDate: String): String {
//        val formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
//        val formattedStartDate = LocalDateTime.parse(reservedDate, formatter1)
        val formatter2 = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm")
        return LocalDateTime.parse(reservedDate, formatter2).toString()
    }
}