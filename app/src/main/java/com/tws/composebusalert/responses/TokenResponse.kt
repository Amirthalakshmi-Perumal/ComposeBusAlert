package com.tws.composebusalert.responses

import com.google.gson.annotations.SerializedName

/**
 * Driver Token Response class to parse the Json into Kotlin data Model
 * */
data class TokenResponse(
    @SerializedName("token")
    val token: String?,
    @SerializedName("vehicle")
    val vehicle: Vehicle?
)

/**
 * @see Vehicle class to parse the Json into Kotlin data Model
 * */
data class Vehicle(
    @SerializedName("branch")
    val branch: Branch?,
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
    @SerializedName("name")
    val name: String?,
    @SerializedName("profile")
    val profile: Profile?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    @SerializedName("updatedBy")
    val updatedBy: Any?,
    @SerializedName("vehicleNumber")
    val vehicleNumber: String?
)