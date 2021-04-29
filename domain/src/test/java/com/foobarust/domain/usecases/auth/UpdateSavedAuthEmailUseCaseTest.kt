package com.foobarust.domain.usecases.auth

import com.foobarust.domain.di.DependencyContainer
import com.foobarust.domain.repository.FakeAuthRepositoryImpl
import com.foobarust.domain.states.Resource
import com.foobarust.domain.utils.TestCoroutineRule
import com.foobarust.domain.utils.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by kevin on 4/28/21
 */

class UpdateSavedAuthEmailUseCaseTest {

    private lateinit var updateSavedAuthEmailUseCase: UpdateSavedAuthEmailUseCase
    private lateinit var fakeAuthRepositoryImpl: FakeAuthRepositoryImpl

    private lateinit var dependencyContainer: DependencyContainer

    @get:Rule
    var coroutineRule = TestCoroutineRule()

    @Before
    fun init() {
        dependencyContainer = DependencyContainer()

        fakeAuthRepositoryImpl = FakeAuthRepositoryImpl(
            idToken = dependencyContainer.fakeIdToken,
            defaultAuthProfile = dependencyContainer.fakeAuthProfile,
            isSignedIn = false
        )

        updateSavedAuthEmailUseCase = UpdateSavedAuthEmailUseCase(
            authRepository = fakeAuthRepositoryImpl,
            coroutineDispatcher = coroutineRule.testDispatcher
        )
    }

    @Test
    fun `test update saved auth email success`() = coroutineRule.runBlockingTest {
        fakeAuthRepositoryImpl.setIOError(false)
        val email = "test@test.com"
        val result = updateSavedAuthEmailUseCase(email)
        assert(result is Resource.Success)
    }

    @Test
    fun `test update saved auth email error`() = coroutineRule.runBlockingTest {
        fakeAuthRepositoryImpl.setIOError(true)
        val email = "test@test.com"
        val result = updateSavedAuthEmailUseCase(email)
        assert(result is Resource.Error)
    }
}