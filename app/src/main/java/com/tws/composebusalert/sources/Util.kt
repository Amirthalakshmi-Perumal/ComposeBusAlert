package com.tws.composebusalert.sources

object Util {
    const val MAX_RETRIES = 3L
    private const val INITIAL_BACKOFF = 2000L

    fun getBackoffDelay(attempt: Long) = INITIAL_BACKOFF * (attempt + 1)
}