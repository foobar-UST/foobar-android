package com.foobarust.domain.usecases.cart

import com.foobarust.domain.states.Resource
import com.foobarust.testshared.di.DependencyContainer
import com.foobarust.testshared.repositories.FakeAuthRepositoryImpl
import com.foobarust.testshared.repositories.FakeCartRepositoryImpl
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

class ClearUserCartUseCaseTest {

    private lateinit var clearUserCartUseCase: ClearUserCartUseCase
    private lateinit var fakeAuthRepositoryImpl: FakeAuthRepositoryImpl
    private lateinit var fakeCartRepositoryImpl: FakeCartRepositoryImpl
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

        fakeCartRepositoryImpl = FakeCartRepositoryImpl(
            idToken = dependencyContainer.fakeIdToken,
            defaultUserCart = dependencyContainer.fakeUserCart,
            defaultCartItems = dependencyContainer.fakeUserCartItems
        )

        clearUserCartUseCase = ClearUserCartUseCase(
            authRepository = fakeAuthRepositoryImpl,
            cartRepository = fakeCartRepositoryImpl,
            coroutineDispatcher = coroutineRule.testDispatcher
        )
    }

    @Test
    fun `test clear cart success`() = coroutineRule.runBlockingTest {
        fakeAuthRepositoryImpl.setUserSignedIn(true)
        fakeCartRepositoryImpl.setNetworkError(false)

        val result = clearUserCartUseCase(Unit).toList()
        assert(result.last() is Resource.Success)
    }

    @Test
    fun `test not signed in error`() = coroutineRule.runBlockingTest {
        fakeAuthRepositoryImpl.setUserSignedIn(false)
        fakeCartRepositoryImpl.setNetworkError(false)

        val result = clearUserCartUseCase(Unit).toList()
        assert(result.last() is Resource.Error)
    }

    @Test
    fun `test network error`() = coroutineRule.runBlockingTest {
        fakeAuthRepositoryImpl.setUserSignedIn(true)
        fakeCartRepositoryImpl.setNetworkError(true)

        val result = clearUserCartUseCase(Unit).toList()
        assert(result.last() is Resource.Error)
    }
}