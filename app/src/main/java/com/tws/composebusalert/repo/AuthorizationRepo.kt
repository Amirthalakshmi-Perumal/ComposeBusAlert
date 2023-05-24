package com.tws.composebusalert.repo

import android.content.Context
import android.provider.ContactsContract

import androidx.navigation.NavController
import com.tws.composebusalert.request.GeoPositionRequest
import com.tws.composebusalert.request.StartLocationServiceRequest
import com.tws.composebusalert.request.StopLocationUpdateRequest
import com.tws.composebusalert.request.UserData
import com.tws.composebusalert.responses.*
import com.tws.composebusalert.services.Resource
import com.tws.composebusalert.services.ResourceBus
import kotlinx.coroutines.flow.Flow

interface AuthorizationRepo {
    suspend fun checkRegisterMobileNumber(
        countryCode: String,
        mobNum: String,
        userType: String,
        navController: NavController? = null,
        context: Context
    ) : Flow<Resource<CheckMobileNumberResponse>>

    suspend fun registerUser(userData: UserData): ResourceBus<UserRegisterResponse>
    suspend fun getDriveDetails(id: String): ResourceBus<Profile>
//    suspend fun firebaseAuth():FirebaseAuth

    suspend fun getRouteList(
        branchId: String?
    ): ResourceBus<List<RouteListResponse>>
   /* suspend fun checkMobile(
        checkMobileNumberRequest: CheckMobileNumberRequest
    ): Resource<CheckMobileNumberResponse>*/

    suspend fun getVehicleList(
        branchId: String?
    ): ResourceBus<VehicleRouteListResponse>

    suspend fun startLocationService(startLocationServiceRequest: StartLocationServiceRequest):
            ResourceBus<StartLocationServiceResponse>
//    suspend fun getStudentList(code: String?): ResourceBus<List<Profile>>

    suspend fun stopLocationUpdate(stopLocationServiceRequest: StopLocationUpdateRequest):
            ResourceBus<StartLocationServiceResponse>

    suspend fun updateGeoLocation(geoPositionRequest: GeoPositionRequest):
            ResourceBus<GeoPositionResponse>

}