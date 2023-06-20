package com.tws.composebusalert.network

import com.tws.composebusalert.datastore.StoreData
import com.tws.composebusalert.responses.TokenResponse
import com.tws.composebusalert.webservice.UserDataSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class AuthAuthenticator @Inject constructor(
    private val tokenManager: StoreData,
): Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val token = runBlocking {
            tokenManager.getToken().first()
        }
        return runBlocking {
            val newToken = getNewToken(token)

            if (!newToken.isSuccessful || newToken.body() == null) { //Couldn't refresh the token, so restart the login process
                tokenManager.deleteToken()
            }

            newToken.body()?.let {
                it.token?.let { it1 -> tokenManager.saveTokenR(it1) }
                response.request.newBuilder()
                    .header("Authorization", "Bearer ${it.token}")
                    .build()
            }
        }
    }

    private suspend fun getNewToken(refreshToken: String?): retrofit2.Response<TokenResponse> {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl( "http://206.189.137.65")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        val service = retrofit.create(UserDataSource::class.java)
        return service.refreshToken("Bearer $refreshToken")
    }
}


