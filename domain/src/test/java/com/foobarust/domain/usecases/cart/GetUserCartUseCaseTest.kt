package com.foobarust.domain.usecases.cart

import com.foobarust.domain.states.Resource
import com.foobarust.domain.utils.toListUntil
import com.foobarust.testshared.di.DependencyContainer
import com.foobarust.testshared.repositories.FakeAuthRepositoryImpl
import com.foobarust.testshared.repositories.FakeCartRepositoryImpl
import com.foobarust.testshared.utils.TestCoroutineRule
import com.foobarust.testshared.utils.coroutineScope
import com.foobarust.testshared.utils.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by kevin on 4/28/21
 */

class GetUserCartUseCaseTest {

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
    }

    @Test
    fun `test user signed out`() = coroutineRule.runBlockingTest {
        fakeAuthRepositoryImpl.setUserSignedIn(false)
        fakeCartRepositoryImpl.setNetworkError(false)

        val getUserCartUseCase = buildGetUserCartUseCase()
        val results = getUserCartUseCase(Unit).toListUntil { it is Resource.Error }

        assert(results.last() is Resource.Error)
    }

    @Test
    fun `test get cart success`() = coroutineRule.runBlockingTest {
        fakeAuthRepositoryImpl.setUserSignedIn(true)
        fakeCartRepositoryImpl.setNetworkError(false)

        val getUserCartUseCase = buildGetUserCartUseCase()
        val results = getUserCartUseCase(Unit).toListUntil { it is Resource.Success }

        assert(results.last() is Resource.Success)
    }

    @Test
    fun `test network unavailable, get cart error`() = coroutineRule.runBlockingTest {
        fakeAuthRepositoryImpl.setUserSignedIn(true)
        fakeCartRepositoryImpl.setNetworkError(true)

        val getUserCartUseCase = buildGetUserCartUseCase()
        val results = getUserCartUseCase(Unit).toListUntil { it is Resource.Error }

        assert(results.last() is Resource.Error)
    }

    @Test
    fun `test user signed out, get cart error`() = coroutineRule.runBlockingTest {
        fakeAuthRepositoryImpl.setUserSignedIn(false)
        fakeCartRepositoryImpl.setNetworkError(false)

        val getUserCartUseCase = buildGetUserCartUseCase()
        val results = getUserCartUseCase(Unit).toListUntil { it is Resource.Error }

        assert(results.last() is Resource.Error)
    }

    private fun buildGetUserCartUseCase(): GetUserCartUseCase {
        return GetUserCartUseCase(
            authRepository = fakeAuthRepositoryImpl,
            cartRepository = fakeCartRepositoryImpl,
            coroutineDispatcher = coroutineRule.testDispatcher
        )
    }
}