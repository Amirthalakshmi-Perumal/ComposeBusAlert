package com.tws.composebusalert.responses
import com.google.gson.annotations.SerializedName

/**
 *Response Class to get the Current Location of Driver
 * class to parse the Json into Kotlin data Model
 * */
data class DriverCurrentLocationResponse(
    @SerializedName("activity")
    val activity: String?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("createdBy")
    val createdBy: Any?,
    @SerializedName("extraData")
    val extraData: Any?,
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
    @SerializedName("time")
    val time: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    @SerializedName("updatedBy")
    val updatedBy: Any?
)