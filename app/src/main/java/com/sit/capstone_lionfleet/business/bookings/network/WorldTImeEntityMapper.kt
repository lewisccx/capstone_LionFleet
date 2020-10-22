package com.sit.capstone_lionfleet.business.bookings.network

import com.sit.capstone_lionfleet.business.bookings.network.model.WorldTime
import com.sit.capstone_lionfleet.business.bookings.network.response.WorldTimeResponse
import com.sit.capstone_lionfleet.utils.EntityMapper
import javax.inject.Inject

class WorldTImeEntityMapper
@Inject constructor() : EntityMapper<WorldTimeResponse, WorldTime> {
    override fun mapFromEntity(entity: WorldTimeResponse): WorldTime {
        return WorldTime(
            datetime = entity.datetime,
            timezone = entity.timezone,
            unixtime = entity.unixtime
        )
    }
}