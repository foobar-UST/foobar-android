package com.foobarust.domain.states

import com.foobarust.domain.states.Result.Success

/**
 * Created by kevin on 8/9/20
 * Wrapper class holding data results and represent data states
 */
sealed class Result<out T> {

    data class Success<out T>(val data: T) : Result<T>()

    data class Error(val message: String?) : Result<Nothing>()

    object Loading : Result<Nothing>()
}

fun <T> Result<T>.getSuccessDataOr(fallback: T): T {
    return (this as? Success<T>)?.data ?: fallback
}