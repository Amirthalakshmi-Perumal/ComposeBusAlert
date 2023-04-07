package com.tws.composebusalert.mapdomain.model

import com.tws.composebusalert.mapdomain.model.Legs
import com.tws.composebusalert.mapdomain.model.OverviewPolyline

data class Routes(
    val summary: String,
    val overview_polyline: OverviewPolyline,
    val legs: List<Legs>
)
