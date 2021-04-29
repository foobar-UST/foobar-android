package com.foobarust.domain.usecases.auth

import com.foobarust.domain.di.DependencyContainer
import com.foobarust.domain.repository.FakeAuthRepositoryImpl
import com.foobarust.domain.repository.FakeUserRepositoryImpl
import com.foobarust.domain.usecases.AuthState
import com.foobarust.domain.utils.TestCoroutineRule
import com.foobarust.domain.utils.coroutineScope
import com.foobarust.domain.utils.toListUntil
import kotlinx.coroutines.runBlocking
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
            isSignedIn = false
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
    fun `test user signed out`() = runBlocking {
        fakeAuthRepositoryImpl.setUserSignedIn(false)
        fakeUserRepositoryImpl.setNetworkError(false)

        val getUserAuthStateUseCase = buildGetUserAuthStateUseCase()
        val results = getUserAuthStateUseCase(Unit).toListUntil { it is AuthState.Unauthenticated }

        assert(results.last() is AuthState.Unauthenticated)
    }

    @Test
    fun `test network available, user signed in, use user detail`() = runBlocking {
        fakeAuthRepositoryImpl.setUserSignedIn(true)
        fakeUserRepositoryImpl.setNetworkError(false)

        val getUserAuthStateUseCase = buildGetUserAuthStateUseCase()
        val results = getUserAuthStateUseCase(Unit).toListUntil { it is AuthState.Authenticated }

        assert(results[0] is AuthState.Loading)

        val authenticated = results[1]
        assert(
            authenticated is AuthState.Authenticated &&
            authenticated.data.username == DETAIL_USERNAME
        )
    }

    @Test
    fun `test network unavailable, user signed in, use auth profile`() = runBlocking {
        fakeAuthRepositoryImpl.setUserSignedIn(true)
        fakeUserRepositoryImpl.setNetworkError(true)

        val getUserAuthStateUseCase = buildGetUserAuthStateUseCase()
        val results = getUserAuthStateUseCase(Unit).toListUntil { it is AuthState.Authenticated }

        assert(results[0] is AuthState.Loading)

        val authenticated = results[1]
        assert(
            authenticated is AuthState.Authenticated &&
            authenticated.data.username == PROFILE_USERNAME
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