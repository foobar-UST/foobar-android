package com.foobarust.domain.usecases.auth

import com.foobarust.domain.di.DependencyContainer
import com.foobarust.domain.repository.FakeAuthRepositoryImpl
import com.foobarust.domain.states.Resource
import com.foobarust.domain.utils.TestCoroutineRule
import com.foobarust.domain.utils.runBlockingTest
import kotlinx.coroutines.flow.toList
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by kevin on 4/21/21
 */

class RequestAuthEmailUseCaseTest {

    private lateinit var requestAuthEmailUseCase: RequestAuthEmailUseCase
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

        requestAuthEmailUseCase = RequestAuthEmailUseCase(
            authRepository = fakeAuthRepositoryImpl,
            coroutineDispatcher = coroutineRule.testDispatcher
        )
    }

    @Test
    fun `test request success`() = coroutineRule.runBlockingTest {
        fakeAuthRepositoryImpl.setNetworkError(false)
        val results = requestAuthEmailUseCase(FAKE_AUTH_EMAIL).toList()
        assert(results.last() is Resource.Success)
    }

    @Test
    fun `test request failure`() = coroutineRule.runBlockingTest {
        fakeAuthRepositoryImpl.setNetworkError(true)
        val results = requestAuthEmailUseCase(FAKE_AUTH_EMAIL).toList()
        assert(results.last() is Resource.Error)
    }

    companion object {
        private const val FAKE_AUTH_EMAIL = "test@test.com"
    }
}