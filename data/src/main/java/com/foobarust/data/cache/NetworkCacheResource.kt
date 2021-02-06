package com.foobarust.data.cache

import android.util.Log
import com.foobarust.domain.states.Resource
import com.foobarust.domain.utils.cancelIfActive
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Created by kevin on 1/23/21
 */

private const val TAG = "NetworkCacheResource"

internal inline fun <T> networkCacheResource(
    noinline cacheSource: suspend () -> T,
    crossinline networkSource: () -> Flow<Resource<T>>,
    noinline updateCache: suspend (T) -> Unit
): Flow<Resource<T>> = channelFlow {
    var observeCacheJob: Job? = null
    networkSource().collect {
        when (it) {
            is Resource.Success -> {
                // Cancel cache flow when the network is restored.
                observeCacheJob?.cancelIfActive()
                channel.offer(it)
                Log.d(TAG, "Offer network resource.")
                startUpdateCache(updateCache, it.data)
            }
            is Resource.Error -> {
                // Emit result from cache flow when the network is down.
                observeCacheJob = startFetchCacheSource(cacheSource)
            }
            is Resource.Loading -> {
                channel.offer(it)
            }
        }
    }
}

private fun<T> ProducerScope<Resource<T>>.startFetchCacheSource(
    cacheSource: suspend () -> T
): Job = launch {
    channel.offer(Resource.Loading())
    try {
        Log.d(TAG, "Offer cache resource.")
        val result = cacheSource()
        channel.offer(Resource.Success(result))
    } catch (e: Exception) {
        channel.offer(Resource.Error(e.message))
    }
}

private fun<T> ProducerScope<Resource<T>>.startUpdateCache(
    updateLocal: suspend (T) -> Unit,
    networkData: T
): Job = launch {
    Log.d(TAG, "Update cache.")
    updateLocal(networkData)
}