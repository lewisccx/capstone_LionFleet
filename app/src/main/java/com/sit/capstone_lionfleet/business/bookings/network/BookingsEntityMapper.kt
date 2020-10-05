package com.sit.capstone_lionfleet.business.bookings.network

import com.sit.capstone_lionfleet.business.bookings.network.model.Booking
import com.sit.capstone_lionfleet.business.bookings.network.response.BookingResponse
import com.sit.capstone_lionfleet.utils.EntityMapper
import javax.inject.Inject

class BookingsEntityMapper
@Inject
constructor(): EntityMapper<BookingResponse, Booking>{
    override fun mapFromEntity(entity: BookingResponse): Booking {
        return Booking(
            id = entity.id,
            status = entity.status
        )
    }
    fun mapFromListResponse(entities: List<BookingResponse>):List<Booking>{
        return entities.map {
            mapFromEntity(it)
        }
    }
}