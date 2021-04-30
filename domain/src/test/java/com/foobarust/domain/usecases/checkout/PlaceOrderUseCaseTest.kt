package com.foobarust.domain.usecases.checkout

import com.foobarust.domain.states.Resource
import com.foobarust.testshared.di.DependencyContainer
import com.foobarust.testshared.repositories.FakeAuthRepositoryImpl
import com.foobarust.testshared.repositories.FakeCheckoutRepositoryImpl
import com.foobarust.testshared.utils.TestCoroutineRule
import com.foobarust.testshared.utils.coroutineScope
import com.foobarust.testshared.utils.runBlockingTest
import kotlinx.coroutines.flow.toList
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by kevin on 4/28/21
 */

class PlaceOrderUseCaseTest {

    private lateinit var placeOrderUseCase: PlaceOrderUseCase
    private lateinit var fakeAuthRepositoryImpl: FakeAuthRepositoryImpl
    private lateinit var fakeCheckoutRepositoryImpl: FakeCheckoutRepositoryImpl
    private lateinit var dependencyContainer: DependencyContainer

    @get:Rule
    var coroutineRule = TestCoroutineRule()

    @Before
    fun init() {
        dependencyContainer = DependencyContainer()

        fakeAuthRepositoryImpl = FakeAuthRepositoryImpl(
            idToken = dependencyContainer.fakeIdToken,
            defaultAuthProfile = dependencyContainer.fakeAuthProfile,
            isSignedIn = true,
            coroutineScope = coroutineRule.coroutineScope()
        )

        fakeCheckoutRepositoryImpl = FakeCheckoutRepositoryImpl(dependencyContainer.fakeIdToken)

        placeOrderUseCase = PlaceOrderUseCase(
            authRepository = fakeAuthRepositoryImpl,
            checkoutRepository = fakeCheckoutRepositoryImpl,
            coroutineDispatcher = coroutineRule.testDispatcher
        )
    }

    @Test
    fun `test place order success`() = coroutineRule.runBlockingTest {
        fakeAuthRepositoryImpl.setUserSignedIn(true)
        fakeCheckoutRepositoryImpl.setNetworkError(false)

        val testIdentifier = fakeCheckoutRepositoryImpl.fakePaymentMethods.first().identifier
        val params = PlaceOrderParameters(
            orderMessage = "test message",
            paymentMethodIdentifier = testIdentifier
        )
        val results = placeOrderUseCase(params).toList()

        assert(results.last() is Resource.Success)
    }

    @Test
    fun `test not signed in`() = coroutineRule.runBlockingTest {
        fakeAuthRepositoryImpl.setUserSignedIn(false)
        fakeCheckoutRepositoryImpl.setNetworkError(true)

        val testIdentifier = fakeCheckoutRepositoryImpl.fakePaymentMethods.first().identifier
        val params = PlaceOrderParameters(
            orderMessage = "test message",
            paymentMethodIdentifier = testIdentifier
        )
        val results = placeOrderUseCase(params).toList()

        assert(results.last() is Resource.Error)
    }

    @Test
    fun `test network error`() = coroutineRule.runBlockingTest {
        fakeAuthRepositoryImpl.setUserSignedIn(true)
        fakeCheckoutRepositoryImpl.setNetworkError(true)

        val testIdentifier = fakeCheckoutRepositoryImpl.fakePaymentMethods.first().identifier
        val params = PlaceOrderParameters(
            orderMessage = "test message",
            paymentMethodIdentifier = testIdentifier
        )
        val results = placeOrderUseCase(params).toList()

        assert(results.last() is Resource.Error)
    }
}