package com.tws.composebusalert.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.TextUtils
import com.tws.composebusalert.di.Settings
import com.tws.composebusalert.exception.NoNetworkException
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

/**
 * Interceptor for network service which can set headers for requests
 * @param context is required to check the network state of the app
 * */
class HttpInterceptor(private val context: Context) : Interceptor {

    @Inject
    lateinit var settings: Settings

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isConnected()) {
            throw NoNetworkException()
        }
        var request: Request = chain.request()

        val builder: Request.Builder = request.newBuilder()
        builder.header("Content-Type", "application/json")
//        val token: String? = settings.token
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwcm9maWxlIjoiZDJiNWVlZDgtOTZhMS00MDAzLThlN2ItNjU3MTc2N2U5NjljIiwiaWF0IjoxNjgwODUyOTM3LCJleHAiOjE2ODA5MzkzMzd9.ntlzuhZ-AwQscIYgDVWMVs2SRM3h-ABSMcJ8vN9E3UY"
//        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwcm9maWxlIjoiZjRmMGRiYTctMTc0MS00YzRjLWI1YzUtNDBkMGJiN2QwMmNiIiwiaWF0IjoxNjgwNzg4NzA2LCJleHAiOjE2ODA4NzUxMDZ9.IHwkHRcvH6SC6stn-ZuNOs899EsC4KDPYLAbyMsF1W0"
//        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwcm9maWxlIjoiZjRmMGRiYTctMTc0MS00YzRjLWI1YzUtNDBkMGJiN2QwMmNiIiwiaWF0IjoxNjgwNzc5MzU1LCJleHAiOjE2ODA4NjU3NTV9._FOsf1WVJn9WRqDKzfU8YCcyxdcDNd2n6a4NMJM07sw"

        setAuthHeader(builder, token)
        request = builder.build()
        return chain.proceed(request)
    }
    private fun setAuthHeader(builder: Request.Builder, token: String?) {
        if (!TextUtils.isEmpty(token)) {
            builder.header("Authorization", String.format("Bearer %s", token))
        }
    }
    @Suppress("ComplexMethod", "ReturnCount", "DEPRECATION")
    private fun isConnected(): Boolean {
        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {

                result = when (type) {
                    ConnectivityManager.TYPE_WIFI,
                    ConnectivityManager.TYPE_MOBILE,
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return result
    }
}