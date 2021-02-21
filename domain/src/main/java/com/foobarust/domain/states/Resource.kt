package com.foobarust.domain.states

import androidx.annotation.FloatRange
import com.foobarust.domain.states.Resource.Success

/**
 * Created by kevin on 8/9/20
 */
sealed class Resource<out T> {

    data class Success<out T>(val data: T) : Resource<T>()

    data class Error(val message: String?) : Resource<Nothing>()

    data class Loading(
        @FloatRange(from = 0.0, to = 100.0)
        val progress: Double? = null
    ) : Resource<Nothing>()
}

/**
 * Check if there is a correctly received data,
 * or else return a custom fallback object
 */
fun<T> Resource<T>.getSuccessDataOr(fallback: T): T {
    return (this as? Success<T>)?.data ?: fallback
}
