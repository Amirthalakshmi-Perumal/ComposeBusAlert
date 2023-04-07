package com.tws.composebusalert.request

import com.google.gson.annotations.SerializedName

/**
 *Request class to identify user mobile Number  present in School Db
 * class to parse the Json into Kotlin data Model
 * */
data class CheckMobileNumberRequest(
    @SerializedName("countryCode")
    val countryCode: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    @SerializedName("type")
    val type: String
)