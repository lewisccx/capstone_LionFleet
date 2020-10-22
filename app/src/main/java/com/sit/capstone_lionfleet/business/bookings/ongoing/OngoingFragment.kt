package com.sit.capstone_lionfleet.business.bookings.ongoing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.alan.alansdk.button.AlanButton
import com.sit.capstone_lionfleet.R
import com.sit.capstone_lionfleet.business.bookings.BookingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import org.json.JSONObject

@AndroidEntryPoint
class OngoingFragment : Fragment(R.layout.ongoing_fragment) {

    private val viewModel: BookingsViewModel by viewModels();

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.ongoing_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setVisualState()
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
}