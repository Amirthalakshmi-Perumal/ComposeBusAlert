package com.tws.composebusalert.map_features

import com.tws.composebusalert.mapdomain.model.Duration


data class DurationDto(
    val text: String,
    val value: Int
){
    fun toDuration(): Duration {
        return Duration(
            text = text,
            value = value
        )
    }
}
