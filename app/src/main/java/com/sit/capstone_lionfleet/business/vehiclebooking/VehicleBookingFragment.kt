package com.sit.capstone_lionfleet.business.vehiclebooking

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sit.capstone_lionfleet.R
import com.sit.capstone_lionfleet.base.response.Resource
import com.sit.capstone_lionfleet.business.map.network.model.Vehicle
import com.sit.capstone_lionfleet.core.di.ImageService
import com.sit.capstone_lionfleet.core.extension.hide
import com.sit.capstone_lionfleet.core.extension.show
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.vehicle_booking_fragment.*
import kotlinx.android.synthetic.main.view_cancel_success.*
import kotlinx.android.synthetic.main.view_error.*
import kotlinx.android.synthetic.main.view_select_booking_date.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class VehicleBookingFragment : Fragment(R.layout.vehicle_booking_fragment),
    DatePickerDialog.OnDateSetListener {
    val TAG = "VehicleBookingFragment"
    private val viewModel: VehicleBookingViewModel by viewModels()
    private val args: VehicleBookingFragmentArgs by navArgs()
    private val vehicleSchedule = mutableSetOf<LocalDate>()

    private var datePickerDialog: DatePickerDialog? = null

    @Inject
    lateinit var imageService: ImageService
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.vehicle_booking_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val selectedVehicleArgs = args.SelectedVehicle

        observeViewModel()
        initVehicleCard(view, selectedVehicleArgs)
        initLoadVehicleSchedule()
        initEditTexts()
        initBtns()
    }

    private fun initEditTexts() {
        timeFrameTextView.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    viewModel.onSelectedDateChanged(p0.toString())
                }
            }
        )
    }

    private fun initBtns() {

        btnCalendarPicker.setOnClickListener {
            initCalendarPicker()
        }
        btnBookNow.setOnClickListener {
            viewModel.setVehicleBookingEvent(VehicleBookingStateEvent.NewVehicleBooking, args.SelectedVehicle!!.id)
            btnBookNow.hide()
        }
    }

    private fun initCalendarPicker() {

        val now = Calendar.getInstance()
        val maxDate = Calendar.getInstance()
        var year = maxDate.get(Calendar.YEAR)
        var month = maxDate.get(Calendar.MONTH)
        var day = maxDate.get(Calendar.DAY_OF_MONTH)
        maxDate[Calendar.YEAR] = year
        maxDate[Calendar.MONTH] = month + 2
        maxDate[Calendar.DAY_OF_MONTH] = day

        if (datePickerDialog == null) {
            datePickerDialog = DatePickerDialog.newInstance(
                this@VehicleBookingFragment,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
            )
        } else {
            datePickerDialog!!.initialize(
                this@VehicleBookingFragment,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
            )
        }
        val unavailableDate = mutableListOf<Calendar>()
        if (vehicleSchedule.isNotEmpty()) {
            for (date in vehicleSchedule) {
                val gc = Calendar.Builder()
                    .setDate(date.year, date.monthValue - 1, date.dayOfMonth)
                    .build() as GregorianCalendar
                unavailableDate.add(gc)
            }
            datePickerDialog!!.disabledDays = unavailableDate.toTypedArray()
        }
        datePickerDialog!!.minDate = now
        datePickerDialog!!.maxDate = maxDate
        datePickerDialog!!.isThemeDark = false
        datePickerDialog!!.vibrate(true)
        datePickerDialog!!.dismissOnPause(true)
        datePickerDialog!!.showYearPickerFirst(false)
        datePickerDialog!!.setOnCancelListener {
            it.cancel()
        }
        datePickerDialog!!.show(childFragmentManager, "datepickerdialog")
    }

    private fun observeViewModel() {
        viewModel.newVehicleBookingDataState.observe(
            viewLifecycleOwner,
            Observer { newVehicleBookingDataState ->

                when (newVehicleBookingDataState) {
                    is Resource.Success -> {
                        vehicleBookingProgressBar.hide()
                        vehicleBookingView.hide()
                        vehicleBookingSuccess.show()

                        Toast.makeText(requireContext(), newVehicleBookingDataState.value.booking.toString(), Toast.LENGTH_LONG).show()
                        btnOkay.setOnClickListener {
                            findNavController().navigate(R.id.navigation_bookings)
                        }
                    }
                    is Resource.Failure -> {
                        vehicleBookingProgressBar.hide()
                        vehicleBookingView.hide()
                        vehicleBookingFail.show()
                        btnTryAgain.setOnClickListener {
                            findNavController().navigate(R.id.navigation_map)
                        }
                    }
                    is Resource.Loading -> {
                        vehicleBookingProgressBar.show()
                        vehicleBookingView.hide()
                    }
                }
            }

        )

        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            MainScope().launch { btnBookNow.isEnabled = it.BookButtonEnabled }
        })
        viewModel.vehicleScheduleDataState.observe(
            viewLifecycleOwner,
            Observer { vehicleScheduleDataState ->
                when (vehicleScheduleDataState) {
                    is Resource.Success -> {
                        for (date in vehicleScheduleDataState.value.schedule) {
                            vehicleSchedule.add(date)
                        }
                        Toast.makeText(
                            requireContext(),
                            vehicleScheduleDataState.value.schedule.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                        vehicleBookingProgressBar.hide()
                    }
                    is Resource.Loading -> {
                        vehicleBookingProgressBar.show()
                    }
                    is Resource.Failure -> {
                        Toast.makeText(
                            requireContext(),
                            R.string.something_went_wrong_error,
                            Toast.LENGTH_SHORT
                        ).show()
                        vehicleBookingProgressBar.hide()
                    }

                }
            })
    }

    private fun initLoadVehicleSchedule() {
        viewModel.setVehicleBookingEvent(VehicleBookingStateEvent.LoadVehicleSchedule, args.SelectedVehicle!!.id)
    }

    private fun initVehicleCard(view: View, selectedVehicleArgs: Vehicle?) {
        val vehicleImage = view.findViewById<ImageView>(R.id.vehicleImage)
        val vehicleModelBrand = view.findViewById<TextView>(R.id.vehicleModelBrand)
        val txtEngineType = view.findViewById<TextView>(R.id.textEngineType)
        val imgEngineType = view.findViewById<ImageView>(R.id.iconEngineType)
        val txtLicense = view.findViewById<TextView>(R.id.vehicleLicense)
        val txtModelBrand = view.findViewById<TextView>(R.id.vehicleModelBrand)
        val txtPricePerKm = view.findViewById<TextView>(R.id.textPricePerKm)
        val txtPricePerDay = view.findViewById<TextView>(R.id.textPricePerDay)
        val isAvailableFlag = view.findViewById<ImageView>(R.id.isAvailableFlag)
        if (selectedVehicleArgs!!.isDiesel) {
            txtEngineType.text = view.context.resources.getString(R.string.engineDisel)
            imgEngineType.setImageDrawable(view.context.getDrawable(R.drawable.ic_gasoline))
        }
        if (!selectedVehicleArgs.availability) {
            isAvailableFlag.setImageDrawable(view.context.getDrawable(R.drawable.ic_wrong))
        }
        imageService.loadImage(selectedVehicleArgs.imageUrl, vehicleImage)
        vehicleModelBrand.text = "${selectedVehicleArgs.brand} ${selectedVehicleArgs.model}"
        txtLicense.text = selectedVehicleArgs.plate
        txtModelBrand.text = "${selectedVehicleArgs?.brand} ${selectedVehicleArgs?.model}"
        txtPricePerKm.text = "${selectedVehicleArgs?.costsPerHour} $/km"
        txtPricePerDay.text = "${selectedVehicleArgs?.costsPerDay} $/day"
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val selectedDate =
            "$year-$monthOfYear-$dayOfMonth"
        val selectDateInfo = "You picked the following date:$dayOfMonth-$monthOfYear-$year "
        timeFrameTextView.show()
        timeFrameTextView.text = selectedDate
    }
}