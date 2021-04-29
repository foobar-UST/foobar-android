package com.foobarust.domain.cache

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

inline fun <T> networkCacheResource(
    noinline cacheSource: suspend () -> T,
    crossinline networkSource: () -> Flow<Resource<T>>,
    noinline updateCache: suspend (T) -> Unit
): Flow<Resource<T>> = channelFlow {
    var observeCacheJob: Job? = null
    networkSource().collect { resource ->
        when (resource) {
            is Resource.Success -> {
                // Cancel caching when the network is restored.
                observeCacheJob?.cancelIfActive()
                channel.offer(resource)
                startUpdateCache(updateCache, resource.data)
            }
            is Resource.Error -> {
                // Emit result from cache flow when the network is down.
                observeCacheJob = startFetchCacheSource(cacheSource)
            }
            is Resource.Loading -> {
                channel.offer(resource)
            }
        }
    }
}

@PublishedApi internal fun<T> ProducerScope<Resource<T>>.startFetchCacheSource(
    cacheSource: suspend () -> T
): Job = launch {
    channel.offer(Resource.Loading())
    try {
        val result = cacheSource()
        channel.offer(Resource.Success(result))
    } catch (e: Exception) {
        channel.offer(Resource.Error(e.message))
    }
}

@PublishedApi internal fun<T> ProducerScope<Resource<T>>.startUpdateCache(
    updateLocal: suspend (T) -> Unit,
    networkData: T
): Job = launch {
    updateLocal(networkData)
}