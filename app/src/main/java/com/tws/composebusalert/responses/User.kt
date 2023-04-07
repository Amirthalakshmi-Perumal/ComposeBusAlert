package com.tws.composebusalert.responses
import com.google.gson.annotations.SerializedName

/**
 * User Class to parse the json into model class
 * */
data class User(
    @SerializedName("countryCode")
    val countryCode: String?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("createdBy")
    val createdBy: Any?,
    @SerializedName("email")
    val email: Any?,
    @SerializedName("extraData")
    val extraData: UserMetadata?,
    @SerializedName("facebookId")
    val facebookId: Any?,
    @SerializedName("googleId")
    val googleId: Any?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("isActive")
    val isActive: Boolean?,
    @SerializedName("isDeleted")
    val isDeleted: Boolean?,
    @SerializedName("phoneNumber")
    val phoneNumber: String?,
    @SerializedName("profile")
    val profile: Profile?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    @SerializedName("updatedBy")
    val updatedBy: Any?
)