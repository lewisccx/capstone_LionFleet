package com.sit.capstone_lionfleet.business.bookings

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sit.capstone_lionfleet.R

class BookingsFragment : Fragment() {

    companion object {
        fun newInstance() = BookingsFragment()
    }

    private lateinit var viewModel: BookingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bookings, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(BookingsViewModel::class.java)
        // TODO: Use the ViewModel
    }
}