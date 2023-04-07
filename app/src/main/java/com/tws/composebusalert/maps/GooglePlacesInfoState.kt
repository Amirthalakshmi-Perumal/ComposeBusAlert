package com.tws.composebusalert.maps

import com.tws.composebusalert.mapdomain.model.GooglePlacesInfo


data class GooglePlacesInfoState(val direction: GooglePlacesInfo? = null, val isLoading: Boolean = false)