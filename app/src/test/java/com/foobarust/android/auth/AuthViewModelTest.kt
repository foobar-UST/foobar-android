package com.foobarust.android.auth

import android.content.Context
import com.foobarust.android.di.RepositoryContainer
import com.foobarust.domain.usecases.auth.*
import com.foobarust.testshared.utils.TestCoroutineRule
import com.foobarust.testshared.utils.coroutineScope
import com.foobarust.testshared.utils.runBlockingTest
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by kevin on 4/30/21
 */

class AuthViewModelTest {

    private lateinit var repositoryContainer: RepositoryContainer

    @get:Rule
    var coroutineRule = TestCoroutineRule()

    @Before
    fun init() {
        repositoryContainer = RepositoryContainer(coroutineRule.coroutineScope())
    }

    @Test
    fun `test email domains loaded`() {
        val authViewModel = createAuthViewModel()
        assertEquals(authViewModel.emailDomains, AuthEmailUtil().emailDomains)
    }

    @Test
    fun `test initial auth state is input`() = coroutineRule.runBlockingTest {
        val authViewModel = createAuthViewModel()
        assertEquals(authViewModel.authUiState, AuthEmailUtil().emailDomains)
    }

    private fun createAuthViewModel(): AuthViewModel = AuthViewModel(
        context = mockApplicationContext(),
        requestAuthEmailUseCase = RequestAuthEmailUseCase(
            authRepository = repositoryContainer.authRepository,
            coroutineDispatcher = coroutineRule.testDispatcher
        ),
        signInWithEmailLinkUseCase = SignInWithEmailLinkUseCase(
            authRepository = repositoryContainer.authRepository,
            messagingRepository = repositoryContainer.messagingRepository,
            coroutineDispatcher = coroutineRule.testDispatcher
        ),
        getSavedAuthEmailUseCase = GetSavedAuthEmailUseCase(
            authRepository = repositoryContainer.authRepository,
            coroutineDispatcher = coroutineRule.testDispatcher
        ),
        updateSavedAuthEmailUseCase = UpdateSavedAuthEmailUseCase(
            authRepository = repositoryContainer.authRepository,
            coroutineDispatcher = coroutineRule.testDispatcher
        ),
        oneShotTimerUseCase = OneShotTimerUseCase(
            coroutineDispatcher = coroutineRule.testDispatcher
        ),
        getIsUserSignedInUseCase = GetIsUserSignedInUseCase(
            authRepository = repositoryContainer.authRepository,
            coroutineDispatcher = coroutineRule.testDispatcher
        ),
        authEmailUtil = AuthEmailUtil()
    )

    private fun mockApplicationContext(): Context {
        return mockk()
    }
}