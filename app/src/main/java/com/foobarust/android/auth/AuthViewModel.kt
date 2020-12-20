package com.foobarust.android.auth

import android.content.Context
import android.os.CountDownTimer
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.auth.SignInState.*
import com.foobarust.android.common.BaseViewModel
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.auth.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private const val RESEND_BUFFER = 5_000L
private const val SECOND = 1_000L

class AuthViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val requestAuthEmailUseCase: RequestAuthEmailUseCase,
    private val signInWithAuthLinkUseCase: SignInWithAuthLinkUseCase,
    private val getRequestedEmailUseCase: GetRequestedEmailUseCase,
    private val updateRequestedEmailUseCase: UpdateRequestedEmailUseCase,
    private val removeRequestedEmailUseCase: RemoveRequestedEmailUseCase,
    private val authEmailUtil: AuthEmailUtil
) : BaseViewModel() {

    val emailDomains = liveData {
        emit(authEmailUtil.emailDomains)
    }

    private var _username = MutableStateFlow("")
    private val _emailDomains = MutableStateFlow(authEmailUtil.emailDomains.first())

    // Compute the sign-in email
    private val signInEmail: Flow<String> = _username.asStateFlow()
        .combine(_emailDomains.asStateFlow()) { username, emailDomain ->
            "$username@${emailDomain.domain}"
        }

    // Timer to provide buffer time between each email resend request
    private lateinit var resendAuthEmailTimer: CountDownTimer
    private var _resendAuthEmailTimerActive: Boolean = false

    private val _signInState = MutableLiveData<SignInState>()
    val signInState: LiveData<SignInState>
        get() = _signInState

    fun onRequestAuthEmail() = viewModelScope.launch {
        val signInEmail = signInEmail.first()

        // Check if the username input is empty
        if (_username.value.isBlank()) {
            showToastMessage(context.getString(R.string.signin_input_username_empty))
            return@launch
        }

        // Check if the request timer is still active
        if (_resendAuthEmailTimerActive) {
            showToastMessage(context.getString(R.string.signin_auth_email_resend_interval))
            return@launch
        }

        // Request authentication email
        when (val result = requestAuthEmailUseCase(signInEmail)) {
            is Resource.Success -> {
                // Condition 1: Success email request
                // Navigate to verify screen
                updateRequestedEmailUseCase(signInEmail)
                _signInState.value = VERIFYING
                showToastMessage(context.getString(R.string.signin_auth_email_sent))
            }
            is Resource.Error -> {
                // Condition 3: Failed email request
                // When there is something wrong with the request, navigate back to input screen
                _signInState.value = INPUT
                showToastMessage(result.message)
            }
        }

        // Start timer to prevent abuse of email request
        startResendAuthEmailTimer()
    }

    fun onVerifyEmailLinkAndSignIn(emailLink: String) = viewModelScope.launch {
        _signInState.value = VERIFYING

        // Get cached email address
        when (val result = getRequestedEmailUseCase(Unit)) {
            is Resource.Success -> {
                val signInParams = SignInWithAuthLinkParameters(
                    email = result.data,
                    authLink = emailLink
                )
                signInWithAuthLink(signInParams)
            }
            is Resource.Error -> {
                // No email is saved for verification, go back to input screen
                _signInState.value = INPUT
                showToastMessage(context.getString(R.string.signin_error))
            }
            is Resource.Loading -> Unit
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
        _signInState.value = INPUT
        removeRequestedEmail()
        showToastMessage(context.getString(R.string.signin_cancel))
    }

    fun onSignInSkip() {
        _signInState.value = COMPLETED
    }

    private suspend fun signInWithAuthLink(signInParams: SignInWithAuthLinkParameters) {
        when (val result = signInWithAuthLinkUseCase(signInParams)) {
            is Resource.Success -> {
                // Condition 2: Success email verification
                // Remove cached email from preferences
                removeRequestedEmail()
                _signInState.value = COMPLETED
            }
            is Resource.Error -> {
                // Condition 5: Failed email verification
                // Navigate back to input screen
                _signInState.value = INPUT
                showToastMessage(result.message)
            }
            is Resource.Loading -> Unit
        }
    }

    private fun removeRequestedEmail() = viewModelScope.launch {
        when (removeRequestedEmailUseCase(Unit)) {
            is Resource.Success -> showToastMessage("Removed request email.")
            is Resource.Error -> showToastMessage("Failed to remove request email.")
            is Resource.Loading -> Unit
        }
    }

    private fun createResendAuthEmailTimer() = viewModelScope.launch {
        resendAuthEmailTimer = object : CountDownTimer(RESEND_BUFFER, SECOND) {
            override fun onTick(millisUntilFinished: Long) = Unit
            override fun onFinish() { resetResendAuthEmailTimer() }
        }.start()
    }

    private fun startResendAuthEmailTimer() {
        createResendAuthEmailTimer()
        _resendAuthEmailTimerActive = true
    }

    private fun resetResendAuthEmailTimer() {
        resendAuthEmailTimer.cancel()
        _resendAuthEmailTimerActive = false
    }
}

enum class SignInState {
    INPUT,
    VERIFYING,
    COMPLETED
}