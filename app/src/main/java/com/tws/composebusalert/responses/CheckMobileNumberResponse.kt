package com.tws.composebusalert.responses
import com.google.gson.annotations.SerializedName

/**
 *Response Class to identify user mobile Number  present in School Db
 * class to parse the Json into Kotlin data Model
 * */

data class CheckMobileNumberResponse(
    @SerializedName("address")
    val address: Address?,
    @SerializedName("branch")
    val branch: String?,
    @SerializedName("countryCode")
    val countryCode: String?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("createdBy")
    val createdBy: Any?,
    @SerializedName("deviceDetail")
    val deviceDetail: Any?,
    @SerializedName("extraData")
    val extraData: UserMetadata?,
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
    @SerializedName("organization")
    val organization: String?,
    @SerializedName("phoneNumber")
    val phoneNumber: String?,
    @SerializedName("profilePicURL")
    val profilePicURL: Any?,
    @SerializedName("role")
    val role: Any?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    @SerializedName("updatedBy")
    val updatedBy: Any?
)
