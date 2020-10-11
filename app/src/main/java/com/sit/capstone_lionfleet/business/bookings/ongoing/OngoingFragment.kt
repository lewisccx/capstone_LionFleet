package com.sit.capstone_lionfleet.business.bookings.ongoing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sit.capstone_lionfleet.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OngoingFragment : Fragment(R.layout.ongoing_fragment) {

    private val viewModel: OngoingViewModel by viewModels();

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.ongoing_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}