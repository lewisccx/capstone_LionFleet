package com.sit.capstone_lionfleet.dataSource.local.model

import com.sit.capstone_lionfleet.business.map.network.model.Vehicle
import com.sit.capstone_lionfleet.utils.EntityMapper
import javax.inject.Inject

class VehicleCacheMapper
@Inject
constructor(): EntityMapper<VehicleCacheEntity, Vehicle>{
    override fun mapFromEntity(entity: VehicleCacheEntity): Vehicle {
        return Vehicle(
            id = entity.id,
            plate = entity.plate,
            brand = entity.brand,
            model = entity.model,
            imageUrl = entity.imageUrl,
            availability = entity.availability,
            isDiesel = entity.isDiesel,
            costsPerDay = entity.costsPerDay,
            costsPerHour = entity.costsPerHour,
            costsPerKm = entity.costsPerKm,
            stationId = entity.stationId,
            stationName = entity.stationName
        )
    }
    fun mapToEntity(domainModel: Vehicle): VehicleCacheEntity {
        return VehicleCacheEntity(
            id = domainModel.id,
            plate = domainModel.plate,
            brand = domainModel.brand,
            model = domainModel.model,
            imageUrl = domainModel.imageUrl,
            availability = domainModel.availability,
            isDiesel = domainModel.isDiesel,
            costsPerDay = domainModel.costsPerDay,
            costsPerHour = domainModel.costsPerHour,
            costsPerKm = domainModel.costsPerKm,
            stationId = domainModel.stationId,
            stationName = domainModel.stationName
        )
    }
    fun mapFromEntityList(entities: List<VehicleCacheEntity>):List<Vehicle>{
        return entities.map {
            mapFromEntity(it)
        }
    }
}