package com.tws.composebusalert.request
import com.google.gson.annotations.SerializedName

/**
 * Stop location request model
 * */
data class StopLocationUpdateRequest(
    @SerializedName("activity")
    val relationActivity: String?
)