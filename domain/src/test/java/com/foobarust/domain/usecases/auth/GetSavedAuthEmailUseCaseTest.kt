package com.foobarust.domain.usecases.auth

import com.foobarust.domain.repository.FakeAuthRepositoryImpl
import com.foobarust.domain.states.Resource
import com.foobarust.domain.utils.TestCoroutineRule
import com.foobarust.domain.utils.runBlockingTest
import di.DependencyContainer
import kotlinx.coroutines.flow.toList
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by kevin on 4/21/21
 */

class GetSavedAuthEmailUseCaseTest {

    private lateinit var getSavedAuthEmailUseCase: GetSavedAuthEmailUseCase
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

        getSavedAuthEmailUseCase = GetSavedAuthEmailUseCase(
            authRepository = fakeAuthRepositoryImpl,
            coroutineDispatcher = coroutineRule.testDispatcher
        )
    }

    @Test
    fun `test get saved auth email success`() = coroutineRule.runBlockingTest {
        fakeAuthRepositoryImpl.setIOError(false)
        fakeAuthRepositoryImpl.updateSavedAuthEmail(FAKE_AUTH_EMAIL)
        val result = getSavedAuthEmailUseCase(Unit).toList().last()
        assert(result is Resource.Success && result.data == FAKE_AUTH_EMAIL)
    }

    @Test
    fun `test get saved auth email failed`() = coroutineRule.runBlockingTest {
        fakeAuthRepositoryImpl.setIOError(true)
        val result = getSavedAuthEmailUseCase(Unit).toList().last()
        assert(result is Resource.Error)
    }

    companion object {
        private const val FAKE_AUTH_EMAIL = "test@test.com"
    }
}