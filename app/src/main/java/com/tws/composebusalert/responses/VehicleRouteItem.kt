package com.tws.composebusalert.responses


import com.google.gson.annotations.SerializedName

data class VehicleRouteItem(
    @SerializedName("branch")
    val branch: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("createdBy")
    val createdBy: Any,
    @SerializedName("endPoint")
    val endPoint: EndPointR,
    @SerializedName("extraData")
    val extraData: ExtraData1,
    @SerializedName("id")
    val id: String,
    @SerializedName("isActive")
    val isActive: Boolean,
    @SerializedName("isDeleted")
    val isDeleted: Boolean,
    @SerializedName("name")
    val name: String,
    @SerializedName("startPoint")
    val startPoint: StartPointR,
    @SerializedName("type")
    val type: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("updatedBy")
    val updatedBy: Any,
    @SerializedName("vehicle")
    val vehicle: List<VehicleR>
)

data class ExtraData1(
    @SerializedName("orders")
    val orders: Any
)

data class VehicleR(
    @SerializedName("branch")
    val branch: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("createdBy")
    val createdBy: Any,
    @SerializedName("device")
    val device: String,
    @SerializedName("extraData")
    val extraData: Any,
    @SerializedName("id")
    val id: String,
    @SerializedName("isActive")
    val isActive: Boolean,
    @SerializedName("isDeleted")
    val isDeleted: Boolean,
    @SerializedName("name")
    val name: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("updatedBy")
    val updatedBy: Any,
    @SerializedName("vehicleNumber")
    val vehicleNumber: String
)
data class StartPointR(
    @SerializedName("city")
    val city: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("fullAddress")
    val fullAddress: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("pincode")
    val pincode: String,
    @SerializedName("state")
    val state: String,
    @SerializedName("streetLine1")
    val streetLine1: String,
    @SerializedName("streetLine2")
    val streetLine2: Any
)
data class EndPointR(
    @SerializedName("city")
    val city: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("fullAddress")
    val fullAddress: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("pincode")
    val pincode: String,
    @SerializedName("state")
    val state: String,
    @SerializedName("streetLine1")
    val streetLine1: String,
    @SerializedName("streetLine2")
    val streetLine2: Any
)