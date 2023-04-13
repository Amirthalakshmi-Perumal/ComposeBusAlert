package com.tws.composebusalert.responses
import com.google.gson.annotations.SerializedName

/**
 * RouteListResponse class to parse json data
 * */

data class RouteListResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: String
)