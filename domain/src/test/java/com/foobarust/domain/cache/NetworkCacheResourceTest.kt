package com.foobarust.domain.cache

import com.foobarust.domain.states.Resource
import com.foobarust.domain.utils.TestCoroutineRule
import com.foobarust.domain.utils.runBlockingTest
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

    private var fakeCacheData: String = ""

    @Test
    fun `test first result is loading`() = coroutineRule.runBlockingTest {
        val ncr = networkCacheResource(
            cacheSource = { getFakeCacheData() },
            networkSource = { getFakeNetworkData() },
            updateCache = { networkData -> updateFakeCacheData(networkData) }
        )
        val firstResult = ncr.toList().first()

        assert(firstResult is Resource.Loading)
    }

    @Test
    fun `test network success, result is network data`() = coroutineRule.runBlockingTest {
        val ncr = networkCacheResource(
            cacheSource = { getFakeCacheData() },
            networkSource = { getFakeNetworkData() },
            updateCache = { networkData -> updateFakeCacheData(networkData) }
        )
        val dataResult = ncr.toList().last()

        assert(dataResult is Resource.Success && dataResult.data == FAKE_NETWORK_DATA)
    }

    @Test
    fun `test network success, confirm cache updated`() = coroutineRule.runBlockingTest {
        val ncr = networkCacheResource(
            cacheSource = { getFakeCacheData() },
            networkSource = { getFakeNetworkData() },
            updateCache = { networkData -> updateFakeCacheData(networkData) }
        )

        val dataResult = ncr.toList().last()

        assert(
            dataResult is Resource.Success &&
            dataResult.data == FAKE_NETWORK_DATA &&
            fakeCacheData == FAKE_NETWORK_DATA
        )
    }

    @Test
    fun `test network error, result is cache data`() = coroutineRule.runBlockingTest {
        val ncr = networkCacheResource(
            cacheSource = { getFakeCacheData() },
            networkSource = { getFakeNetworkData(true) },
            updateCache = { networkData -> updateFakeCacheData(networkData) }
        )
        val dataResult = ncr.toList().last()

        assert(dataResult is Resource.Success && dataResult.data == FAKE_CACHE_DATA)
    }

    @Suppress("RedundantSuspendModifier")
    private suspend fun getFakeCacheData(): String = fakeCacheData

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

    @Suppress("RedundantSuspendModifier")
    private suspend fun updateFakeCacheData(data: String) {
        fakeCacheData = data
    }

    companion object {
        private const val FAKE_CACHE_DATA = "cache data"
        private const val FAKE_NETWORK_DATA = "network data"
    }
}