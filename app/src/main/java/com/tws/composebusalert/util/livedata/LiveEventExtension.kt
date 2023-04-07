package com.tws.composebusalert.util.livedata

import androidx.lifecycle.LiveData

/**
 * Extension function of the live data to listen only once
 * */
fun <T> LiveData<T>.toSingleEvent(): LiveData<T> {
    val result = LiveEvent<T>()
    result.addSource(this) {
        result.value = it
    }
    return result
}