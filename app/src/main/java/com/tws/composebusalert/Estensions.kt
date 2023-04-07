package com.tws.composebusalert

import com.tws.composebusalert.services.Resource
import com.tws.composebusalert.sources.Util.MAX_RETRIES
import com.tws.composebusalert.sources.Util.getBackoffDelay
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retryWhen
import java.io.IOException


/**
 * some common side-effects to your flow to avoid repeating commonly used
 * logic across the app.
 */

fun <T : Any> Flow<Resource<T>>.applyCommonSideEffects() =
    retryWhen { cause, attempt ->
        when {
            (cause is IOException && attempt < MAX_RETRIES) -> {
                delay(getBackoffDelay(attempt))
                true
            }
            else -> {
                false
            }
        }
    }.onStart { emit(Resource.loading(true)) }
        .onCompletion { emit(Resource.loading(false)) }

fun <T : Any> Flow<Resource<T>>.applyCommonSideEffects(forPaging: Boolean) =
    retryWhen { cause, attempt ->
        when {
            (cause is IOException && attempt < MAX_RETRIES) -> {
                delay(getBackoffDelay(attempt))
                true
            }
            else -> {
                false
            }
        }
    }.onStart {
        if (!forPaging) {
            emit(Resource.loading(true))
        }
    }.onCompletion {
        if (!forPaging) {
                emit(Resource.loading(false))
        }
    }

fun <T : Any> Flow<T>.applyCommonSideEffect() =
    retryWhen { cause, attempt ->
        when {
            (cause is IOException && attempt < MAX_RETRIES) -> {
                delay(getBackoffDelay(attempt))
                true
            }
            else -> {
                false
            }
        }
    }.onStart {  }
        .onCompletion {  }

