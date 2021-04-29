package com.foobarust.domain.usecases.checkout

import com.foobarust.domain.di.DependencyContainer
import com.foobarust.domain.repository.FakeCheckoutRepositoryImpl
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

class GetPaymentMethodsUseCaseTest {

    private lateinit var getPaymentMethodsUseCase: GetPaymentMethodsUseCase
    private lateinit var fakeCheckoutRepositoryImpl: FakeCheckoutRepositoryImpl
    private lateinit var dependencyContainer: DependencyContainer

    @get:Rule
    var coroutineRule = TestCoroutineRule()

    @Before
    fun init() {
        dependencyContainer = DependencyContainer()
        fakeCheckoutRepositoryImpl = FakeCheckoutRepositoryImpl(dependencyContainer.fakeIdToken)
        getPaymentMethodsUseCase = GetPaymentMethodsUseCase(
            checkoutRepository = fakeCheckoutRepositoryImpl,
            coroutineDispatcher = coroutineRule.testDispatcher
        )
    }

    @Test
    fun `test get success`() = coroutineRule.runBlockingTest {
        fakeCheckoutRepositoryImpl.setNetworkError(false)

        val results = getPaymentMethodsUseCase(Unit).toList()
        assert(results.last() is Resource.Success)
    }

    @Test
    fun `test network error`() = coroutineRule.runBlockingTest {
        fakeCheckoutRepositoryImpl.setNetworkError(true)

        val results = getPaymentMethodsUseCase(Unit).toList()
        assert(results.last() is Resource.Error)
    }
}