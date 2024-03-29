package com.foobarust.domain.cache

import com.foobarust.domain.states.Resource
import com.foobarust.testshared.utils.TestCoroutineRule
import com.foobarust.testshared.utils.runBlockingTest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import org.junit.Rule
import org.junit.Test

/**
 * Created by kevin on 4/19/21
 */

class NetworkCacheResourceTest {

    @get:Rule
    var coroutineRule = TestCoroutineRule()

    @Test
    fun `test first result is loading`() = coroutineRule.runBlockingTest {
        val ncr = networkCacheResource(
            cacheSource = { getFakeCacheData() },
            networkSource = { getFakeNetworkData() },
            updateCache = { }
        )
        val firstResult = ncr.toList().first()

        assert(firstResult is Resource.Loading)
    }

    @Test
    fun `test network success, result is network data`() = coroutineRule.runBlockingTest {
        val ncr = networkCacheResource(
            cacheSource = { getFakeCacheData() },
            networkSource = { getFakeNetworkData() },
            updateCache = { }
        )
        val dataResult = ncr.toList().last()

        assert(dataResult is Resource.Success && dataResult.data == FAKE_NETWORK_DATA)
    }

    @Test
    fun `test network success, confirm cache updated`() = coroutineRule.runBlockingTest {
        var newCache: String? = null
        val ncr = networkCacheResource(
            cacheSource = { getFakeCacheData() },
            networkSource = { getFakeNetworkData() },
            updateCache = { newCache = it }
        )

        val dataResult = ncr.toList().last()

        assert(
            dataResult is Resource.Success &&
            dataResult.data == FAKE_NETWORK_DATA &&
            newCache == FAKE_NETWORK_DATA
        )
    }

    @Test
    fun `test network error, result is cache data`() = coroutineRule.runBlockingTest {
        val ncr = networkCacheResource(
            cacheSource = { getFakeCacheData() },
            networkSource = { getFakeNetworkData(true) },
            updateCache = { }
        )
        val dataResult = ncr.toList().last()

        println(dataResult)

        assert(dataResult is Resource.Success && dataResult.data == FAKE_CACHE_DATA)
    }

    @Suppress("RedundantSuspendModifier")
    private suspend fun getFakeCacheData(): String = FAKE_CACHE_DATA

    private fun getFakeNetworkData(
        hasNetworkError: Boolean = false
    ): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        if (hasNetworkError) {
            emit(Resource.Error("Network error."))
        } else {
            emit(Resource.Success(FAKE_NETWORK_DATA))
        }
    }

    companion object {
        private const val FAKE_CACHE_DATA = "cache data"
        private const val FAKE_NETWORK_DATA = "network data"
    }
}