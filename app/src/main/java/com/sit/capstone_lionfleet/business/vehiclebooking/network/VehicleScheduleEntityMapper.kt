package com.sit.capstone_lionfleet.business.bookings.network

import com.sit.capstone_lionfleet.business.vehiclebooking.network.model.VehicleSchedule
import com.sit.capstone_lionfleet.business.vehiclebooking.network.response.VehicleScheduleResponse
import com.sit.capstone_lionfleet.utils.EntityMapper
import java.time.LocalDate
import javax.inject.Inject

class VehicleScheduleEntityMapper
@Inject
constructor() : EntityMapper<VehicleScheduleResponse, VehicleSchedule> {

    override fun mapFromEntity(entity: VehicleScheduleResponse): VehicleSchedule {
        val vehicleSchedule = mapFromEntityResponse(entity)
        return VehicleSchedule(
            schedule = vehicleSchedule
        )
    }

//    fun mapStringToDate(entity: VehicleScheduleResponse): List<LocalDate> {
//        val dateList: List<LocalDate> = emptyList()
//
//        for (str in entity.schedule) {
//            val localDateStr = strToLocalDate.format(str)
//            val localDate = LocalDate.parse(localDateStr)
//            dateList.toMutableList().add(localDate)
//        }
//        return dateList
//    }
    private fun mapFromEntityResponse(entity: VehicleScheduleResponse):List<LocalDate>{
        return entity.schedule.map {
            mapStringToDate(it)
        }
}
   private fun mapStringToDate(dateStr: String): LocalDate{
        return LocalDate.parse(dateStr)
    }
}