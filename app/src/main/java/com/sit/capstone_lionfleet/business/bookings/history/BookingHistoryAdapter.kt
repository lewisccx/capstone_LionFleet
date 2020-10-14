package com.sit.capstone_lionfleet.business.bookings.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sit.capstone_lionfleet.R
import com.sit.capstone_lionfleet.business.bookings.network.model.Booking
import com.sit.capstone_lionfleet.utils.DateUtils.Companion.ObjectDateTimeFormatter
import com.sit.capstone_lionfleet.utils.DateUtils.Companion.getFormattedDuration
import com.sit.capstone_lionfleet.utils.FormattingUtils
import kotlinx.android.synthetic.main.view_booking_history_item.view.*
import java.time.temporal.ChronoUnit

class BookingHistoryAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Booking>() {
        override fun areItemsTheSame(oldItem: Booking, newItem: Booking): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Booking, newItem: Booking): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BookingsHistoryListEntriesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.view_booking_history_list_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val bookingHistory = differ.currentList[position]
        holder.itemView.apply {
            txtStationName.text = bookingHistory.stationName
            txtDate.text = bookingHistory.checkedInTs
            val checkedInDateTime = ObjectDateTimeFormatter.parse(bookingHistory.checkedInTs)
            val checkedOutDateTime = ObjectDateTimeFormatter.parse(bookingHistory.checkedOutTs)

            val duation = ChronoUnit.MINUTES.between(
                checkedInDateTime.toInstant(), checkedOutDateTime.toInstant()
            )

            actualDuration.text = getFormattedDuration(duation, holder.itemView.context)
                txtDistance.text =
                FormattingUtils.getFormattedBookingDistance(
                    bookingHistory.distance.toString(),
                    rootView.context
                )
            txtPrice.text = FormattingUtils.getFormattedBookingPrice(
                bookingHistory.actualCost,
                bookingHistory.expectedCost,
                rootView.context
            )

            setOnClickListener {
                onItemClickListener?.let {
                    it(bookingHistory)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class BookingsHistoryListEntriesViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView)

    private var onItemClickListener: ((Booking) -> Unit)? = null
    fun setOnItemClickListener(listener: (Booking) -> Unit) {
        onItemClickListener = listener
    }
}