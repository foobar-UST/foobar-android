package com.foobarust.domain.usecases.user

import com.foobarust.domain.states.Resource
import com.foobarust.testshared.di.DependencyContainer
import com.foobarust.testshared.repositories.FakeAuthRepositoryImpl
import com.foobarust.testshared.repositories.FakeUserRepositoryImpl
import com.foobarust.testshared.utils.TestCoroutineRule
import com.foobarust.testshared.utils.coroutineScope
import com.foobarust.testshared.utils.runBlockingTest
import kotlinx.coroutines.flow.toList
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

/**
 * Created by kevin on 4/29/21
 */

class UploadUserPhotoUseCaseTest {

    private lateinit var uploadUserPhotoUseCase: UploadUserPhotoUseCase
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
            isSignedIn = true,
            coroutineScope = coroutineRule.coroutineScope()
        )

        fakeUserRepositoryImpl = FakeUserRepositoryImpl(
            idToken = dependencyContainer.fakeIdToken,
            defaultUserDetail = dependencyContainer.fakeUserDetail,
            hasCompletedTutorial = true
        )

        uploadUserPhotoUseCase = UploadUserPhotoUseCase(
            authRepository = fakeAuthRepositoryImpl,
            userRepository = fakeUserRepositoryImpl,
            coroutineDispatcher = coroutineRule.testDispatcher
        )
    }

    @Test
    fun `test upload success`() = coroutineRule.runBlockingTest {
        fakeAuthRepositoryImpl.setUserSignedIn(true)
        fakeUserRepositoryImpl.setNetworkError(false)

        val params = UploadUserPhotoParameters(
            photoUri = UUID.randomUUID().toString(),
            photoExtension = "jpg"
        )
        val result = uploadUserPhotoUseCase(params).toList().last()

        assert(result is Resource.Success)
    }

    @Test
    fun `test not signed in error`() = coroutineRule.runBlockingTest {
        fakeAuthRepositoryImpl.setUserSignedIn(false)
        fakeUserRepositoryImpl.setNetworkError(false)

        val params = UploadUserPhotoParameters(
            photoUri = UUID.randomUUID().toString(),
            photoExtension = "jpg"
        )
        val result = uploadUserPhotoUseCase(params).toList().last()

        assert(result is Resource.Error)
    }

    @Test
    fun `test network error`() = coroutineRule.runBlockingTest {
        fakeAuthRepositoryImpl.setUserSignedIn(true)
        fakeUserRepositoryImpl.setNetworkError(true)

        val params = UploadUserPhotoParameters(
            photoUri = UUID.randomUUID().toString(),
            photoExtension = "jpg"
        )
        val result = uploadUserPhotoUseCase(params).toList().last()

        assert(result is Resource.Error)
    }
}