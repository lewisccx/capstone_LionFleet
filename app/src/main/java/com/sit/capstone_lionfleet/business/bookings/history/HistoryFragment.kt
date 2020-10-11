package com.sit.capstone_lionfleet.business.bookings.history

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.sit.capstone_lionfleet.R
import com.sit.capstone_lionfleet.business.bookings.BookingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    companion object {
        fun newInstance() = HistoryFragment()
    }

    private val viewModel: BookingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.history_fragment, container, false)
    }


}