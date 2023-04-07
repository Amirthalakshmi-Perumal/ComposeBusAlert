package com.tws.composebusalert.exception

import java.io.IOException
private const val NO_INTERNET_CONNECTION = "No Internet Connection"

open class NoNetworkException : IOException() {

    override fun getLocalizedMessage(): String? = NO_INTERNET_CONNECTION

}