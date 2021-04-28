package com.foobarust.domain.usecases.maps

import com.foobarust.domain.models.common.GeolocationPoint
import com.foobarust.domain.models.map.TravelMode
import com.foobarust.domain.repository.FakeMapRepositoryImpl
import com.foobarust.domain.states.Resource
import com.foobarust.domain.utils.TestCoroutineRule
import com.foobarust.domain.utils.runBlockingTest
import kotlinx.coroutines.flow.toList
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by kevin on 4/28/21
 */

class GetDirectionsUseCaseTest {

    private lateinit var getDirectionsUseCase: GetDirectionsUseCase
    private lateinit var fakeMapRepositoryImpl: FakeMapRepositoryImpl

    @get:Rule
    var coroutineRule = TestCoroutineRule()

    @Before
    fun init() {
        fakeMapRepositoryImpl = FakeMapRepositoryImpl()
        getDirectionsUseCase = GetDirectionsUseCase(
            mapRepository = fakeMapRepositoryImpl,
            coroutineDispatcher = coroutineRule.testDispatcher
        )
    }

    @Test
    fun `test get directions success`() = coroutineRule.runBlockingTest {
        fakeMapRepositoryImpl.setNetworkError(false)

        val params = GetDirectionsParameters(
            currentLocation = GeolocationPoint(1.0, 2.0),
            destination = GeolocationPoint(3.0, 4.0),
            travelMode = TravelMode.DRIVING
        )
        val results = getDirectionsUseCase(params).toList()

        assert(results.last() is Resource.Success)
    }

    @Test
    fun `test network error`() = coroutineRule.runBlockingTest {
        fakeMapRepositoryImpl.setNetworkError(true)

        val params = GetDirectionsParameters(
            currentLocation = GeolocationPoint(1.0, 2.0),
            destination = GeolocationPoint(3.0, 4.0),
            travelMode = TravelMode.DRIVING
        )
        val results = getDirectionsUseCase(params).toList()

        assert(results.last() is Resource.Error)
    }
}