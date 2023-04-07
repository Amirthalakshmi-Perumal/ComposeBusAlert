package com.tws.composebusalert.mapdomain.model

data class GooglePlacesInfo(
    val geocoded_waypoints: List<GeocodedWaypoints>,
    val routes: List<Routes>,
    val status: String
)
