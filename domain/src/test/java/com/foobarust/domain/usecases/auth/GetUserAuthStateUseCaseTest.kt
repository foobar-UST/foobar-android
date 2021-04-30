package com.foobarust.domain.usecases.auth

import com.foobarust.domain.usecases.AuthState
import com.foobarust.testshared.di.DependencyContainer
import com.foobarust.testshared.repositories.FakeAuthRepositoryImpl
import com.foobarust.testshared.repositories.FakeUserRepositoryImpl
import com.foobarust.testshared.utils.TestCoroutineRule
import com.foobarust.testshared.utils.coroutineScope
import com.foobarust.testshared.utils.runBlockingTest
import com.foobarust.testshared.utils.toListUntil
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by kevin on 4/21/21
 */

class GetUserAuthStateUseCaseTest {

    private lateinit var fakeAuthRepositoryImpl: FakeAuthRepositoryImpl
    private lateinit var fakeUserRepositoryImpl: FakeUserRepositoryImpl

    private lateinit var dependencyContainer: DependencyContainer

    @get:Rule
    var coroutineRule = TestCoroutineRule()

    @Before
    fun init() {
        dependencyContainer = DependencyContainer()

        fakeAuthRepositoryImpl = FakeAuthRepositoryImpl(
            idToken = dependencyContainer.fakeIdToken,
            defaultAuthProfile = dependencyContainer.fakeAuthProfile.copy(
                username = PROFILE_USERNAME
            ),
            isSignedIn = false,
            coroutineScope = coroutineRule.coroutineScope()
        )

        fakeUserRepositoryImpl = FakeUserRepositoryImpl(
            idToken = dependencyContainer.fakeIdToken,
            defaultUserDetail = dependencyContainer.fakeUserDetail.copy(
                username = DETAIL_USERNAME
            ),
            hasCompletedTutorial = false
        )
    }

    @Test
    fun `test user signed out`() = coroutineRule.runBlockingTest {
        fakeAuthRepositoryImpl.setUserSignedIn(false)
        fakeUserRepositoryImpl.setNetworkError(false)

        val getUserAuthStateUseCase = buildGetUserAuthStateUseCase()
        val result = getUserAuthStateUseCase(Unit).toListUntil { it is AuthState.Unauthenticated }.last()

        assert(result is AuthState.Unauthenticated)
    }

    @Test
    fun `test network available, user signed in, use user detail`() = coroutineRule.runBlockingTest {
        fakeAuthRepositoryImpl.setUserSignedIn(true)
        fakeUserRepositoryImpl.setNetworkError(false)

        val getUserAuthStateUseCase = buildGetUserAuthStateUseCase()
        val result = getUserAuthStateUseCase(Unit).toListUntil { it is AuthState.Authenticated }.last()

        assert(
            result is AuthState.Authenticated &&
            result.data.username == DETAIL_USERNAME
        )
    }

    @Test
    fun `test network unavailable, user signed in, use auth profile`() = coroutineRule.runBlockingTest {
        fakeAuthRepositoryImpl.setUserSignedIn(true)
        fakeUserRepositoryImpl.setNetworkError(true)

        val getUserAuthStateUseCase = buildGetUserAuthStateUseCase()
        val result = getUserAuthStateUseCase(Unit).toListUntil { it is AuthState.Authenticated }.last()

        assert(
            result is AuthState.Authenticated &&
            result.data.username == PROFILE_USERNAME
        )
    }

    private fun buildGetUserAuthStateUseCase(): GetUserAuthStateUseCase {
        return GetUserAuthStateUseCase(
            authRepository = fakeAuthRepositoryImpl,
            userRepository = fakeUserRepositoryImpl,
            externalScope = coroutineRule.coroutineScope(),
            coroutineDispatcher = coroutineRule.testDispatcher
        )
    }

    companion object {
        private const val DETAIL_USERNAME = "username_from_detail"
        private const val PROFILE_USERNAME = "username_from_profile"
    }
}