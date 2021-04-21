package com.foobarust.domain.usecases.auth

import com.foobarust.domain.models.auth.AuthProfile
import com.foobarust.domain.models.user.UserDetail
import com.foobarust.domain.repository.FakeAuthRepositoryImpl
import com.foobarust.domain.repository.FakeUserRepositoryImpl
import com.foobarust.domain.usecases.AuthState
import com.foobarust.domain.utils.TestCoroutineRule
import com.foobarust.domain.utils.coroutineScope
import com.foobarust.domain.utils.runBlockingTest
import com.foobarust.domain.utils.toListUntil
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

/**
 * Created by kevin on 4/21/21
 */

class TestGetUserAuthStateUseCase {

    private lateinit var getUserAuthStateUseCase: GetUserAuthStateUseCase
    private lateinit var fakeAuthRepositoryImpl: FakeAuthRepositoryImpl
    private lateinit var fakeUserRepositoryImpl: FakeUserRepositoryImpl

    @get:Rule
    var coroutineRule = TestCoroutineRule()

    @Before
    fun init() {
        val fakeUserDetail = UserDetail(
            id =  UUID.randomUUID().toString(),
            username = DETAIL_USERNAME,
            email = "test@test.com",
            name = "Hello World",
            phoneNum = "+852 12345678",
            photoUrl = "about:blank",
            updatedAt = Date()
        )
        val fakeIdToken = UUID.randomUUID().toString()

        fakeAuthRepositoryImpl = FakeAuthRepositoryImpl(
            idToken = fakeIdToken,
            defaultAuthProfile = AuthProfile(
                id = UUID.randomUUID().toString(),
                email = "test@test.com",
                username = PROFILE_USERNAME
            ),
            isSignedIn = false
        )

        fakeUserRepositoryImpl = FakeUserRepositoryImpl(
            idToken = fakeIdToken,
            defaultUserDetail = fakeUserDetail,
            hasCompletedTutorial = false
        )

        getUserAuthStateUseCase = GetUserAuthStateUseCase(
            authRepository = fakeAuthRepositoryImpl,
            userRepository = fakeUserRepositoryImpl,
            externalScope = coroutineRule.coroutineScope(),
            coroutineDispatcher = coroutineRule.testDispatcher
        )
    }

    @Test
    fun `test user signed out`() = coroutineRule.runBlockingTest {
        fakeAuthRepositoryImpl.setUserSignedIn(false)
        val results = getUserAuthStateUseCase(Unit).toListUntil { it is AuthState.Unauthenticated }
        assert(results[0] is AuthState.Loading)
        assert(results[1] is AuthState.Unauthenticated)
    }

    @Test
    fun `test network available, user signed in, use user detail`() = runBlocking {
        fakeAuthRepositoryImpl.setUserSignedIn(true)
        fakeUserRepositoryImpl.setNetworkError(false)

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

        val results = getUserAuthStateUseCase(Unit).toListUntil { it is AuthState.Authenticated }

        assert(results[0] is AuthState.Loading)

        val authenticated = results[1]
        assert(
            authenticated is AuthState.Authenticated &&
            authenticated.data.username == PROFILE_USERNAME
        )
    }

    companion object {
        private const val DETAIL_USERNAME = "username_from_detail"
        private const val PROFILE_USERNAME = "username_from_profile"
    }
}