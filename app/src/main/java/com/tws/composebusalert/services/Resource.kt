package com.tws.composebusalert.services

import Status


/**
 * Resource class for handle network calls
 * */

class Resource<T> private constructor(
    val status: Status,
    val data: T? = null,
    val apiError: ApiFailureException? = null,
    val loading: Boolean
) {

    companion object {



        fun <T> success(data: T?): Resource<T> = Resource(Status.SUCCESS, data, null, false)



        fun <T> error(apiError: ApiFailureException?): Resource<T> =
            Resource(Status.ERROR, null, apiError, false)


        fun <T> loading(isLoading: Boolean): Resource<T> =
            Resource(Status.LOADING, null, null, isLoading)
    }
}

sealed class ResourceMap <T>(val data:T?=null,val message:String?=null)
{
    class Loading<T>(data: T?=null):ResourceMap<T>(data)
    class Success<T>(data: T?):ResourceMap<T>(data)
    class Error<T>(message: String,data: T?=null):ResourceMap<T>(data,message)
}
/**
 * Class to handle Network response.
 * It either can be Success with the required data or Error with an exception.
 * @param [status] status of the network call which become success, failed or in-progress
 * @param [data] data holds the result of the network call
 * @param [message] holds the content if the network call get failed
 * */

data class ResourceBus<out T>(val status: Status, val data: T?, val message: String?) {

    companion object {
        /**
         *successful network call return success method
         * */
        fun <T> success(data: T?): ResourceBus<T> =
            ResourceBus(
                Status.SUCCESS,
                data,
                null
            )

        /**
         * when network call ended with any error return error method
         * */
        fun <T> error(msg: String?, data: T?): ResourceBus<T> =
            ResourceBus(
                Status.ERROR,
                data,
                msg
            )

        /**
         *when the network call is still in progress by various reason like
         *slow internet or retriving and huge data from server returns loading
         * */
        fun <T> loading(data: T?): ResourceBus<T> =
            ResourceBus(
                Status.LOADING,
                data,
                null
            )
    }
}