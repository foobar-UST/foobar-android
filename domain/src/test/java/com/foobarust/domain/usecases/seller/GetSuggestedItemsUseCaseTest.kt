package com.foobarust.domain.usecases.seller

import com.foobarust.domain.states.Resource
import com.foobarust.testshared.di.DependencyContainer
import com.foobarust.testshared.repositories.FakeSellerRepositoryImpl
import com.foobarust.testshared.serialize.toSellerItemBasic
import com.foobarust.testshared.utils.TestCoroutineRule
import com.foobarust.testshared.utils.runBlockingTest
import kotlinx.coroutines.flow.toList
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by kevin on 4/29/21
 */

class GetSuggestedItemsUseCaseTest {

    private lateinit var getSuggestedItemsUseCase: GetSuggestedItemsUseCase
    private lateinit var fakeSellerRepositoryImpl: FakeSellerRepositoryImpl
    private lateinit var dependencyContainer: DependencyContainer

    @get:Rule
    var coroutineRule = TestCoroutineRule()

    @Before
    fun init() {
        dependencyContainer = DependencyContainer()
        fakeSellerRepositoryImpl = FakeSellerRepositoryImpl()
        getSuggestedItemsUseCase = GetSuggestedItemsUseCase(
            sellerRepository = fakeSellerRepositoryImpl,
            coroutineDispatcher = coroutineRule.testDispatcher
        )
    }

    @Test
    fun `test get items success, ignore current`() = coroutineRule.runBlockingTest {
        fakeSellerRepositoryImpl.setNetworkError(false)

        val currentItem = fakeSellerRepositoryImpl.sellerItemList.first().toSellerItemBasic()

        val params = GetSuggestedItemsParameters(
            sellerId = dependencyContainer.fakeUserCart.sellerId,
            ignoreItemId = currentItem.id,
            numOfItems = Int.MAX_VALUE
        )

        val result = getSuggestedItemsUseCase(params).toList().last()

        assert(
            result is Resource.Success &&
            currentItem !in result.data
        )
    }

    @Test
    fun `test network error`() = coroutineRule.runBlockingTest {
        fakeSellerRepositoryImpl.setNetworkError(true)

        val currentItem = fakeSellerRepositoryImpl.sellerItemList.first().toSellerItemBasic()

        val params = GetSuggestedItemsParameters(
            sellerId = dependencyContainer.fakeUserCart.sellerId,
            ignoreItemId = currentItem.id,
            numOfItems = Int.MAX_VALUE
        )

        val result = getSuggestedItemsUseCase(params).toList().last()

        assert(result is Resource.Error)
    }
}