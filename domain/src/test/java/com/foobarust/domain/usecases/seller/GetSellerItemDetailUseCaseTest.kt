package com.foobarust.domain.usecases.seller

import com.foobarust.domain.repository.FakeSellerRepositoryImpl
import com.foobarust.domain.states.Resource
import com.foobarust.domain.utils.TestCoroutineRule
import com.foobarust.domain.utils.runBlockingTest
import di.DependencyContainer
import kotlinx.coroutines.flow.toList
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by kevin on 4/28/21
 */

class GetSellerItemDetailUseCaseTest {

    private lateinit var getSellerItemDetailUseCase: GetSellerItemDetailUseCase
    private lateinit var fakeSellerRepositoryImpl: FakeSellerRepositoryImpl
    private lateinit var dependencyContainer: DependencyContainer

    @get:Rule
    var coroutineRule = TestCoroutineRule()

    @Before
    fun init() {
        dependencyContainer = DependencyContainer()
        fakeSellerRepositoryImpl = FakeSellerRepositoryImpl()
        getSellerItemDetailUseCase = GetSellerItemDetailUseCase(
            sellerRepository = fakeSellerRepositoryImpl,
            coroutineDispatcher = coroutineRule.testDispatcher
        )
    }

    @Test
    fun `test get item detail success`() = coroutineRule.runBlockingTest {
        fakeSellerRepositoryImpl.setNetworkError(false)

        val itemId = dependencyContainer.fakeUserCartItems.first().itemId
        val results = getSellerItemDetailUseCase(itemId).toList()

        assert(results.last() is Resource.Success)
    }

    @Test
    fun `test network error`() = coroutineRule.runBlockingTest {
        fakeSellerRepositoryImpl.setNetworkError(true)

        val itemId = dependencyContainer.fakeUserCartItems.first().itemId
        val results = getSellerItemDetailUseCase(itemId).toList()

        assert(results.last() is Resource.Error)
    }
}