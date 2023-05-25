package com.tws.composebusalert.responses
import com.google.gson.annotations.SerializedName

/**
 *Response Class to get the passenger update response
 * class to parse the Json into Kotlin data Model
 * */
data class UpdatePassengerDetailResponse(
    @SerializedName("caretaker")
    val caretaker: String?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("createdBy")
    val createdBy: Any?,
    @SerializedName("extraData")
    val extraData: ExtraData?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("isActive")
    val isActive: Boolean?,
    @SerializedName("isDeleted")
    val isDeleted: Boolean?,
    @SerializedName("notifyDistance")
    val notifyDistance: Any?,
    @SerializedName("notifyTime")
    val notifyTime: Int?,
    @SerializedName("passenger")
    val passenger: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    @SerializedName("updatedBy")
    val updatedBy: Any?
)
/**
 *Response Class commonly used to get extra data needed from server
 * class to parse the Json into Kotlin data Model
 * */
data class ExtraData(
    @SerializedName("isNotifyTimeUpdated")
    val isNotifyTimeUpdated: Boolean,
    @SerializedName("class")
    val standard: String?,
    @SerializedName("section")
    val section: String?
)