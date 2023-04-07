package com.tws.composebusalert.services
import android.text.TextUtils
import android.widget.Toast


open class ApiFailureException(message: String? = null, cause: Throwable? = null, code: Int? = null) :
    Exception(message) {
    private val _message: String? by lazy {
        if (!TextUtils.isEmpty(message)) {

            cause?.toString()
        } else {

            null
        }
    }

    private val _code: Int? by lazy {
        code
    }

    val code get() = _code

    override val message get() = _message ?: super.message

    init {
        if (cause != null) {
            super.initCause(cause)
        }
    }
}
