package com.sit.capstone_lionfleet.business.bookings.network

import com.sit.capstone_lionfleet.business.bookings.network.model.Booking
import com.sit.capstone_lionfleet.business.bookings.network.response.BookingItem
import com.sit.capstone_lionfleet.business.bookings.network.response.Price
import com.sit.capstone_lionfleet.business.bookings.network.response.Station
import com.sit.capstone_lionfleet.business.bookings.network.response.Vehicle
import com.sit.capstone_lionfleet.utils.EntityMapper
import javax.inject.Inject

class BookingEntityMapper
@Inject
constructor() : EntityMapper<BookingItem, Booking> {

    override fun mapFromEntity(entity: BookingItem): Booking {
        return Booking(
            id = entity.id,
            checkedInTs = entity.checkedInTs,
            checkedOutTs = entity.checkedOutTs,
            distance = entity.distance,
            reservedDate = entity.reservedDate,
            status = entity.status,
            brand = entity.vehicle.brand,
            model = entity.vehicle.model,
            plate = entity.vehicle.plate,
            availability = entity.vehicle.availability,
            imageUrl = entity.vehicle.imageUrl,
            stationName = entity.station.name,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            actualCost = entity.actualCost,
            expectedCost = entity.expectedCost,
            costsPerDay = entity.vehicle.price.costsPerDay,
            costsPerKm = entity.vehicle.price.costsPerKm,
            costsPerHour = entity.vehicle.price.costsPerHour
        )
    }

    fun mapToEntity(domainModel: Booking): BookingItem {
        return BookingItem(
            id = domainModel.id,
            checkedInTs = domainModel.checkedInTs,
            checkedOutTs = domainModel.checkedOutTs,
            distance = domainModel.distance,
            reservedDate = domainModel.reservedDate,
            status = domainModel.status,
            vehicle = Vehicle(
                domainModel.plate,
                domainModel.model,
                domainModel.brand,
                domainModel.imageUrl,
                domainModel.availability,
                price = Price(
                    domainModel.costsPerDay,
                    domainModel.costsPerKm,
                    domainModel.costsPerHour
                )
            ),
            station = Station(domainModel.stationName),
            createdAt = domainModel.createdAt,
            updatedAt = domainModel.updatedAt,
            actualCost = domainModel.actualCost,
            expectedCost = domainModel.expectedCost,

            )
    }

    fun mapFromEntityResponse(entities: List<BookingItem>): List<Booking> {
        return entities.map {
            mapFromEntity(it)
        }
    }
}