package com.tws.composebusalert.repo.impl

import com.tws.composebusalert.map_features.GooglePlacesApi
import com.tws.composebusalert.mapdomain.model.GooglePlacesInfo
import com.tws.composebusalert.repo.GooglePlacesInfoRepository
import com.tws.composebusalert.services.Resource
import com.tws.composebusalert.services.ResourceMap

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class GooglePlacesInfoRepositoryImplementation(private val api: GooglePlacesApi):
    GooglePlacesInfoRepository {
    override fun getDirection(
        origin: String,
        destination: String,
        key: String
    ): Flow<ResourceMap<GooglePlacesInfo>> = flow{
        emit(ResourceMap.Loading())
        try {
            val directionData = api.getDirection(origin = origin, destination = destination, key=key)
            emit(ResourceMap.Success(data = directionData))
        }catch (e: HttpException){
            emit(ResourceMap.Error(message = "Oops something is not right: $e"))
        }catch (e: IOException){
            emit(ResourceMap.Error(message = "No Internet Connection: $e"))
        }
    }

}