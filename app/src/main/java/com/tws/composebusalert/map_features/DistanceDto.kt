package com.tws.composebusalert.map_features

import com.tws.composebusalert.mapdomain.model.Distance


data class DistanceDto(
    val text: String,
    val value: Int
){
    fun toDistance(): Distance {
        return  Distance(
            text = text,
            value = value
        )
    }
}
