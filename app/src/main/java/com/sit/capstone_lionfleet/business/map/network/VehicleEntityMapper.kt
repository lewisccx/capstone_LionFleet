package com.sit.capstone_lionfleet.business.map.network

import com.sit.capstone_lionfleet.business.map.network.model.Vehicle
import com.sit.capstone_lionfleet.business.map.network.response.VehicleItem
import com.sit.capstone_lionfleet.utils.EntityMapper
import javax.inject.Inject

class VehicleEntityMapper
@Inject
constructor() : EntityMapper<VehicleItem, Vehicle> {
    override fun mapFromEntity(entity: VehicleItem): Vehicle {
        return Vehicle(
            id = entity.id,
            plate = entity.plate,
            brand = entity.brand,
            model = entity.model,
            imageUrl = entity.imageUrl,
            availability = entity.availability,
            isDiesel = entity.isDiesel,
            costsPerDay = entity.pricing.costsPerDay,
            costsPerHour = entity.pricing.costsPerHour,
            costsPerKm = entity.pricing.costsPerKm,
            stationId = entity.station.id,
            stationName = entity.station.name

        )
    }
    fun mapFromEntityResponse(entities : List<VehicleItem>): List<Vehicle>{
        return entities.map {
            mapFromEntity(it)
        }
    }
}