package com.foobarust.domain.usecases.seller

import com.foobarust.domain.repository.FakeSellerRepositoryImpl
import com.foobarust.domain.serialize.toSellerItemBasic
import com.foobarust.domain.states.Resource
import com.foobarust.domain.utils.TestCoroutineRule
import com.foobarust.domain.utils.runBlockingTest
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

    @get:Rule
    var coroutineRule = TestCoroutineRule()

    @Before
    fun init() {
        fakeSellerRepositoryImpl = FakeSellerRepositoryImpl()
        getSuggestedItemsUseCase = GetSuggestedItemsUseCase(
            sellerRepository = fakeSellerRepositoryImpl,
            coroutineDispatcher = coroutineRule.testDispatcher
        )
    }

    @Test
    fun `test get items success, ignore current`() = coroutineRule.runBlockingTest {
        fakeSellerRepositoryImpl.setNetworkError(false)

        val firstItem = fakeSellerRepositoryImpl.sellerItemList.first()
        val lastItem = fakeSellerRepositoryImpl.sellerItemList.last()

        val params = GetSuggestedItemsParameters(
            sellerId = firstItem.seller_id,
            ignoreItemId = lastItem.id,
            numOfItems = Int.MAX_VALUE
        )
        val firstResult = getSuggestedItemsUseCase(params).toList().last()
        val secondResult = getSuggestedItemsUseCase(params).toList().last()

        assert(
            firstResult is Resource.Success &&
            secondResult is Resource.Success &&
            firstResult.data.containsAll(secondResult.data) &&
            lastItem.toSellerItemBasic() !in firstResult.data &&
            lastItem.toSellerItemBasic() !in secondResult.data
        )
    }

    @Test
    fun `test network error`() = coroutineRule.runBlockingTest {
        fakeSellerRepositoryImpl.setNetworkError(true)
    }
}