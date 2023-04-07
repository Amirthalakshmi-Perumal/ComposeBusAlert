package com.tws.composebusalert.usecase

import com.tws.composebusalert.mapdomain.model.GooglePlacesInfo
import com.tws.composebusalert.repo.GooglePlacesInfoRepository
import com.tws.composebusalert.services.Resource
import com.tws.composebusalert.services.ResourceMap
import kotlinx.coroutines.flow.Flow

class GetDirectionInfo(private val repository: GooglePlacesInfoRepository) {
    operator fun invoke(origin: String, destination: String, key: String): Flow<ResourceMap<GooglePlacesInfo>> = repository.getDirection(origin = origin, destination = destination, key = key)
}