package com.tws.composebusalert.exception


import com.tws.composebusalert.NO_INTERNET_ERROR_CODE
import com.tws.composebusalert.services.ApiFailureException
import com.tws.composebusalert.services.BusAlertException
import com.tws.composebusalert.services.Resource
import java.net.ConnectException

const val SERVER_ERROR = 500
const val NO_INTERNET_CONNECTION_TRY =
    "No internet connection, Please check your mobile data or Wi-fi"
const val FAILED_CONNECTION_SERVER = "Failed to connect to the server, Please try after some time"

object ErrorHandler {
    inline fun <reified T> handleException(
        exception: Exception,
    ): Resource<T> {
        return when (exception) {
            is BusAlertException -> {
                Resource.error(
                    ApiFailureException(
                        NO_INTERNET_CONNECTION_TRY,
                        null,
                        NO_INTERNET_ERROR_CODE
                    )
                )
            }
            is NoNetworkException -> {
                Resource.error(
                    ApiFailureException(
                        NO_INTERNET_CONNECTION_TRY,
                        null,
                        NO_INTERNET_ERROR_CODE
                    )
                )
            }
            is ConnectException -> {
                Resource.error(
                    ApiFailureException(
                        FAILED_CONNECTION_SERVER,
                        null,
                        SERVER_ERROR
                    )
                )
            }
            else -> {
                Resource.error(ApiFailureException(exception.localizedMessage, exception, null))
            }
        }
    }
}

