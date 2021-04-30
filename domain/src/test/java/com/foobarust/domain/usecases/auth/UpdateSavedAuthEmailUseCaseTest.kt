package com.foobarust.domain.usecases.auth

import com.foobarust.domain.states.Resource
import com.foobarust.testshared.di.DependencyContainer
import com.foobarust.testshared.repositories.FakeAuthRepositoryImpl
import com.foobarust.testshared.utils.TestCoroutineRule
import com.foobarust.testshared.utils.coroutineScope
import com.foobarust.testshared.utils.runBlockingTest
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
            isSignedIn = false,
            coroutineScope = coroutineRule.coroutineScope()
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