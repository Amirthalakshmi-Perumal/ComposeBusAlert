package com.tws.composebusalert.map_features

import com.tws.composebusalert.mapdomain.model.OverviewPolyline


data class OverviewPolylineDto(
    val points: String
){
    fun toOverviewPolyline(): OverviewPolyline {
        return OverviewPolyline(
            points = points
        )
    }
}
