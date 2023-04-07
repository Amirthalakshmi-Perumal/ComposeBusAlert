package com.tws.composebusalert.responses
import com.google.gson.annotations.SerializedName

/**
 * DriverGeoPosition Response class to parse the Json into Kotlin data Model
 * */
data class GeoPositionResponse(
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("createdBy")
    val createdBy: Any?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("isActive")
    val isActive: Boolean?,
    @SerializedName("isDeleted")
    val isDeleted: Boolean?,
    @SerializedName("latitude")
    val latitude: Double?,
    @SerializedName("longitude")
    val longitude: Double?,
    @SerializedName("relationactivity")
    val relationactivity: Any?,
    @SerializedName("time")
    val time: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    @SerializedName("updatedBy")
    val updatedBy: Any?
)