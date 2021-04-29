package com.foobarust.domain.usecases.user

import com.foobarust.domain.di.DependencyContainer
import com.foobarust.domain.repository.FakeAuthRepositoryImpl
import com.foobarust.domain.repository.FakeUserRepositoryImpl
import com.foobarust.domain.states.Resource
import com.foobarust.domain.utils.TestCoroutineRule
import com.foobarust.domain.utils.runBlockingTest
import kotlinx.coroutines.flow.toList
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by kevin on 4/29/21
 */

class GetDelivererProfileUseCaseTest {

    private lateinit var getDelivererProfileUseCase: GetDelivererProfileUseCase
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
            defaultAuthProfile = dependencyContainer.fakeAuthProfile,
            isSignedIn = true
        )

        fakeUserRepositoryImpl = FakeUserRepositoryImpl(
            idToken = dependencyContainer.fakeIdToken,
            defaultUserDetail = dependencyContainer.fakeUserDetail,
            hasCompletedTutorial = true
        )

        getDelivererProfileUseCase = GetDelivererProfileUseCase(
            authRepository = fakeAuthRepositoryImpl,
            userRepository = fakeUserRepositoryImpl,
            coroutineDispatcher = coroutineRule.testDispatcher
        )
    }

    @Test
    fun `test get profile success`() = coroutineRule.runBlockingTest {
        fakeAuthRepositoryImpl.setUserSignedIn(true)
        fakeUserRepositoryImpl.setNetworkError(false)

        val result = getDelivererProfileUseCase(dependencyContainer.fakeAuthProfile.id).toList().last()
        assert(result is Resource.Success)
    }

    @Test
    fun `test not signed in, error`() = coroutineRule.runBlockingTest {
        fakeAuthRepositoryImpl.setUserSignedIn(false)
        fakeUserRepositoryImpl.setNetworkError(false)

        val result = getDelivererProfileUseCase(dependencyContainer.fakeAuthProfile.id).toList().last()
        assert(result is Resource.Error)
    }

    @Test
    fun `test network error`() = coroutineRule.runBlockingTest {
        fakeAuthRepositoryImpl.setUserSignedIn(true)
        fakeUserRepositoryImpl.setNetworkError(true)

        val result = getDelivererProfileUseCase(dependencyContainer.fakeAuthProfile.id).toList().last()
        assert(result is Resource.Error)
    }
}