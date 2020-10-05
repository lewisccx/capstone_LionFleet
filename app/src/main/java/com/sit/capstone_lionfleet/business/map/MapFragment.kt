package com.sit.capstone_lionfleet.business.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.sit.capstone_lionfleet.R
import com.sit.capstone_lionfleet.business.bookings.BookingsFragment
import com.sit.capstone_lionfleet.business.bookings.BookingsViewModel
import com.sit.capstone_lionfleet.databinding.FragmentMapBinding

class MapFragment : Fragment(){

    companion object {
        fun newInstance() = MapFragment()
    }

    private lateinit var viewModel: MapViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)
        // TODO: Use the ViewModel
    }
}