package com.tws.composebusalert.map_features


import com.tws.composebusalert.map_features.DistanceDto
import com.tws.composebusalert.map_features.DurationDto
import com.tws.composebusalert.mapdomain.model.Legs

data class LegsDto(
    val distance: DistanceDto,
    val duration: DurationDto
){
    fun toLegs(): Legs {
        return Legs(
            distance = distance.toDistance(),
            duration = duration.toDuration()
        )
    }
}
