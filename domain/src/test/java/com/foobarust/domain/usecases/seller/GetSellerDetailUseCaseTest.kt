package com.foobarust.domain.usecases.seller

import com.foobarust.domain.states.Resource
import com.foobarust.testshared.di.DependencyContainer
import com.foobarust.testshared.repositories.FakeSellerRepositoryImpl
import com.foobarust.testshared.utils.TestCoroutineRule
import com.foobarust.testshared.utils.runBlockingTest
import kotlinx.coroutines.flow.toList
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by kevin on 4/28/21
 */

class GetSellerDetailUseCaseTest {

    private lateinit var getSellerDetailUseCase: GetSellerDetailUseCase
    private lateinit var fakeSellerRepositoryImpl: FakeSellerRepositoryImpl
    private lateinit var dependencyContainer: DependencyContainer

    @get:Rule
    var coroutineRule = TestCoroutineRule()

    @Before
    fun init() {
        dependencyContainer = DependencyContainer()
        fakeSellerRepositoryImpl = FakeSellerRepositoryImpl()
        getSellerDetailUseCase = GetSellerDetailUseCase(
            sellerRepository = fakeSellerRepositoryImpl,
            coroutineDispatcher = coroutineRule.testDispatcher
        )
    }

    @Test
    fun `test get seller detail success`() = coroutineRule.runBlockingTest {
        fakeSellerRepositoryImpl.setNetworkError(false)

        val sellerId = dependencyContainer.fakeUserCart.sellerId
        val results = getSellerDetailUseCase(sellerId).toList()
        val lastResult = results.last()

        val expected = fakeSellerRepositoryImpl.getSellerDetail(sellerId)

        assert(
            lastResult is Resource.Success &&
            lastResult.data == expected
        )
    }

    @Test
    fun `test network error`() = coroutineRule.runBlockingTest {
        fakeSellerRepositoryImpl.setNetworkError(true)

        val sellerId = dependencyContainer.fakeUserCart.sellerId
        val results = getSellerDetailUseCase(sellerId).toList()

        assert(results.last() is Resource.Error)
    }
}