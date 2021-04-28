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

class GetRelatedSellerSectionsUseCaseTest {

    private lateinit var getRelatedSellerSectionsUseCase: GetRelatedSellerSectionsUseCase
    private lateinit var fakeSellerRepositoryImpl: FakeSellerRepositoryImpl
    private lateinit var dependencyContainer: DependencyContainer

    @get:Rule
    var coroutineRule = TestCoroutineRule()

    @Before
    fun init() {
        dependencyContainer = DependencyContainer()
        fakeSellerRepositoryImpl = FakeSellerRepositoryImpl()
        getRelatedSellerSectionsUseCase = GetRelatedSellerSectionsUseCase(
            sellerRepository = fakeSellerRepositoryImpl,
            coroutineDispatcher = coroutineRule.testDispatcher
        )
    }

    @Test
    fun `test get sections success, exclude current`() = coroutineRule.runBlockingTest {
        fakeSellerRepositoryImpl.setNetworkError(false)

        val currentSectionId = fakeSellerRepositoryImpl.sellerSectionList.first().id
        val params = GetRelatedSellerSectionsParameters(
            sellerId = dependencyContainer.fakeUserCart.sellerId,
            numOfSections = 5,
            currentSectionId = currentSectionId
        )
        val results = getRelatedSellerSectionsUseCase(params).toList()
        val lastResult = results.last()

        assert(
            lastResult is Resource.Success &&
            lastResult.data.find { it.id == currentSectionId } == null
        )
    }

    @Test
    fun `test get sections, network error`() = coroutineRule.runBlockingTest {
        fakeSellerRepositoryImpl.setNetworkError(true)

        val currentSectionId = fakeSellerRepositoryImpl.sellerSectionList.first().id
        val params = GetRelatedSellerSectionsParameters(
            sellerId = dependencyContainer.fakeUserCart.sellerId,
            numOfSections = 5,
            currentSectionId = currentSectionId
        )
        val results = getRelatedSellerSectionsUseCase(params).toList()

        assert(results.last() is Resource.Error)
    }
}