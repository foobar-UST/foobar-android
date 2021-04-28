package com.foobarust.domain.usecases.cart

import com.foobarust.domain.states.Resource
import com.foobarust.domain.utils.TestCoroutineRule
import com.foobarust.domain.utils.runBlockingTest
import di.DependencyContainer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by kevin on 4/28/21
 */

class CheckCartTimeoutUseCaseTest {

    private lateinit var checkCartTimeoutUseCase: CheckCartTimeoutUseCase
    private lateinit var dependencyContainer: DependencyContainer

    @get:Rule
    var coroutineRule = TestCoroutineRule()

    @Before
    fun init() {
        dependencyContainer = DependencyContainer()
        checkCartTimeoutUseCase = CheckCartTimeoutUseCase(
            coroutineDispatcher = coroutineRule.testDispatcher
        )
    }

    @Test
    fun `test cart is not timeout`() = coroutineRule.runBlockingTest {
        val userCart = dependencyContainer.fakeUserCart.copy(
            updatedAt = Date(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(5))
        )
        val params = CheckCartTimeoutParameters(
            userCart = userCart,
            timeoutMills = CART_TIMEOUT
        )
        val result = checkCartTimeoutUseCase(params)
        assert(result is Resource.Success && !result.data)
    }

    @Test
    fun `test cart is timeout`() = coroutineRule.runBlockingTest {
        val userCart = dependencyContainer.fakeUserCart.copy(
            updatedAt = Date(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(40))
        )
        val params = CheckCartTimeoutParameters(
            userCart = userCart,
            timeoutMills = CART_TIMEOUT
        )
        val result = checkCartTimeoutUseCase(params)
        assert(result is Resource.Success && result.data)
    }

    @Test
    fun `test cart is empty`() = coroutineRule.runBlockingTest {
        val userCart = dependencyContainer.fakeUserCart.copy(itemsCount = 0)
        val params = CheckCartTimeoutParameters(
            userCart = userCart,
            timeoutMills = CART_TIMEOUT
        )
        val result = checkCartTimeoutUseCase(params)
        assert(result is Resource.Success && !result.data)
    }

    @Test
    fun `test cart updated at null`() = coroutineRule.runBlockingTest {
        val userCart = dependencyContainer.fakeUserCart.copy(updatedAt = null)
        val params = CheckCartTimeoutParameters(
            userCart = userCart,
            timeoutMills = CART_TIMEOUT
        )
        val result = checkCartTimeoutUseCase(params)
        assert(result is Resource.Error)
    }

    companion object {
        private val CART_TIMEOUT = TimeUnit.MINUTES.toMillis(30)
    }
}