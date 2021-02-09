package com.foobarust.domain.usecases.seller

import com.foobarust.domain.models.seller.SellerBasic
import com.foobarust.domain.models.seller.SellerType
import com.foobarust.domain.repository.FakeSellerRepositoryImpl
import com.foobarust.domain.states.Resource
import com.foobarust.domain.utils.TestCoroutineRule
import com.foobarust.domain.utils.runBlockingTest
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

/**
 * Created by kevin on 2/7/21
 */

class TestGetSellerUseCase {

    // System in test
    private lateinit var getSellerUseCase: GetSellerUseCase

    // Dependencies
    private lateinit var sellerRepository: FakeSellerRepositoryImpl

    @get:Rule
    var coroutineRule = TestCoroutineRule()

    @Before
    fun init() {
        sellerRepository = FakeSellerRepositoryImpl()
        getSellerUseCase = GetSellerUseCase(
            sellerRepository = sellerRepository,
            coroutineDispatcher = TestCoroutineDispatcher()
        )
    }

    @Test
    fun `get seller return success`() = coroutineRule.runBlockingTest {
        // Add fake seller
        val seller = provideSeller()

        sellerRepository.removeAllSellers()
        sellerRepository.addSeller(seller)
        sellerRepository.setShouldReturnNetworkError(false)

        // Get seller
        val result = getSellerUseCase(seller.id).toList()

        assert(result.last() == Resource.Success(seller))
    }

    @Test
    fun `get seller return not found`() = coroutineRule.runBlockingTest {
        val sellerId = UUID.randomUUID().toString()

        sellerRepository.removeAllSellers()
        sellerRepository.setShouldReturnNetworkError(false)

        val result = getSellerUseCase(sellerId).toList()

        assert(result.last() is Resource.Error)
    }

    @Test
    fun `get seller return network error`() = coroutineRule.runBlockingTest {
        // Add fake seller
        val seller = provideSeller()

        sellerRepository.removeAllSellers()
        sellerRepository.addSeller(seller)
        sellerRepository.setShouldReturnNetworkError(true)

        val result = getSellerUseCase(seller.id).toList()

        assert(result.last() is Resource.Error)
    }

    private fun provideSeller(): SellerBasic {
        val sellerId = UUID.randomUUID().toString()
        return SellerBasic(
            id = sellerId, name = "Happy Restaurant", nameZh = "餐廳", imageUrl = "about:blank",
            rating = 4.9, type = SellerType.ON_CAMPUS, online = true, minSpend = 10.0,
            tags = listOf("Chinese", "Western")
        )
    }
}