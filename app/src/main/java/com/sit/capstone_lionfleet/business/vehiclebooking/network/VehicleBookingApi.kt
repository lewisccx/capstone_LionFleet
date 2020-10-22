package com.sit.capstone_lionfleet.business.vehiclebooking.network

import com.sit.capstone_lionfleet.business.vehiclebooking.network.request.NewVehicleBookingRequest
import com.sit.capstone_lionfleet.business.vehiclebooking.network.response.NewVehicleBookingResponse
import com.sit.capstone_lionfleet.business.vehiclebooking.network.response.VehicleScheduleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface VehicleBookingApi {

    @GET("bookings/vehicle/{vehicleId}/schedule")
    suspend fun getVehicleBookingSchedule(@Path("vehicleId") vehicleId: String): Response<VehicleScheduleResponse>

    @POST("bookings/{vehicleId}")
    suspend fun newVehicleBooking(@Body newVehicleBookingRequest: NewVehicleBookingRequest, @Path("vehicleId") vehicleId: String): NewVehicleBookingResponse

}