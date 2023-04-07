package com.tws.composebusalert.webservice

import com.tws.composebusalert.request.CheckMobileNumberRequest
import com.tws.composebusalert.request.UserData
import com.tws.composebusalert.responses.CheckMobileNumberResponse
import com.tws.composebusalert.responses.Profile
import com.tws.composebusalert.responses.UserRegisterResponse
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

    /**
     * This method is used to get the Profile details
     * */
    @POST("/api/v1/register")
    suspend fun registerUser(@Body userData: UserData): UserRegisterResponse

    /**
     * This function is used to get the Particular user profile
     * @param [id] is the user id to get
     * */
    @GET("/api/v1/profile/{id}")
    suspend fun getProfile(@Path("id") id: String): Profile


}