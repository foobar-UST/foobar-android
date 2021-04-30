package com.foobarust.android.auth

import android.content.Context
import app.cash.turbine.test
import com.foobarust.android.di.RepositoryContainer
import com.foobarust.domain.usecases.auth.*
import com.foobarust.testshared.utils.TestCoroutineRule
import com.foobarust.testshared.utils.coroutineScope
import com.foobarust.testshared.utils.runBlockingTest
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by kevin on 4/30/21
 */

class AuthViewModelTest {

    private lateinit var dependencyContainer: RepositoryContainer

    @get:Rule
    var coroutineRule = TestCoroutineRule()

    @Before
    fun init() {
        dependencyContainer = RepositoryContainer(coroutineRule.coroutineScope())
    }

    @Test
    fun `test email domains loaded`() = coroutineRule.runBlockingTest {
        val authViewModel = createAuthViewModel()
        val emailDomains = authViewModel.emailDomains.first()
        assertEquals(emailDomains, AuthEmailUtil().emailDomains)
    }

    @Test
    fun `test initial auth state is input`() = coroutineRule.runBlockingTest {
        val authViewModel = createAuthViewModel()
        val authState = authViewModel.authUiState.first()
        assertEquals(authState, AuthUiState.INPUT)
    }

    @Test
    fun `test request auth email success, goto verifying state`() = coroutineRule.runBlockingTest {
        val authViewModel = createAuthViewModel()
        authViewModel.onUsernameUpdated("kthon")
        authViewModel.onRequestAuthEmail()

        val authState = authViewModel.authUiState.value
        assertEquals(authState, AuthUiState.VERIFYING)
    }

    @Test
    fun `test request auth email failed, return input state`() = coroutineRule.runBlockingTest {
        val authViewModel = createAuthViewModel(hasNetworkError = true)
        authViewModel.onUsernameUpdated("kthon")
        authViewModel.onRequestAuthEmail()

        val authState = authViewModel.authUiState.value
        assertEquals(authState, AuthUiState.INPUT)
    }

    @Test
    fun `test skip sign in, is complete state`() = coroutineRule.runBlockingTest {
        val authViewModel = createAuthViewModel()
        authViewModel.onSignInSkipped()

        val authState = authViewModel.authUiState.value
        assertEquals(authState, AuthUiState.COMPLETED)
    }

    @Test
    fun `test sign in success, is complete state`() = coroutineRule.runBlockingTest {
        val authViewModel = createAuthViewModel()

        authViewModel.authUiState.test {
            assertEquals(expectItem(), AuthUiState.INPUT)
            authViewModel.onUsernameUpdated("kthon")

            authViewModel.onRequestAuthEmail()
            assertEquals(expectItem(), AuthUiState.REQUESTING)
            assertEquals(expectItem(), AuthUiState.VERIFYING)

            authViewModel.onSignInWithEmailLink("test link")
            assertEquals(expectItem(), AuthUiState.COMPLETED)
        }
    }

    @Test
    fun `test sign in error, return input state`() = coroutineRule.runBlockingTest {
        val authViewModel = createAuthViewModel()

        authViewModel.authUiState.test {
            assertEquals(expectItem(), AuthUiState.INPUT)
            authViewModel.onUsernameUpdated("kthon")

            authViewModel.onRequestAuthEmail()
            assertEquals(expectItem(), AuthUiState.REQUESTING)
            assertEquals(expectItem(), AuthUiState.VERIFYING)

            dependencyContainer.authRepository.setNetworkError(true)
            authViewModel.onSignInWithEmailLink("test link")
            assertEquals(expectItem(), AuthUiState.INPUT)
        }
    }

    @Test
    fun `test cancel email verification`() = coroutineRule.runBlockingTest {
        val authViewModel = createAuthViewModel()

        authViewModel.authUiState.test {
            assertEquals(expectItem(), AuthUiState.INPUT)
            authViewModel.onUsernameUpdated("kthon")

            authViewModel.onRequestAuthEmail()
            assertEquals(expectItem(), AuthUiState.REQUESTING)
            assertEquals(expectItem(), AuthUiState.VERIFYING)

            authViewModel.onEmailVerificationCancelled()
            assertEquals(expectItem(), AuthUiState.INPUT)
        }
    }

    @Test
    fun `test request multiple auth emails within timer interval`() = coroutineRule.runBlockingTest {
        val authViewModel = createAuthViewModel()

        authViewModel.authUiState.test {
            assertEquals(expectItem(), AuthUiState.INPUT)
            authViewModel.onUsernameUpdated("kthon")

            authViewModel.onRequestAuthEmail()
            assertEquals(expectItem(), AuthUiState.REQUESTING)
            assertEquals(expectItem(), AuthUiState.VERIFYING)

            authViewModel.onRequestAuthEmail()
            expectNoEvents()
        }
    }

    private fun createAuthViewModel(
        isUserSignedIn: Boolean = false,
        hasNetworkError: Boolean = false
    ): AuthViewModel {
        dependencyContainer.setNetworkError(hasNetworkError)
        dependencyContainer.setUserSignedIn(isUserSignedIn)

        return AuthViewModel(
            context = mockApplicationContext(),
            requestAuthEmailUseCase = RequestAuthEmailUseCase(
                authRepository = dependencyContainer.authRepository,
                coroutineDispatcher = coroutineRule.testDispatcher
            ),
            signInWithEmailLinkUseCase = SignInWithEmailLinkUseCase(
                authRepository = dependencyContainer.authRepository,
                messagingRepository = dependencyContainer.messagingRepository,
                coroutineDispatcher = coroutineRule.testDispatcher
            ),
            getSavedAuthEmailUseCase = GetSavedAuthEmailUseCase(
                authRepository = dependencyContainer.authRepository,
                coroutineDispatcher = coroutineRule.testDispatcher
            ),
            updateSavedAuthEmailUseCase = UpdateSavedAuthEmailUseCase(
                authRepository = dependencyContainer.authRepository,
                coroutineDispatcher = coroutineRule.testDispatcher
            ),
            oneShotTimerUseCase = OneShotTimerUseCase(
                coroutineDispatcher = coroutineRule.testDispatcher
            ),
            getIsUserSignedInUseCase = GetIsUserSignedInUseCase(
                authRepository = dependencyContainer.authRepository,
                coroutineDispatcher = coroutineRule.testDispatcher
            ),
            authEmailUtil = AuthEmailUtil()
        )
    }

    private fun mockApplicationContext(): Context = mockk {
        every { getString(any()) } returns "mocked string"
    }
}