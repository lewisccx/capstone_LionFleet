package com.sit.capstone_lionfleet.dataSource.local.model

import com.sit.capstone_lionfleet.business.bookings.network.model.Booking
import com.sit.capstone_lionfleet.utils.EntityMapper
import javax.inject.Inject

class BookingCacheMapper
@Inject
constructor() : EntityMapper<BookingCacheEntity, Booking> {
    override fun mapFromEntity(entity: BookingCacheEntity): Booking {
        return Booking(
            id = entity.id,
            checkedInTs = entity.checkedInTs,
            checkedOutTs = entity.checkedOutTs,
            distance = entity.distance,
            reservedDate = entity.reservedDate,
            status = entity.status,
            brand = entity.brand,
            model = entity.model,
            plate = entity.plate,
            availability = entity.availability,
            imageUrl = entity.imageUrl,
            stationName = entity.stationName,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun mapToEntity(domainModel: Booking): BookingCacheEntity {
        return BookingCacheEntity(
            id = domainModel.id,
            checkedInTs = domainModel.checkedInTs,
            checkedOutTs = domainModel.checkedOutTs,
            distance = domainModel.distance,
            reservedDate = domainModel.reservedDate,
            status = domainModel.status,
            brand = domainModel.brand,
            model = domainModel.model,
            plate = domainModel.plate,
            availability = domainModel.availability,
            imageUrl = domainModel.imageUrl,
            stationName = domainModel.stationName,
            createdAt = domainModel.createdAt,
            updatedAt = domainModel.updatedAt
        )
    }

    fun mapFromEntityList(entities: List<BookingCacheEntity>): List<Booking> {

        return entities.map {
            mapFromEntity(it)
        }
    }
}