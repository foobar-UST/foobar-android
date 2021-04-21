package com.foobarust.domain.usecases.auth

import com.foobarust.domain.models.auth.AuthProfile
import com.foobarust.domain.repository.FakeAuthRepositoryImpl
import com.foobarust.domain.states.Resource
import com.foobarust.domain.utils.TestCoroutineRule
import com.foobarust.domain.utils.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

/**
 * Created by kevin on 4/9/21
 */

class TestGetIsUserSignedInUseCase {

    private lateinit var getIsUserSignedInUseCase: GetIsUserSignedInUseCase
    private lateinit var fakeAuthRepositoryImpl: FakeAuthRepositoryImpl

    @get:Rule
    var coroutineRule = TestCoroutineRule()

    @Before
    fun init() {
        fakeAuthRepositoryImpl = FakeAuthRepositoryImpl(
            idToken = UUID.randomUUID().toString(),
            defaultAuthProfile = AuthProfile(
                id = UUID.randomUUID().toString(),
                email = "test@test.com",
                username = "hello_world"
            ),
            isSignedIn = false
        )

        getIsUserSignedInUseCase = GetIsUserSignedInUseCase(
            authRepository = fakeAuthRepositoryImpl,
            coroutineDispatcher = coroutineRule.testDispatcher
        )
    }

    @Test
    fun `test user signed in`() = coroutineRule.runBlockingTest {
        fakeAuthRepositoryImpl.setUserSignedIn(true)
        val result = getIsUserSignedInUseCase(Unit)
        assert(result is Resource.Success && result.data)
    }

    @Test
    fun `test user signed out`() = coroutineRule.runBlockingTest {
        fakeAuthRepositoryImpl.setUserSignedIn(false)
        val result = getIsUserSignedInUseCase(Unit)
        assert(result is Resource.Success && !result.data)
    }
}