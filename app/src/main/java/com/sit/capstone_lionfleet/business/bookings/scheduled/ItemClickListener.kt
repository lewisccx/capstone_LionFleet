package com.sit.capstone_lionfleet.business.bookings.scheduled

import com.google.android.material.button.MaterialButton
import com.sit.capstone_lionfleet.business.bookings.network.model.Booking

interface ItemClickListener {

   fun onItemClicked(button: MaterialButton, booking: Booking)
}