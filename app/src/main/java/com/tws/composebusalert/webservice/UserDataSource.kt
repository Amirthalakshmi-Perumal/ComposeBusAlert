package com.tws.composebusalert.webservice

import com.tws.composebusalert.request.*
import com.tws.composebusalert.responses.*
import retrofit2.Response
import retrofit2.http.*

interface UserDataSource {
    /*
     * [checkMobileNumber] is used to check the mobile number is registered already with school
     * @param checkMobileNumberRequest is the request param to hit service
     */
    @POST("/api/v1/checkNumber")
    suspend fun checkMobileNumber(
        @Body checkMobileNumberRequest: CheckMobileNumberRequest
    ): Response<CheckMobileNumberResponse>
//    ): Call<CheckMobileNumberResponse>

    @POST("/api/v1/register")
    suspend fun registerUser(@Body userData: UserData): UserRegisterResponse

    @GET("/api/v1/profile/{id}")
    suspend fun getProfile(@Path("id") id: String): Profile
    @GET("/api/v1/route")
    suspend fun getRouteList(
        @Query("branch") branchID: String?,
        @Query("isDeleted") deleteItem: Boolean,
        @Query("fields") value: String
    ): List<RouteListResponse>

    @POST("/api/v1/getPassengerDetail")
    suspend fun getPassengersDetail(
        @Body passengerDetailRequest: PassengerDetailRequest,@Query("isDeleted") isDelete: Boolean
    ): List<PassengerDetailResponse>

//    @GET("/api/v1/profile")
//    suspend fun getStudentList(@Query("caretaker") token: String?): List<Profile>
    @GET("/api/v1/route")
    suspend fun getVehicleList(
        @Query("id") routeId: String?,
        @Query("populate") value: String
    ): VehicleRouteListResponse

    @POST("/api/v1/relation/start")
    suspend fun startLocationService(
        @Body startLocationServiceRequest: StartLocationServiceRequest
    ): StartLocationServiceResponse

    /**
     * This function is used to stop the location update service if driver press stop
     * */
    @POST("/api/v1/relation/end")
    suspend fun stopLocationService(
        @Body stopLocationUpdateRequest: StopLocationUpdateRequest
    ): StartLocationServiceResponse
    @POST("/api/v1/geoposition")
    suspend fun updateGeoLocation(@Body geoPositionRequest: GeoPositionRequest): GeoPositionResponse



}