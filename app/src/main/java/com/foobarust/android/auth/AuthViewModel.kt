package com.foobarust.android.auth

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.domain.states.Resource
import com.foobarust.domain.states.getSuccessDataOr
import com.foobarust.domain.usecases.auth.*
import com.foobarust.domain.usecases.user.DoOnSignInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "AuthViewModel"
private const val RESEND_BUFFER = 5000L

@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val requestAuthEmailUseCase: RequestAuthEmailUseCase,
    private val signInWithEmailLinkUseCase: SignInWithEmailLinkUseCase,
    private val getSavedAuthEmailUseCase: GetSavedAuthEmailUseCase,
    private val updateSavedAuthEmailUseCase: UpdateSavedAuthEmailUseCase,
    private val countDownTimerUseCase: CountDownTimerUseCase,
    private val getIsUserSignedInUseCase: GetIsUserSignedInUseCase,
    private val doOnSignInUseCase: DoOnSignInUseCase,
    authEmailUtil: AuthEmailUtil
) : BaseViewModel() {

    private var _username = MutableStateFlow("")
    private val _emailDomain = MutableStateFlow(authEmailUtil.emailDomains.first())

    private val signInEmail: Flow<String> = _username.combine(_emailDomain) { username, emailDomain ->
        "$username@${emailDomain.domain}"
    }

    private var resendEmailTimerJob: Job? = null
    private var isResendEmailTimerActive: Boolean = false

    private val _authPage = MutableStateFlow(AuthPage.INPUT)
    val authPage: LiveData<AuthPage> = _authPage.asLiveData(viewModelScope.coroutineContext)

    private val _userSignedIn = SingleLiveEvent<Unit>()
    val userSignedIn: LiveData<Unit>
        get() = _userSignedIn

    // TODO
    private val _requestingEmail = MutableStateFlow(false)
    val requestingEmail: LiveData<Boolean> = _requestingEmail
        .asLiveData(viewModelScope.coroutineContext)

    val emailDomains: List<AuthEmailDomain> = authEmailUtil.emailDomains

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
                    updateSavedAuthEmailUseCase(signInEmail)
                    _authPage.value = AuthPage.VERIFYING
                    _requestingEmail.value = false
                }
                is Resource.Error -> {
                    // Condition 3: Failed email request
                    // When there is something wrong with the request, navigate back to input screen
                    showToastMessage(it.message)
                    _authPage.value = AuthPage.INPUT
                    _requestingEmail.value = false
                }
                is Resource.Loading -> {
                    _requestingEmail.value = true
                }
            }
        }
    }

    fun onSignInWithEmailLink(emailLink: String) = viewModelScope.launch {
        // Check if the user is already signed in.
        if (getIsUserSignedInUseCase(Unit).getSuccessDataOr(false)) {
            showToastMessage(context.getString(R.string.auth_signed_in_message))
            _userSignedIn.value = Unit
            return@launch
        }

        // Get cached email address
        getSavedAuthEmailUseCase(Unit).collect {
            when (it) {
                is Resource.Success -> {
                    val signInParams = SignInWithEmailLinkParameters(
                        email = it.data,
                        authLink = emailLink
                    )
                    signInWithEmailLink(signInParams)
                }
                is Resource.Error -> {
                    showToastMessage(context.getString(R.string.auth_error_message))
                    _authPage.value = AuthPage.INPUT
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun onUsernameUpdated(username: String) {
        _username.value = username
    }

    fun onEmailDomainSelected(domain: AuthEmailDomain) {
        _emailDomain.value = domain
    }

    fun onEmailVerificationCanceled() = viewModelScope.launch {
        // Condition 4: cancel email verification
        _authPage.value = AuthPage.INPUT
    }

    fun onSkipSignIn() {
        _authPage.value = AuthPage.COMPLETED
    }

    private fun signInWithEmailLink(params: SignInWithEmailLinkParameters) = viewModelScope.launch {
        signInWithEmailLinkUseCase(params).collect {
            when (it) {
                is Resource.Success -> {
                    // Condition 2: Success email verification
                    // Navigate to MainActivity
                    doOnSignInUseCase(Unit)
                    _authPage.value = AuthPage.COMPLETED
                }
                is Resource.Error -> {
                    // Condition 5: Failed email verification
                    // Navigate back to input page
                    showToastMessage(it.message)
                    _authPage.value = AuthPage.INPUT
                }
                is Resource.Loading -> {
                    // Navigate to verify page when loading
                    _authPage.value = AuthPage.VERIFYING
                }
            }
        }
    }

    private fun startResendEmailTimer() {
        // Ensure there is only one timer instance.
        if (resendEmailTimerJob?.isActive == true) {
            Log.d(TAG, "resend timer is still active.")
            return
        }

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

enum class AuthPage {
    INPUT,
    VERIFYING,
    COMPLETED
}