package com.foobarust.android.auth

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.shared.BaseViewModel
import com.foobarust.domain.states.Resource
import com.foobarust.domain.states.getSuccessDataOr
import com.foobarust.domain.usecases.auth.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "AuthViewModel"
private const val AUTH_EMAIL_RESEND_INTERVAL = 5000L

@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val requestAuthEmailUseCase: RequestAuthEmailUseCase,
    private val signInWithEmailLinkUseCase: SignInWithEmailLinkUseCase,
    private val getSavedAuthEmailUseCase: GetSavedAuthEmailUseCase,
    private val updateSavedAuthEmailUseCase: UpdateSavedAuthEmailUseCase,
    private val oneShotTimerUseCase: OneShotTimerUseCase,
    private val getIsUserSignedInUseCase: GetIsUserSignedInUseCase,
    private val authEmailUtil: AuthEmailUtil
) : BaseViewModel() {

    private val _username = MutableStateFlow("")
    private val _emailDomain = MutableStateFlow(authEmailUtil.emailDomains.first())

    private val signInEmail: Flow<String> = _username.combine(_emailDomain) { username, emailDomain ->
        "$username@${emailDomain.domain}"
    }

    private var resendEmailTimerJob: Job? = null
    private var isResendEmailTimerActive: Boolean = false

    private val _authUiState = MutableStateFlow(AuthUiState.INPUT)
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    private val _isUserSignedIn = Channel<Unit>()
    val isUserSignedIn: Flow<Unit> = _isUserSignedIn.receiveAsFlow()

    val emailDomains: StateFlow<List<AuthEmailDomain>> = MutableStateFlow(authEmailUtil.emailDomains)
        .asStateFlow()

    fun getSavedUsernameInput(): String = _username.value

    fun getSavedEmailDomainInput(): AuthEmailDomain = _emailDomain.value

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

        // Clear input fields after leaving input fragment
        clearInputFields()

        // Request authentication email
        requestAuthEmailUseCase(signInEmail).collect {
            when (it) {
                is Resource.Success -> {
                    // Condition 1: Success email request
                    // Navigate to verify screen
                    updateSavedAuthEmailUseCase(signInEmail)
                    _authUiState.value = AuthUiState.VERIFYING
                }
                is Resource.Error -> {
                    // Condition 3: Failed email request
                    // When there is something wrong with the request, navigate back to input screen
                    showToastMessage(it.message)
                    _authUiState.value = AuthUiState.INPUT
                }
                is Resource.Loading -> {
                    _authUiState.value = AuthUiState.REQUESTING
                }
            }
        }
    }

    fun onSignInWithEmailLink(emailLink: String) = viewModelScope.launch {
        // Check if the user is already signed in.
        if (getIsUserSignedInUseCase(Unit).getSuccessDataOr(false)) {
            showToastMessage(context.getString(R.string.auth_signed_in_message))
            _isUserSignedIn.offer(Unit)
            return@launch
        }

        // Get saved email address and sign in
        getSavedAuthEmailUseCase(Unit).flatMapLatest {
            when (it) {
                is Resource.Success -> {
                    val params = SignInWithEmailLinkParameters(
                        email = it.data,
                        authLink = emailLink
                    )
                    signInWithEmailLinkUseCase(params)
                }
                is Resource.Error -> {
                    _authUiState.value = AuthUiState.INPUT
                    showToastMessage(context.getString(R.string.auth_error_message))
                    emptyFlow()
                }
                is Resource.Loading -> emptyFlow()
            }
        }.collect {
            when (it) {
                is Resource.Success -> {
                    // Condition 2: Success email verification
                    _authUiState.value = AuthUiState.COMPLETED
                }
                is Resource.Error -> {
                    // Condition 5: Failed email verification
                    _authUiState.value = AuthUiState.INPUT
                    showToastMessage(it.message)
                }
                is Resource.Loading -> {
                    // Signing in
                    _authUiState.value = AuthUiState.VERIFYING
                }
            }
        }
    }

    fun onUsernameUpdated(username: String) {
        _username.value = username
    }

    fun onEmailDomainUpdated(domain: AuthEmailDomain) {
        _emailDomain.value = domain
    }

    fun onEmailVerificationCancelled() = viewModelScope.launch {
        // Condition 4: cancel email verification
        _authUiState.value = AuthUiState.INPUT
    }

    fun onSignInSkipped() {
        _authUiState.value = AuthUiState.COMPLETED
    }

    private fun clearInputFields() {
        _username.value = ""
        _emailDomain.value = authEmailUtil.emailDomains.first()
    }

    private fun startResendEmailTimer() {
        if (resendEmailTimerJob?.isActive == true) {
            Log.d(TAG, "Email resend timer is still active.")
            return
        }

        resendEmailTimerJob = viewModelScope.launch {
            oneShotTimerUseCase(AUTH_EMAIL_RESEND_INTERVAL).collect {
                isResendEmailTimerActive = it
            }
        }
    }
}

enum class AuthUiState {
    INPUT,
    REQUESTING,
    VERIFYING,
    COMPLETED
}