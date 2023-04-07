package com.tws.composebusalert.map_features

import com.tws.composebusalert.mapdomain.model.GeocodedWaypoints

data class GeocodedWaypointsDto(
    val geocoder_status: String,
    val place_id: String,
    val types: List<String>
){
    fun toGeocodedWaypoints(): GeocodedWaypoints {
        return GeocodedWaypoints(
            geocoder_status = geocoder_status,
            place_id =place_id,
            types = types
        )
    }
}
