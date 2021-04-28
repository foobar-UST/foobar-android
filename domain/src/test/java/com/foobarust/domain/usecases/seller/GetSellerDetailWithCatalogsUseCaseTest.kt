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

class GetSellerDetailWithCatalogsUseCaseTest {

    private lateinit var getSellerDetailWithCatalogsUseCase: GetSellerDetailWithCatalogsUseCase
    private lateinit var fakeSellerRepositoryImpl: FakeSellerRepositoryImpl
    private lateinit var dependencyContainer: DependencyContainer

    @get:Rule
    var coroutineRule = TestCoroutineRule()

    @Before
    fun init() {
        dependencyContainer = DependencyContainer()
        fakeSellerRepositoryImpl = FakeSellerRepositoryImpl()
        getSellerDetailWithCatalogsUseCase = GetSellerDetailWithCatalogsUseCase(
            sellerRepository = fakeSellerRepositoryImpl,
            coroutineDispatcher = coroutineRule.testDispatcher
        )
    }

    @Test
    fun `test get seller detail with catalogs success`() = coroutineRule.runBlockingTest {
        fakeSellerRepositoryImpl.setNetworkError(false)

        val sellerId = dependencyContainer.fakeUserCart.sellerId
        val results = getSellerDetailWithCatalogsUseCase(sellerId).toList()
        val lastResult = results.last()

        val expectedDetail = fakeSellerRepositoryImpl.getSellerDetail(sellerId)

        assert(
            lastResult is Resource.Success &&
            lastResult.data.sellerDetail == expectedDetail
        )
    }

    @Test
    fun `test network error`() = coroutineRule.runBlockingTest {
        fakeSellerRepositoryImpl.setNetworkError(true)

        val sellerId = dependencyContainer.fakeUserCart.sellerId
        val results = getSellerDetailWithCatalogsUseCase(sellerId).toList()

        assert(results.last() is Resource.Error)
    }
}