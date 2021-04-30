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
 * Created by kevin on 4/29/21
 */

class SearchSellersUseCaseTest {

    private lateinit var searchSellersUseCase: SearchSellersUseCase
    private lateinit var fakeSellerRepositoryImpl: FakeSellerRepositoryImpl

    @get:Rule
    var coroutineRule = TestCoroutineRule()

    @Before
    fun init() {
        fakeSellerRepositoryImpl = FakeSellerRepositoryImpl()
        searchSellersUseCase = SearchSellersUseCase(
            sellerRepository = fakeSellerRepositoryImpl,
            coroutineDispatcher = coroutineRule.testDispatcher
        )
    }

    @Test
    fun `test search sellers success`() = coroutineRule.runBlockingTest {
        fakeSellerRepositoryImpl.setNetworkError(false)

        val seller = fakeSellerRepositoryImpl.sellerList.random()
        val searchQuery = seller.name.take(3)

        val params = SearchSellersParameters(
            searchQuery = searchQuery,
            numOfSellers = 5
        )
        val result = searchSellersUseCase(params).toList().last()

        val expected = fakeSellerRepositoryImpl.searchSellers(
            searchQuery = searchQuery,
            numOfSellers = 5
        )

        assert(
            result is Resource.Success &&
            result.data == expected
        )
    }

    @Test
    fun `test search sellers empty query`() = coroutineRule.runBlockingTest {
        fakeSellerRepositoryImpl.setNetworkError(false)

        val searchQuery = "\t \t "

        val params = SearchSellersParameters(
            searchQuery = searchQuery,
            numOfSellers = 5
        )
        val result = searchSellersUseCase(params).toList().last()

        assert(
            result is Resource.Success &&
            result.data.isEmpty()
        )
    }

    @Test
    fun `test network error`() = coroutineRule.runBlockingTest {
        fakeSellerRepositoryImpl.setNetworkError(true)

        val seller = fakeSellerRepositoryImpl.sellerList.random()
        val searchQuery = seller.name.take(3)

        val params = SearchSellersParameters(
            searchQuery = searchQuery,
            numOfSellers = 5
        )
        val result = searchSellersUseCase(params).toList().last()

        assert(result is Resource.Error)
    }
}