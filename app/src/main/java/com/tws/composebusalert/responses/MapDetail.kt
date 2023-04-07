package com.tws.composebusalert.responses
import com.google.gson.annotations.SerializedName

/**
 * @see MapDetail class to parse json in kotlin model
 * */
data class MapDetail(
    @SerializedName("mapData")
    val mapData: MapData?,
    @SerializedName("currentPointIndex")
    val currentPointIndex: Int?,
    @SerializedName("isReachedEndPoint")
    val isReachedEndPoint: Boolean?,
    @SerializedName("isReachedStartPoint")
    val isReachedStartPoint: Boolean?,
    @SerializedName("points")
    val points: List<Point>,
    @SerializedName("stoppings")
    val stoppings: List<Stoppings>
)

/**
* @see MapData class to parse json into kotlin model
 **/
data class MapData(
    @SerializedName("geocoded_waypoints")
    val geocodedWaypoints: List<GeocodedWaypoint>,
    @SerializedName("routes")
    val routes: List<MapRoute>?,
    @SerializedName("status")
    val status: String?
)

data class GeocodedWaypoint(
    @SerializedName("geocoder_status")
    val geocoderStatus: String,
    @SerializedName("place_id")
    val placeId: String,
    @SerializedName("types")
    val types: List<String>
)

/**
 * @see MapRoute class to parse json into kotlin model
 **/
data class MapRoute(
    @SerializedName("bounds")
    val bounds: Bounds,
    @SerializedName("copyrights")
    val copyrights: String,
    @SerializedName("legs")
    val legs: List<Leg>,
    @SerializedName("overview_polyline")
    val overviewPolyline: OverviewPolyline,
    @SerializedName("summary")
    val summary: String,
    @SerializedName("warnings")
    val warnings: List<Any>,
    @SerializedName("waypoint_order")
    val waypointOrder: List<Int>
)

/**
 * @see OverviewPolyline class to parse json into kotlin model
 **/
data class OverviewPolyline(
    @SerializedName("points")
    val points: String?
)

/**
 * [Point] data class to parse json into kotlin model
 * */
data class Point(
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lng")
    val lng: Double,
    @SerializedName("timePerMeter")
    val timePerMeter: Double?
)

/**
 * [Stoppings] data class to parse json into kotlin model
 * */
data class Stoppings(
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("name")
    val name: String,
    @SerializedName("index")
    val index: Int
)

data class Bounds(
    @SerializedName("northeast")
    val northeast: Northeast,
    @SerializedName("southwest")
    val southwest: Southwest
)
data class Southwest(
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lng")
    val lng: Double
)
data class Leg(
    @SerializedName("distance")
    val distance: Distance,
    @SerializedName("duration")
    val duration: Duration,
    @SerializedName("end_address")
    val endAddress: String,
    @SerializedName("end_location")
    val endLocation: EndLocation,
    @SerializedName("start_address")
    val startAddress: String,
    @SerializedName("start_location")
    val startLocation: StartLocation,
    @SerializedName("steps")
    val steps: List<Step>,
    @SerializedName("traffic_speed_entry")
    val trafficSpeedEntry: List<Any>,
    @SerializedName("via_waypoint")
    val viaWaypoint: List<Any>
)

data class Step(
    @SerializedName("distance")
    val distance: Distance,
    @SerializedName("duration")
    val duration: Duration,
    @SerializedName("end_location")
    val endLocation: EndLocation,
    @SerializedName("end_point_index")
    val endPointIndex: Int,
    @SerializedName("html_instructions")
    val htmlInstructions: String,
    @SerializedName("maneuver")
    val maneuver: String,
    @SerializedName("polyline")
    val polyline: Polyline,
    @SerializedName("start_location")
    val startLocation: StartLocation,
    @SerializedName("start_point_index")
    val startPointIndex: Int,
    @SerializedName("travel_mode")
    val travelMode: String
)

data class Polyline(
    @SerializedName("points")
    val points: String
)

data class Duration(
    @SerializedName("text")
    val text: String,
    @SerializedName("value")
    val value: Int
)

data class EndLocation(
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lng")
    val lng: Double
)

data class StartLocation(
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lng")
    val lng: Double
)

data class Distance(
    @SerializedName("text")
    val text: String,
    @SerializedName("value")
    val value: Int
)

data class Northeast(
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lng")
    val lng: Double
)