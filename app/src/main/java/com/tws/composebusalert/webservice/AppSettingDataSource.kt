package com.tws.composebusalert.webservice

import com.tws.composebusalert.responses.TokenResponse
import io.reactivex.Single
import retrofit2.http.GET

/**
 * This service class is used for base application network calls
 * */
interface AppSettingDataSource {

    /**
     * This method is used to refresh the token
     */
    @GET("api/v1/refreshToken")
    fun refreshToken(): Single<TokenResponse>
}