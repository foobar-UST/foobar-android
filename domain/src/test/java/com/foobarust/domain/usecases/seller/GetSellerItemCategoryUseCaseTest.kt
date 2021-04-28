package com.foobarust.domain.usecases.seller

import com.foobarust.domain.repository.FakeSellerRepositoryImpl
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

class GetSellerItemCategoryUseCaseTest {

    private lateinit var getSellerItemCategoryUseCase: GetSellerItemCategoryUseCase
    private lateinit var fakeSellerRepositoryImpl: FakeSellerRepositoryImpl

    @get:Rule
    var coroutineRule = TestCoroutineRule()

    @Before
    fun init() {
        fakeSellerRepositoryImpl = FakeSellerRepositoryImpl()
        getSellerItemCategoryUseCase = GetSellerItemCategoryUseCase(
            sellerRepository = fakeSellerRepositoryImpl,
            coroutineDispatcher = coroutineRule.testDispatcher
        )
    }

    @Test
    fun `test get category success`() = coroutineRule.runBlockingTest {
        fakeSellerRepositoryImpl.setNetworkError(false)

        val tag = fakeSellerRepositoryImpl.itemCategoryList.first().tag
        val results = getSellerItemCategoryUseCase(tag).toList()

        assert(results.last() is Resource.Success)
    }

    @Test
    fun `test network error`() = coroutineRule.runBlockingTest {
        fakeSellerRepositoryImpl.setNetworkError(true)

        val tag = fakeSellerRepositoryImpl.itemCategoryList.first().tag
        val results = getSellerItemCategoryUseCase(tag).toList()

        assert(results.last() is Resource.Error)
    }
}