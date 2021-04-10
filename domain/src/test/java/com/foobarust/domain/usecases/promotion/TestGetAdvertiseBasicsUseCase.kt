package com.foobarust.domain.usecases.promotion

import com.foobarust.domain.models.seller.SellerType
import com.foobarust.domain.repository.FakePromotionRepositoryImpl
import com.foobarust.domain.states.Resource
import com.foobarust.domain.utils.TestCoroutineRule
import com.foobarust.domain.utils.runBlockingTest
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by kevin on 4/9/21
 */

class TestGetAdvertiseBasicsUseCase {

    private lateinit var getAdvertiseBasicsUseCase: GetAdvertiseBasicsUseCase

    private lateinit var fakePromotionRepositoryImpl: FakePromotionRepositoryImpl

    @get:Rule
    var coroutineRule = TestCoroutineRule()

    @Before
    fun init() {
        fakePromotionRepositoryImpl = FakePromotionRepositoryImpl()
        getAdvertiseBasicsUseCase = GetAdvertiseBasicsUseCase(
            promotionRepository = fakePromotionRepositoryImpl,
            coroutineDispatcher = TestCoroutineDispatcher()
        )
    }

    @Test
    fun `get advertise basics, resource success`() = coroutineRule.runBlockingTest {
        val params = GetAdvertiseBasicsParameters(
            sellerType = SellerType.ON_CAMPUS,
            numOfAdvertises = 5
        )
        val expect = fakePromotionRepositoryImpl.advertiseBasicsList.take(5)
        val lastEmit = getAdvertiseBasicsUseCase(params).toList().last()

        assert(lastEmit is Resource.Success && lastEmit.data == expect)
    }

    @Test
    fun `get advertise basics, resource error`() = coroutineRule.runBlockingTest {
        val params = GetAdvertiseBasicsParameters(
            sellerType = SellerType.ON_CAMPUS,
            numOfAdvertises = 5
        )
        fakePromotionRepositoryImpl.setNetworkError(true)

        val lastEmit = getAdvertiseBasicsUseCase(params).toList().last()

        assert(lastEmit is Resource.Error)
    }
}