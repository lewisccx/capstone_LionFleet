package com.sit.capstone_lionfleet.business.bookings.scheduled

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sit.capstone_lionfleet.R
import com.sit.capstone_lionfleet.business.bookings.network.model.Booking
import kotlinx.android.synthetic.main.view_booking_history_item.view.*
import kotlinx.android.synthetic.main.view_booking_overview.view.*
import kotlinx.android.synthetic.main.view_booking_vehicle_info.view.*

class BookingScheduledAdapter(private val itemClickListener: ItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Booking>() {
        override fun areItemsTheSame(oldItem: Booking, newItem: Booking): Boolean {
            return oldItem.id == newItem.id && oldItem.status == newItem.status
        }

        override fun areContentsTheSame(oldItem: Booking, newItem: Booking): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BookingScheduledListEntriesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.view_startable_bookings_list_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val bookingScheduled = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(bookingScheduled.imageUrl).into(imgVehicle)
            txtStartDate.text = bookingScheduled.reservedDate
            txtEndDate.text = bookingScheduled.reservedDate
            txtLicensePlate.text = bookingScheduled.plate
            txtLocation.text = bookingScheduled.stationName
            btnStartNow.setOnClickListener {
                itemClickListener.onItemClicked(it.btnStartNow, bookingScheduled)
            }

            btnCancel.setOnClickListener {
                itemClickListener.onItemClicked(it.btnCancel, bookingScheduled)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class BookingScheduledListEntriesViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView)
}