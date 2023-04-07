package com.tws.composebusalert.responses

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue


/**
 * The response class of the StartService API
 * */
@Parcelize
data class StartLocationServiceResponse(
    @SerializedName("branch")
    val branch: String?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("createdBy")
    val createdBy: @RawValue Any?,
    @SerializedName("driver")
    val driver: String?,
    @SerializedName("endTime")
    val endTime: @RawValue Any?,
    @SerializedName("extraData")
    val extraData: @RawValue Any?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("isActive")
    val isActive: Boolean?,
    @SerializedName("isDeleted")
    val isDeleted: Boolean?,
    @SerializedName("mapDetail")
    val mapDetail:@RawValue MapDetail?,
    @SerializedName("route")
    val route: String?,
    @SerializedName("startTime")
    val startTime: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    @SerializedName("updatedBy")
    val updatedBy: @RawValue Any?,
    @SerializedName("vehicle")
    val vehicle: @RawValue Any?
) :Parcelable {
    var latitude: Double = 0.0
        set(value) {
            field = value
        }
        get() = field

    var longitude: Double = 0.0
        set(value) {
            field = value
        }
        get() = field
}
