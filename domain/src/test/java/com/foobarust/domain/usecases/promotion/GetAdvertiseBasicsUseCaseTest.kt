package com.foobarust.domain.usecases.promotion

import com.foobarust.domain.models.seller.SellerType
import com.foobarust.domain.states.Resource
import com.foobarust.testshared.repositories.FakePromotionRepositoryImpl
import com.foobarust.testshared.utils.TestCoroutineRule
import com.foobarust.testshared.utils.runBlockingTest
import kotlinx.coroutines.flow.toList
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by kevin on 4/9/21
 */

class GetAdvertiseBasicsUseCaseTest {

    private lateinit var getAdvertiseBasicsUseCase: GetAdvertiseBasicsUseCase
    private lateinit var fakePromotionRepositoryImpl: FakePromotionRepositoryImpl

    @get:Rule
    var coroutineRule = TestCoroutineRule()

    @Before
    fun init() {
        fakePromotionRepositoryImpl = FakePromotionRepositoryImpl()
        getAdvertiseBasicsUseCase = GetAdvertiseBasicsUseCase(
            promotionRepository = fakePromotionRepositoryImpl,
            coroutineDispatcher = coroutineRule.testDispatcher
        )
    }

    @Test
    fun `get advertise basics, resource success`() = coroutineRule.runBlockingTest {
        fakePromotionRepositoryImpl.setNetworkError(false)

        val params = GetAdvertiseBasicsParameters(
            sellerType = SellerType.ON_CAMPUS,
            numOfAdvertises = 5
        )
        val results = getAdvertiseBasicsUseCase(params).toList()

        assert(results.last() is Resource.Success)
    }

    @Test
    fun `get advertise basics, resource error`() = coroutineRule.runBlockingTest {
        fakePromotionRepositoryImpl.setNetworkError(true)

        val params = GetAdvertiseBasicsParameters(
            sellerType = SellerType.ON_CAMPUS,
            numOfAdvertises = 5
        )
        val results = getAdvertiseBasicsUseCase(params).toList()

        assert(results.last() is Resource.Error)
    }
}