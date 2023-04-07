package com.tws.composebusalert.webservice

import com.tws.composebusalert.responses.DriverCurrentLocationResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * [BusDataSource] contains the endpoint URL for the
 * bus location update
 * */
interface BusDataSource {

    /**
     * To update the passengerDetail
     * */
    @GET("/api/v1/vehicleLocation")
    suspend fun getCurrentBusLocation(
        @Query("activity") activityId: String
    ): DriverCurrentLocationResponse?
}