package com.tws.composebusalert.request
import com.google.gson.annotations.SerializedName

/**
 *Request class to update notification time for passenger
 * class to parse the Json into Kotlin data Model
 * */
data class UpdatePassengerDetailRequest(
    @SerializedName("careTaker")
    val careTaker: String?,
    @SerializedName("extraData")
    val extraData: UpdatePassengerExtraData?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("notifyTime")
    val notifyTime: Int?
)

/**
 *Request class to send extra data information to update
 * class to parse the Json into Kotlin data Model
 * */
data class UpdatePassengerExtraData(
    @SerializedName("isNotifyTimeUpdated")
    val isNotifyTimeUpdated: Boolean
)