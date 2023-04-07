package com.tws.composebusalert.network

import com.tws.composebusalert.services.ResourceBus
import com.tws.composebusalert.exception.NoNetworkException
import org.json.JSONObject
import retrofit2.HttpException
private const val INVALID_TOKEN = 401
/**
 * Handle the Network call process safely
 * Handle all the Network level issues
 * */
open class ResponseHandler {

    /**
     * Method used when the network call is success
     * */
    fun <T : Any> handleSuccess(data: T?): ResourceBus<T> = ResourceBus.success(data)

    /**
     *Method called when any exception occur during network call
     */

    fun <T : Any> handleException(e: Exception): ResourceBus<T> {
        return when (e) {
            is HttpException -> {
                val message = if (e.code() == INVALID_TOKEN) {
                    "Token expired"
                } else {
                    getError(e)
                }

                ResourceBus.error(message, null)
            }

            is NoNetworkException -> {
                ResourceBus.error(e.localizedMessage, null)
            }

            else -> {
                ResourceBus.error(e.localizedMessage, null)
            }
        }
    }

    private fun getError(e: Exception): String? {
        return try {
            val jObjError = JSONObject((e as HttpException).response()?.errorBody()?.string())
            jObjError.getString("error")
        } catch (e: Exception) {
            "Server error"
        }
    }
}