package com.foobarust.android.auth

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.common.BaseViewModel
import com.foobarust.domain.states.Resource
import com.foobarust.domain.states.getSuccessDataOr
import com.foobarust.domain.usecases.auth.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val RESEND_BUFFER = 5000L

@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val requestAuthEmailUseCase: RequestAuthEmailUseCase,
    private val signInWithAuthLinkUseCase: SignInWithAuthLinkUseCase,
    private val getRequestedEmailUseCase: GetRequestedEmailUseCase,
    private val updateRequestedEmailUseCase: UpdateRequestedEmailUseCase,
    private val removeRequestedEmailUseCase: RemoveRequestedEmailUseCase,
    private val countDownTimerUseCase: CountDownTimerUseCase,
    private val getIsUserSignedInUseCase: GetIsUserSignedInUseCase,
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

    private val _authState = MutableStateFlow(AuthState.INPUT)
    val authState: LiveData<AuthState> = _authState.asLiveData(viewModelScope.coroutineContext)

    private val _requestingEmail = MutableStateFlow(false)
    val requestingEmail: LiveData<Boolean> = _requestingEmail
        .asLiveData(viewModelScope.coroutineContext)

    fun onRequestAuthEmail() = viewModelScope.launch {
        val signInEmail = signInEmail.first()

        // Check if the username input is empty
        if (_username.value.isBlank()) {
            showToastMessage(context.getString(R.string.auth_input_username_empty))
            return@launch
        }

        // Check if the request timer is still active
        if (isResendEmailTimerActive) {
            showToastMessage(context.getString(R.string.auth_email_resend_timer_message))
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
                    updateRequestedEmailUseCase(signInEmail)
                    _authState.value = AuthState.VERIFYING
                    _requestingEmail.value = false
                }
                is Resource.Error -> {
                    // Condition 3: Failed email request
                    // When there is something wrong with the request, navigate back to input screen
                    showToastMessage(it.message)
                    _authState.value = AuthState.INPUT
                    _requestingEmail.value = false
                }
                is Resource.Loading -> {
                    _requestingEmail.value = true
                }
            }
        }
    }

    fun onVerifyEmailLinkAndSignIn(emailLink: String) = viewModelScope.launch {
        if (getIsUserSignedInUseCase(Unit).getSuccessDataOr(false)) {
            showToastMessage(context.getString(R.string.auth_signed_in_message))
            _authState.value = AuthState.COMPLETED
        } else {
            // Get cached email address
            getRequestedEmailUseCase(Unit).collect {
                when (it) {
                    is Resource.Success -> {
                        val signInParams = SignInWithAuthLinkParameters(
                            email = it.data,
                            authLink = emailLink
                        )
                        signInWithAuthLink(signInParams)
                    }
                    is Resource.Error -> {
                        // No email is saved for verification, go back to input screen
                        showToastMessage(context.getString(R.string.auth_error_message))
                        _authState.value = AuthState.INPUT
                    }
                    is Resource.Loading -> Unit
                }
            }
        }
    }

    fun onUsernameUpdated(username: String) {
        _username.value = username
    }

    fun onAuthEmailDomainUpdated(domain: AuthEmailDomain) {
        _emailDomains.value = domain
    }

    fun onAuthEmailVerifyingCanceled() = viewModelScope.launch {
        // Condition 4: cancel email verification
        _authState.value = AuthState.INPUT
        removeRequestedEmail()
    }

    fun onSkipSignIn() {
        _authState.value = AuthState.COMPLETED
    }

    private suspend fun signInWithAuthLink(signInParams: SignInWithAuthLinkParameters) {
        signInWithAuthLinkUseCase(signInParams).collect {
            when (it) {
                is Resource.Success -> {
                    // Condition 2: Success email verification
                    removeRequestedEmail()
                    _authState.value = AuthState.COMPLETED
                }
                is Resource.Error -> {
                    // Condition 5: Failed email verification
                    // Navigate back to input screen
                    showToastMessage(it.message)
                    _authState.value = AuthState.INPUT
                }
                is Resource.Loading -> {
                    _authState.value = AuthState.VERIFYING
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