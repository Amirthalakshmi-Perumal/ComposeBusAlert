package com.tws.composebusalert.responses

import com.google.gson.annotations.SerializedName

/**
 * data class to maintain the route selection details
 * */
data class RouteSelectionResponseModel(
    @SerializedName("name")
    val name: String,
    @SerializedName("isChecked")
    var isChecked: Boolean
)