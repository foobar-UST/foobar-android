package com.foobarust.domain.usecases.auth

import com.foobarust.domain.repository.FakeAuthRepositoryImpl
import com.foobarust.domain.repository.FakeMessagingRepositoryImpl
import com.foobarust.domain.states.Resource
import com.foobarust.domain.utils.TestCoroutineRule
import com.foobarust.domain.utils.runBlockingTest
import di.DependencyContainer
import kotlinx.coroutines.flow.toList
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

/**
 * Created by kevin on 4/21/21
 */

class SignInWithEmailLinkUseCaseTest {

    private lateinit var signInWithEmailLinkUseCase: SignInWithEmailLinkUseCase
    private lateinit var fakeAuthRepositoryImpl: FakeAuthRepositoryImpl
    private lateinit var fakeMessagingRepositoryImpl: FakeMessagingRepositoryImpl

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

        fakeMessagingRepositoryImpl = FakeMessagingRepositoryImpl(
            idToken = dependencyContainer.fakeIdToken
        )

        signInWithEmailLinkUseCase = SignInWithEmailLinkUseCase(
            authRepository = fakeAuthRepositoryImpl,
            messagingRepository = fakeMessagingRepositoryImpl,
            coroutineDispatcher = coroutineRule.testDispatcher
        )
    }

    @Test
    fun `test sign in success`() = coroutineRule.runBlockingTest {
        fakeAuthRepositoryImpl.updateSavedAuthEmail(FAKE_AUTH_EMAIL)
        fakeAuthRepositoryImpl.setNetworkError(false)
        fakeAuthRepositoryImpl.setIOError(false)

        val results = signInWithEmailLinkUseCase(params).toList()
        println(results)
        assert(results.last() is Resource.Success)
    }

    @Test
    fun`test sign in with email link error`() = coroutineRule.runBlockingTest {
        fakeAuthRepositoryImpl.updateSavedAuthEmail(FAKE_AUTH_EMAIL)
        fakeAuthRepositoryImpl.setNetworkError(true)
        fakeAuthRepositoryImpl.setIOError(false)

        val results = signInWithEmailLinkUseCase(params).toList()
        assert(results.last() is Resource.Error)
    }

    @Test
    fun `test link device token error`() = coroutineRule.runBlockingTest {
        fakeAuthRepositoryImpl.updateSavedAuthEmail(FAKE_AUTH_EMAIL)
        fakeAuthRepositoryImpl.setNetworkError(true)
        fakeAuthRepositoryImpl.setIOError(false)

        val results = signInWithEmailLinkUseCase(params).toList()
        assert(results.last() is Resource.Error)
    }

    @Test
    fun `test remove saved auth email error`() = coroutineRule.runBlockingTest {
        fakeAuthRepositoryImpl.updateSavedAuthEmail(FAKE_AUTH_EMAIL)
        fakeAuthRepositoryImpl.setNetworkError(false)
        fakeAuthRepositoryImpl.setIOError(true)

        val results = signInWithEmailLinkUseCase(params).toList()
        assert(results.last() is Resource.Error)
    }

    companion object {
        private const val FAKE_AUTH_EMAIL = "test@test.com"
        private val params = SignInWithEmailLinkParameters(
            email = FAKE_AUTH_EMAIL,
            authLink = UUID.randomUUID().toString()
        )
    }
}