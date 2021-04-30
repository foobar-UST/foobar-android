package com.foobarust.domain.usecases.seller

import com.foobarust.domain.states.Resource
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

class GetSellerItemCategoriesUseCaseTest {

    private lateinit var getSellerItemCategoriesUseCase: GetSellerItemCategoriesUseCase
    private lateinit var fakeSellerRepositoryImpl: FakeSellerRepositoryImpl

    @get:Rule
    var coroutineRule = TestCoroutineRule()

    @Before
    fun init() {
        fakeSellerRepositoryImpl = FakeSellerRepositoryImpl()
        getSellerItemCategoriesUseCase = GetSellerItemCategoriesUseCase(
            sellerRepository = fakeSellerRepositoryImpl,
            coroutineDispatcher = coroutineRule.testDispatcher
        )
    }

    @Test
    fun `test get categories success`() = coroutineRule.runBlockingTest {
        fakeSellerRepositoryImpl.setNetworkError(false)
        val results = getSellerItemCategoriesUseCase(Unit).toList()
        assert(results.last() is Resource.Success)
    }

    @Test
    fun `test network error`() = coroutineRule.runBlockingTest {
        fakeSellerRepositoryImpl.setNetworkError(true)
        val results = getSellerItemCategoriesUseCase(Unit).toList()
        assert(results.last() is Resource.Error)
    }
}