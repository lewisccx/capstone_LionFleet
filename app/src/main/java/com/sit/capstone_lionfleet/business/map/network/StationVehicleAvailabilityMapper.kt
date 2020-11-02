package com.sit.capstone_lionfleet.business.map.network

import com.sit.capstone_lionfleet.business.map.network.model.StationVehicleStatus
import com.sit.capstone_lionfleet.business.map.network.model.Vehicle
import com.sit.capstone_lionfleet.business.map.network.response.StationVehicleAvailableResponse
import com.sit.capstone_lionfleet.business.map.network.response.VehicleItem
import com.sit.capstone_lionfleet.utils.EntityMapper
import javax.inject.Inject

class StationVehicleAvailabilityMapper
@Inject
constructor() : EntityMapper<StationVehicleAvailableResponse, StationVehicleStatus> {
    override fun mapFromEntity(entity: StationVehicleAvailableResponse): StationVehicleStatus {
       return StationVehicleStatus(
           total = entity.total.toString(),
           available = entity.available.toString()
       )
    }
}