package com.tws.composebusalert.request
import com.google.gson.annotations.SerializedName

/**
 *Request class to get student list of caretaker
 * class to parse the Json into Kotlin data Model
 * */
data class PassengerDetailRequest(
    @SerializedName("countryCode")
    val countryCode: String?,
    @SerializedName("phoneNumber")
    val phoneNumber: String?
)