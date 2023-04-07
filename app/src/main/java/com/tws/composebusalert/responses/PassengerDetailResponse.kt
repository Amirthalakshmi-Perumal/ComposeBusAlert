package com.tws.composebusalert.responses

import android.location.Address
import androidx.core.app.ComponentActivity
import com.google.gson.annotations.SerializedName
import okhttp3.Route


data class PassengerDetailResponse(
    @SerializedName("address")
    val address: Address?,
    @SerializedName("branch")
    val branch: String?,
    @SerializedName("caretakerCount")
    val caretakerCount: Any?,
    @SerializedName("countryCode")
    val countryCode: String?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("createdBy")
    val createdBy: Any?,
    @SerializedName("deviceDetail")
    val deviceDetail: Any?,
    @SerializedName("dropStopping")
    val dropStopping: DropStopping?,
    @SerializedName("extraData")
    val extraData: ComponentActivity.ExtraData?,
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
    val middleName: Any?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("notificationDetails")
    val notificationDetails: List<NotificationDetail?>?,
    @SerializedName("organization")
    val organization: String?,
    @SerializedName("phoneNumber")
    val phoneNumber: String?,
    @SerializedName("pickupStopping")
    val pickupStopping: PickupStopping?,
    @SerializedName("profilePicURL")
    val profilePicURL: Any?,
    @SerializedName("route")
    val route: List<Route?>?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    @SerializedName("updatedBy")
    val updatedBy: Any?
)
/**
 *Response Class to get the drop detail of student
 * class to parse the Json into Kotlin data Model
 * */
data class DropStopping(
    @SerializedName("address")
    val address: Address?,
    @SerializedName("branch")
    val branch: String?,
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
    @SerializedName("type")
    val type: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    @SerializedName("updatedBy")
    val updatedBy: Any?
)

/**
 *Response Class to get the Notification detail of student
 * class to parse the Json into Kotlin data Model
 * */
data class NotificationDetail(
    @SerializedName("caretaker")
    val caretaker: String?,
    @SerializedName("notifyDistance")
    val notifyDistance: Any?,
    @SerializedName("notifyTime")
    val notifyTime: Int?,
    @SerializedName("extraData")
    val extraData: ComponentActivity.ExtraData?
)

/**
 *Response Class to get the pickup detail of student
 * class to parse the Json into Kotlin data Model
 * */
data class PickupStopping(
    @SerializedName("address")
    val address: Address?,
    @SerializedName("branch")
    val branch: String?,
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
    @SerializedName("type")
    val type: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    @SerializedName("updatedBy")
    val updatedBy: Any?
)
