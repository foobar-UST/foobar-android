package com.foobarust.domain.utils

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

/**
 * Created by kevin on 4/21/21
 */

internal suspend fun <T> Flow<T>.toListUntil(predicate: suspend (T) -> Boolean): List<T> {
    val results = mutableListOf<T>()
    try {
        coroutineScope {
            collect {
                results.add(it)
                if (predicate(it)) {
                    this.cancel()
                }
            }
        }
    } catch (e: CancellationException) {
        // Ignore
    }

    return results
}