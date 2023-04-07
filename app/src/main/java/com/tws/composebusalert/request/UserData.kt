package com.tws.composebusalert.request
import com.google.gson.annotations.SerializedName

/**
 * User response class to parse json
 * */
data class UserData(
    @SerializedName("email")
    val email: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("password")
    val password: String?,
    @SerializedName("type")
    val type: String?,
//    @SerializedName("googleId")
//    val googleId: String?,
//    @SerializedName("facebookId")
//    val facebookId: String?,
    @SerializedName("phoneNumber")
    val phoneNumber: String?,
    @SerializedName("countryCode")
    val countryCode: String?
)