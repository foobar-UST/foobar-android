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
 * Created by kevin on 4/21/21
 */

class RemoveSavedAuthEmailUseCaseTest {

    private lateinit var removeSavedAuthEmailUseCase: RemoveSavedAuthEmailUseCase
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

        removeSavedAuthEmailUseCase = RemoveSavedAuthEmailUseCase(
            authRepository = fakeAuthRepositoryImpl,
            coroutineDispatcher = coroutineRule.testDispatcher
        )
    }

    @Test
    fun `test remove saved auth email success`() = coroutineRule.runBlockingTest {
        fakeAuthRepositoryImpl.setIOError(false)
        val result = removeSavedAuthEmailUseCase(Unit)
        assert(result is Resource.Success)
    }

    @Test
    fun `test remove saved auth email error`() = coroutineRule.runBlockingTest {
        fakeAuthRepositoryImpl.setIOError(true)
        val result = removeSavedAuthEmailUseCase(Unit)
        assert(result is Resource.Error)
    }
}