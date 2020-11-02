package com.sit.capstone_lionfleet.business.bookings.ongoing

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sit.capstone_lionfleet.R
import com.sit.capstone_lionfleet.business.bookings.service.OngoingTripAction
import com.sit.capstone_lionfleet.business.bookings.service.OngoingTripService
import com.sit.capstone_lionfleet.core.di.PreferenceProvider
import com.sit.capstone_lionfleet.core.extension.hide
import com.sit.capstone_lionfleet.core.extension.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.view_car_key.*
import javax.inject.Inject

@AndroidEntryPoint
class CarKeyFragment : Fragment() {

    private var isVehicleLocked = false

    @Inject
    lateinit var preferenceProvider: PreferenceProvider
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.view_car_key, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBtns()
        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        OngoingTripService.isVehicleLocked.observe(viewLifecycleOwner, { value ->
            when (value) {
                true -> {
                    btnLock.setImageDrawable(requireContext().getDrawable(R.drawable.ic_unlock_icon))
                    lockTitle.text = "Unlock"
                    btnUnlock.setImageDrawable(requireContext().getDrawable(R.drawable.ic_lock_icon))
                    unlockTitle.text = "lock"
                    txtKeyMessage.text = "Car is Locked"
                }
                false -> {
                    btnLock.setImageDrawable(requireContext().getDrawable(R.drawable.ic_lock_icon))
                    lockTitle.text = "Lock"
                    btnUnlock.setImageDrawable(requireContext().getDrawable(R.drawable.ic_unlock_icon))
                    unlockTitle.text = "Unlock"
                    txtKeyMessage.text = "Car is Unlocked"
                }
            }

        })
    }

    private fun initBtns() {
        btnLock.setOnClickListener {
            runSimulation()
            if (!isVehicleLocked) {
//                btnLock.setImageDrawable(requireContext().getDrawable(R.drawable.ic_lock_icon))
//                lockTitle.text = "Lock"
//                btnUnlock.setImageDrawable(requireContext().getDrawable(R.drawable.ic_unlock_icon))
//                unlockTitle.text = "Unlock"
//                txtKeyMessage.text = "Car is Unlocked"
                //unlock= true
                //preferenceProvider.saveCarStatus(unlock!!)
                sendCommandToService(OngoingTripAction.ACTION_CAR_LOCKED.action)
            } else {

//                btnLock.setImageDrawable(requireContext().getDrawable(R.drawable.ic_unlock_icon))
//                lockTitle.text = "Unlock"
//                btnUnlock.setImageDrawable(requireContext().getDrawable(R.drawable.ic_lock_icon))
//                unlockTitle.text = "lock"
//                txtKeyMessage.text = "Car is Locked"
                //unlock= false
                //sendCommandToService(OngoingTripAction.ACTION_CAR_LOCKED.action)
                // preferenceProvider.saveCarStatus(unlock!!)
                sendCommandToService(OngoingTripAction.ACTION_CAR_UNLOCKED.action)
            }

        }
    }

    private fun sendCommandToService(action: String) =
        Intent(requireContext(), OngoingTripService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    private fun runSimulation() {
        carKey_loading_view.show()
        btnLock.hide()
        requireActivity().runOnUiThread {
            val delayedHandler1 = Handler()
            delayedHandler1.postDelayed({
                carKey_loading_view.hide()
                btnLock.show()

            }, 2000)

        }
    }
}