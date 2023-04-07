package com.tws.composebusalert.request

import com.google.gson.annotations.SerializedName

/**
 * Driver GeoPosition Request class to parse the Json into Kotlin data Model
 * */
data class GeoPositionRequest(
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("activity")
    val relationActivity: String,
    @SerializedName("startWayPoint")
    val startWayPoint: StartWayPoint?,
    @SerializedName("movementDistance")
    val movementDistance: Double
)

/**
 * driver start point json request
 * */
data class StartWayPoint(
    @SerializedName("latitude")
    val latitude: Double?,
    @SerializedName("longitude")
    val longitude: Double?
)