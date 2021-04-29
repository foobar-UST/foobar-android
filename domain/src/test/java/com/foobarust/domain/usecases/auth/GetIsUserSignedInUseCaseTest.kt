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
 * Created by kevin on 4/9/21
 */

class GetIsUserSignedInUseCaseTest {

    private lateinit var getIsUserSignedInUseCase: GetIsUserSignedInUseCase
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