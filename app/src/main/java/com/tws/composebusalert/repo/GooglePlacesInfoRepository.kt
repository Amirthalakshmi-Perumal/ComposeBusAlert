package com.tws.composebusalert.repo


import com.tws.composebusalert.mapdomain.model.GooglePlacesInfo
import com.tws.composebusalert.responses.RouteListResponse
import com.tws.composebusalert.services.Resource
import com.tws.composebusalert.services.ResourceMap
import kotlinx.coroutines.flow.Flow

interface GooglePlacesInfoRepository {
    fun getDirection(origin: String, destination: String, key: String): Flow<ResourceMap<GooglePlacesInfo>>



}