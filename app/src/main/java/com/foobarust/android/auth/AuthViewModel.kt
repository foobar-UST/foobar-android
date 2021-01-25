package com.foobarust.android.auth

import android.content.Context
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.auth.AuthState.*
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.common.UiState
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.auth.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private const val RESEND_BUFFER = 5000L

class AuthViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val requestAuthEmailUseCase: RequestAuthEmailUseCase,
    private val signInWithAuthLinkUseCase: SignInWithAuthLinkUseCase,
    private val getRequestedEmailUseCase: GetRequestedEmailUseCase,
    private val updateRequestedEmailUseCase: UpdateRequestedEmailUseCase,
    private val removeRequestedEmailUseCase: RemoveRequestedEmailUseCase,
    private val countDownTimerUseCase: CountDownTimerUseCase,
    authEmailUtil: AuthEmailUtil
) : BaseViewModel() {

    val emailDomains: List<AuthEmailDomain> = authEmailUtil.emailDomains

    private var _username = MutableStateFlow("")
    private val _emailDomains = MutableStateFlow(authEmailUtil.emailDomains.first())

    private val signInEmail: Flow<String> = _username.combine(_emailDomains) { username, emailDomain ->
        "$username@${emailDomain.domain}"
    }

    private var resendEmailTimerJob: Job? = null
    private var isResendEmailTimerActive: Boolean = false

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState>
        get() = _authState

    fun onRequestAuthEmail() = viewModelScope.launch {
        val signInEmail = signInEmail.first()

        // Check if the username input is empty
        if (_username.value.isBlank()) {
            showToastMessage(context.getString(R.string.signin_input_username_empty))
            return@launch
        }

        // Check if the request timer is still active
        if (isResendEmailTimerActive) {
            showToastMessage(context.getString(R.string.signin_auth_email_resend_interval))
            return@launch
        }

        // Start timer to prevent abuse of email request
        startResendEmailTimer()

        // Request authentication email
        requestAuthEmailUseCase(signInEmail).collect {
            when (it) {
                is Resource.Success -> {
                    // Condition 1: Success email request
                    // Navigate to verify screen
                    setUiState(UiState.Success)
                    updateRequestedEmailUseCase(signInEmail)
                    _authState.value = VERIFYING
                }
                is Resource.Error -> {
                    // Condition 3: Failed email request
                    // When there is something wrong with the request, navigate back to input screen
                    setUiState(UiState.Error(it.message))
                    _authState.value = INPUT
                }
                is Resource.Loading -> {
                    setUiState(UiState.Loading)
                }
            }
        }
    }

    fun onVerifyEmailLinkAndSignIn(emailLink: String) = viewModelScope.launch {
        _authState.value = VERIFYING
        // Get cached email address
        getRequestedEmailUseCase(Unit).collect {
            when (it) {
                is Resource.Success -> {
                    setUiState(UiState.Success)
                    val signInParams = SignInWithAuthLinkParameters(
                        email = it.data,
                        authLink = emailLink
                    )
                    signInWithAuthLink(signInParams)
                }
                is Resource.Error -> {
                    // No email is saved for verification, go back to input screen
                    _authState.value = INPUT
                    setUiState(UiState.Error(context.getString(R.string.signin_error)))
                }
                is Resource.Loading -> {
                    setUiState(UiState.Loading)
                }
            }
        }
    }

    fun onUsernameUpdated(username: String) {
        _username.value = username
    }

    fun onAuthEmailDomainUpdated(authEmailDomain: AuthEmailDomain) {
        _emailDomains.value = authEmailDomain
    }

    fun onAuthEmailVerifyingCanceled() = viewModelScope.launch {
        // Condition 4: cancel email verification
        _authState.value = INPUT
        removeRequestedEmail()
    }

    fun onSkipSignIn() {
        _authState.value = COMPLETED
    }

    private suspend fun signInWithAuthLink(signInParams: SignInWithAuthLinkParameters) {
        signInWithAuthLinkUseCase(signInParams).collect {
            when (it) {
                is Resource.Success -> {
                    // Condition 2: Success email verification
                    removeRequestedEmail()
                    _authState.value = COMPLETED
                    setUiState(UiState.Success)
                }
                is Resource.Error -> {
                    // Condition 5: Failed email verification
                    // Navigate back to input screen
                    _authState.value = INPUT
                    setUiState(UiState.Error(it.message))
                }
                is Resource.Loading -> {
                    setUiState(UiState.Loading)
                }
            }
        }
    }

    private fun removeRequestedEmail() = viewModelScope.launch {
        when (removeRequestedEmailUseCase(Unit)) {
            is Resource.Success -> Unit
            is Resource.Error -> showToastMessage("Failed to remove request email.")
            is Resource.Loading -> Unit
        }
    }

    private fun startResendEmailTimer() {
        if (resendEmailTimerJob?.isActive == true) {
            Log.d("AuthViewModel", "resendEmailTimer is still active.")
            return
        }
        Log.d("AuthViewModel", "Start new resendEmailTimer.")
        resendEmailTimerJob = viewModelScope.launch {
            countDownTimerUseCase(RESEND_BUFFER).collect {
                isResendEmailTimerActive = when (it) {
                    is Resource.Success -> it.data
                    is Resource.Error -> false
                    is Resource.Loading -> true
                }
            }
        }
    }
}

enum class AuthState {
    INPUT,
    VERIFYING,
    COMPLETED
}