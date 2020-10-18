package com.sit.capstone_lionfleet.business.map

sealed class MapStateEvent {

    object GetVehiclesByStationId: MapStateEvent()
    object None: MapStateEvent()
}