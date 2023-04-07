package com.tws.composebusalert.responses

import com.google.gson.annotations.SerializedName

/**
 * @see Profile class to parse to json into kotlin class
 * */
data class Profile(
    @SerializedName("address")
    val address: Address?,
    @SerializedName("branch")
    val branch: String?,
    @SerializedName("caretaker")
    val caretaker: Caretaker?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("createdBy")
    val createdBy: Any?,
    @SerializedName("deviceDetail")
    val deviceDetail: Any?,
    @SerializedName("extraData")
    val extraData: Any?,
    @SerializedName("firstName")
    val firstName: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("isActive")
    val isActive: Boolean?,
    @SerializedName("isDeleted")
    val isDeleted: Boolean?,
    @SerializedName("lastName")
    val lastName: String?,
    @SerializedName("middleName")
    val middleName: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("phoneNumber")
    val phoneNumber: String?,
    @SerializedName("profilePicURL")
    var profilePicURL: String?,
    @SerializedName("role")
    val role: Any?,
    @SerializedName("route")
    val route: Route?,
    @SerializedName("stopping")
    val stopping: Stopping?,
    @SerializedName("studentCode")
    val studentCode: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    @SerializedName("updatedBy")
    val updatedBy: Any?,
    @SerializedName("countryCode")
    val countryCode: String?,
    @SerializedName("organization")
    val organization: String?

)

/**
 * @see Address response model which is part of profile
 * */
data class Address(
    @SerializedName("city")
    val city: String?,
    @SerializedName("country")
    val country: String?,
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
    @SerializedName("pincode")
    val pincode: Int?,
    @SerializedName("state")
    val state: String?,
    @SerializedName("streetLine1")
    val streetLine1: String?,
    @SerializedName("streetLine2")
    val streetLine2: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    @SerializedName("updatedBy")
    val updatedBy: Any?,
    @SerializedName("fullAddress")
    val fullAddress: String?

)

/**
 * @see Branch response model which is part of profile
 * */
data class Branch(
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
    @SerializedName("name")
    val name: String?,
    @SerializedName("organization")
    val organization: String?,
    @SerializedName("primaryContact")
    val primaryContact: Any?,
    @SerializedName("profile")
    val profile: String?,
    @SerializedName("secondaryContact")
    val secondaryContact: Any?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    @SerializedName("updatedBy")
    val updatedBy: Any?
)

/**
 * @see CareTaker response model which is part of profile
 * */
data class Caretaker(
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
    @SerializedName("notifyDistance")
    val notifyDistance: Int?,
    @SerializedName("notifyTime")
    val notifyTime: Int?,
    @SerializedName("profile")
    val profile: String?,
    @SerializedName("relationship")
    val relationship: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    @SerializedName("updatedBy")
    val updatedBy: Any?
)

/**
 * @see Route response model which is part of profile
 * */
data class Route(
    @SerializedName("branch")
    val branch: String?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("createdBy")
    val createdBy: Any?,
    @SerializedName("endPoint")
    val endPoint: EndPoint?,
    @SerializedName("endTime")
    val endTime: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("isActive")
    val isActive: Boolean?,
    @SerializedName("isDeleted")
    val isDeleted: Boolean?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("startPoint")
    val startPoint: StartPoint?,
    @SerializedName("startTime")
    val startTime: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    @SerializedName("updatedBy")
    val updatedBy: Any?,
    @SerializedName("copyrights")
    val copyrights: String?,
    @SerializedName("overview_polyline")
    val overviewPolyline: OverviewPolyline?,
    @SerializedName("summary")
    val summary: String?,
    @SerializedName("warnings")
    val warnings: List<Any?>?,
    @SerializedName("waypoint_order")
    val waypointOrder: List<Int?>
)

/**
 * @see Stopping response model which is part of profile
 * */
data class Stopping(
    @SerializedName("address")
    val address: String?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("createdBy")
    val createdBy: Any?,
    @SerializedName("dropTime")
    val dropTime: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("isActive")
    val isActive: Boolean?,
    @SerializedName("isDeleted")
    val isDeleted: Boolean?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("order")
    val order: Int?,
    @SerializedName("pickupTime")
    val pickupTime: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    @SerializedName("updatedBy")
    val updatedBy: Any?
)

/**
 *Response Class to get the starting point of route
 * class to parse the Json into Kotlin data Model
 * */
data class EndPoint(
    @SerializedName("city")
    val city: String?,
    @SerializedName("country")
    val country: String?,
    @SerializedName("fullAddress")
    val fullAddress: String?,
    @SerializedName("latitude")
    val latitude: Double?,
    @SerializedName("longitude")
    val longitude: Double?,
    @SerializedName("pincode")
    val pincode: Int?,
    @SerializedName("state")
    val state: String?,
    @SerializedName("streetLine1")
    val streetLine1: String?,
    @SerializedName("streetLine2")
    val streetLine2: String?
)

/**
 *Response Class to get the end point of route
 * class to parse the Json into Kotlin data Model
 * */
data class StartPoint(
    @SerializedName("city")
    val city: String?,
    @SerializedName("country")
    val country: String?,
    @SerializedName("fullAddress")
    val fullAddress: String?,
    @SerializedName("latitude")
    val latitude: Double?,
    @SerializedName("longitude")
    val longitude: Double?,
    @SerializedName("pincode")
    val pincode: Int?,
    @SerializedName("state")
    val state: String?,
    @SerializedName("streetLine1")
    val streetLine1: String?,
    @SerializedName("streetLine2")
    val streetLine2: String?
)