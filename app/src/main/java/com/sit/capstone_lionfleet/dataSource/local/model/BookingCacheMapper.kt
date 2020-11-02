package com.sit.capstone_lionfleet.dataSource.local.model

import com.sit.capstone_lionfleet.business.bookings.network.model.Booking
import com.sit.capstone_lionfleet.utils.DateUtils.Companion.ObjectDateTimeFormatter
import com.sit.capstone_lionfleet.utils.DateUtils.Companion.mongoDateTimeFormatter
import com.sit.capstone_lionfleet.utils.EntityMapper
import javax.inject.Inject

class BookingCacheMapper
@Inject
constructor() : EntityMapper<BookingCacheEntity, Booking> {
    override fun mapFromEntity(entity: BookingCacheEntity): Booking {
        return Booking(
            id = entity.id,
            checkedInTs = ObjectDateTimeFormatter.format(entity.checkedInTs),
            checkedOutTs = ObjectDateTimeFormatter.format(entity.checkedOutTs),
            distance = entity.distance,
            reservedDate = ObjectDateTimeFormatter.format(entity.reservedDate),
            status = entity.status,
            brand = entity.brand,
            model = entity.model,
            plate = entity.plate,
            availability = entity.availability,
            imageUrl = entity.imageUrl,
            stationName = entity.stationName,
            createdAt = ObjectDateTimeFormatter.format(entity.createdAt),
            updatedAt = ObjectDateTimeFormatter.format(entity.updatedAt),
            actualCost = entity.actualCost,
            expectedCost = entity.expectedCost,
            costsPerHour = entity.costsPerHour,
            costsPerDay = entity.costsPerDay,
            costsPerKm = entity.costsPerKm
        )
    }

    fun mapToEntity(domainModel: Booking): BookingCacheEntity {
        return BookingCacheEntity(
            id = domainModel.id,
            checkedInTs = mongoDateTimeFormatter.parse(domainModel.checkedInTs),
            checkedOutTs = mongoDateTimeFormatter.parse(domainModel.checkedOutTs),
            distance = domainModel.distance,
            reservedDate = mongoDateTimeFormatter.parse(domainModel.reservedDate),
            status = domainModel.status,
            brand = domainModel.brand,
            model = domainModel.model,
            plate = domainModel.plate,
            availability = domainModel.availability,
            imageUrl = domainModel.imageUrl,
            stationName = domainModel.stationName,
            createdAt = mongoDateTimeFormatter.parse(domainModel.createdAt),
            updatedAt = mongoDateTimeFormatter.parse(domainModel.updatedAt),
            actualCost = domainModel.actualCost,
            expectedCost = domainModel.expectedCost,
            costsPerKm = domainModel.costsPerKm,
            costsPerDay = domainModel.costsPerDay,
            costsPerHour = domainModel.costsPerHour
        )
    }

    fun mapFromEntityList(entities: List<BookingCacheEntity>): List<Booking> {

        return entities.map {
            mapFromEntity(it)
        }
    }
}