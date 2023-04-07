package com.tws.composebusalert.responses

import com.google.gson.annotations.SerializedName

/**
 * To get the extra data of the user
 * */
data class UserMetadata(
    @SerializedName("licenseNumber")
    val licenseNumber: String?
)