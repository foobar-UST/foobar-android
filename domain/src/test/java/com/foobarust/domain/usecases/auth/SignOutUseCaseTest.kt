package com.foobarust.domain.usecases.auth

import com.foobarust.domain.states.Resource
import com.foobarust.testshared.di.DependencyContainer
import com.foobarust.testshared.repositories.FakeAuthRepositoryImpl
import com.foobarust.testshared.repositories.FakeMessagingRepositoryImpl
import com.foobarust.testshared.repositories.FakeOrderRepositoryImpl
import com.foobarust.testshared.repositories.FakeUserRepositoryImpl
import com.foobarust.testshared.utils.TestCoroutineRule
import com.foobarust.testshared.utils.coroutineScope
import com.foobarust.testshared.utils.runBlockingTest
import kotlinx.coroutines.flow.toList
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by kevin on 4/21/21
 */

class SignOutUseCaseTest {

    private lateinit var signOutUseCase: SignOutUseCase
    private lateinit var fakeAuthRepositoryImpl: FakeAuthRepositoryImpl
    private lateinit var fakeUserRepositoryImpl: FakeUserRepositoryImpl
    private lateinit var fakeMessagingRepositoryImpl: FakeMessagingRepositoryImpl
    private lateinit var fakeOrderRepositoryImpl: FakeOrderRepositoryImpl

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

        fakeUserRepositoryImpl = FakeUserRepositoryImpl(
            idToken = dependencyContainer.fakeIdToken,
            defaultUserDetail = dependencyContainer.fakeUserDetail,
            hasCompletedTutorial = true
        )

        fakeMessagingRepositoryImpl = FakeMessagingRepositoryImpl(
            idToken = dependencyContainer.fakeIdToken
        )

        fakeOrderRepositoryImpl = FakeOrderRepositoryImpl()

        signOutUseCase = SignOutUseCase(
            authRepository = fakeAuthRepositoryImpl,
            userRepository = fakeUserRepositoryImpl,
            messagingRepository = fakeMessagingRepositoryImpl,
            orderRepository = fakeOrderRepositoryImpl,
            coroutineDispatcher = coroutineRule.testDispatcher
        )
    }

    @Test
    fun `test remove user detail cache error`() = coroutineRule.runBlockingTest {
        fakeUserRepositoryImpl.setIOError(true)
        val results = signOutUseCase(Unit).toList()
        assert(results.last() is Resource.Error)
    }

    @Test
    fun `test remove order cache error`() = coroutineRule.runBlockingTest {
        fakeUserRepositoryImpl.setIOError(false)
        fakeOrderRepositoryImpl
    }

    @Test
    fun `test unlink device token error`() = coroutineRule.runBlockingTest {
        fakeUserRepositoryImpl.setIOError(false)
        fakeAuthRepositoryImpl.setUserSignedIn(false)
        val results = signOutUseCase(Unit).toList()
        assert(results.last() is Resource.Error)
    }

    @Test
    fun `test sign out error`() = coroutineRule.runBlockingTest {

    }
}