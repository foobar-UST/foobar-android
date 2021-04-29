package com.foobarust.domain.usecases.onboarding

import com.foobarust.domain.di.DependencyContainer
import com.foobarust.domain.repository.FakeUserRepositoryImpl
import com.foobarust.domain.states.Resource
import com.foobarust.domain.utils.TestCoroutineRule
import com.foobarust.domain.utils.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by kevin on 4/28/21
 */

class GetUserCompleteTutorialUseCaseTest {

    private lateinit var getUserCompleteTutorialUseCase: GetUserCompleteTutorialUseCase
    private lateinit var fakeUserRepositoryImpl: FakeUserRepositoryImpl
    private lateinit var dependencyContainer: DependencyContainer

    @get:Rule
    var coroutineRule = TestCoroutineRule()

    @Before
    fun init() {
        dependencyContainer = DependencyContainer()

        fakeUserRepositoryImpl = FakeUserRepositoryImpl(
            idToken = dependencyContainer.fakeIdToken,
            defaultUserDetail = dependencyContainer.fakeUserDetail,
            hasCompletedTutorial = false
        )

        getUserCompleteTutorialUseCase = GetUserCompleteTutorialUseCase(
            userRepository = fakeUserRepositoryImpl,
            coroutineDispatcher = coroutineRule.testDispatcher
        )
    }

    @Test
    fun `test tutorial complete success`() = coroutineRule.runBlockingTest {
        fakeUserRepositoryImpl.setIOError(false)
        val result = getUserCompleteTutorialUseCase(Unit)
        assert(result is Resource.Success)
    }

    @Test
    fun `test io error`() = coroutineRule.runBlockingTest {
        fakeUserRepositoryImpl.setIOError(true)
        val result = getUserCompleteTutorialUseCase(Unit)
        assert(result is Resource.Error)
    }
}