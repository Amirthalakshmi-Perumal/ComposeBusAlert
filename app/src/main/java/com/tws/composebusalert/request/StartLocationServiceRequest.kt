package com.tws.composebusalert.request
import com.google.gson.annotations.SerializedName

/**
 * Start location service request which is user while start the service
 **/
data class StartLocationServiceRequest(
    @SerializedName("type")
    val rideType: String?,
    @SerializedName("route")
    val routeId: String,
    @SerializedName("vehicle")
    val vehicleId: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double
)