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
import com.foobarust.domain.states.Resource.Error
import com.foobarust.domain.states.Resource.Success
import com.foobarust.domain.states.getSuccessDataOr
import com.foobarust.domain.usecases.auth.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private const val RESEND_BUFFER = 5_000L          // TODO: Change resend buffer time
private const val SECOND = 1_000L

class AuthViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val requestAuthEmailUseCase: RequestAuthEmailUseCase,
    private val signInWithAuthLinkUseCase: SignInWithAuthLinkUseCase,
    private val getEmailToBeVerifiedUseCase: GetEmailToBeVerifiedUseCase,
    private val saveEmailToBeVerifiedUseCase: SaveEmailToBeVerifiedUseCase,
    private val authEmailUtil: AuthEmailUtil
) : BaseViewModel() {

    val emailDomains = liveData {
        emit(authEmailUtil.emailDomains)
    }

    private var usernameChannel = ConflatedBroadcastChannel("")
    private val emailDomainChannel = ConflatedBroadcastChannel(authEmailUtil.emailDomains.first())

    // Compute the sign-in email
    private val signInEmailFlow: Flow<String> = usernameChannel.asFlow()
        .combine(emailDomainChannel.asFlow()) { username, emailDomain ->
            "$username@${emailDomain.domain}"
        }

    // Timer to provide buffer time between each email resend request
    private val _resendAuthEmailTimerActive = ConflatedBroadcastChannel(false)
    private lateinit var resendAuthEmailTimer: CountDownTimer

    private val _signInState = MutableLiveData<SignInState>()
    val signInState: LiveData<SignInState>
        get() = _signInState

    fun requestAuthEmail() = viewModelScope.launch {
        val signInEmail = signInEmailFlow.first()

        // Check if the username input is empty
        if (usernameChannel.value.isBlank()) {
            showMessage(context.getString(R.string.signin_input_email_empty))
            return@launch
        }

        // Check if the request timer is still active
        if (_resendAuthEmailTimerActive.value) {
            showMessage(context.getString(R.string.signin_auth_email_resend_interval))
            return@launch
        }

        requestAuthEmailUseCase(signInEmail).collect {
            // Navigate to VerifyFragment if the request is success, otherwise do nothing.
            when (it) {
                is Success -> {
                    // Condition 1: success email request
                    saveEmailToBeVerifiedUseCase(signInEmail)
                    _signInState.value = VERIFYING
                    showMessage(context.getString(R.string.signin_auth_email_sent))
                }
                is Error -> {
                    // Condition 3: failed email request
                    // When there is something wrong with the request, navigate back to input screen
                    _signInState.value = INPUT
                    showMessage(it.message)
                }
            }

            // Start timer to prevent abuse of email request
            startResendAuthEmailTimer()
        }
    }

    fun verifyEmailLinkAndSignIn(emailLink: String) = viewModelScope.launch {
        _signInState.value = VERIFYING

        // Get the saved email and start to verify
        getEmailToBeVerifiedUseCase(Unit).getSuccessDataOr(null)?.let { email ->
            val signInParams = SignInWithAuthLinkParameters(
                email = email,
                authLink = emailLink
            )

            //showMessage(context.getString(R.string.signin_ongoing))

            signInWithAuthLinkUseCase(signInParams).collect {
                when (it) {
                    is Success -> handleSuccessSignIn()
                    is Error -> {
                        // Condition 5: failed email verification
                        _signInState.value = INPUT
                        showMessage(it.message)
                    }
                }
            }
        } ?: showMessage(context.getString(R.string.signin_error))
    }

    fun onUsernameChanged(username: String) {
        usernameChannel.offer(username)
    }

    fun onAuthEmailDomainChanged(authEmailDomain: AuthEmailDomain) {
        emailDomainChannel.offer(authEmailDomain)
    }

    fun onAuthEmailVerifyingCanceled() = viewModelScope.launch {
        // Condition 4: cancel email verification
        _signInState.value = INPUT
        saveEmailToBeVerifiedUseCase(null)
        showMessage(context.getString(R.string.signin_cancel))
    }

    fun onSignInSkip() {
        _signInState.value = COMPLETED
    }

    private fun handleSuccessSignIn() {
        // Condition 2: success email verification
        viewModelScope.launch {
            saveEmailToBeVerifiedUseCase(null)
            _signInState.value = COMPLETED
        }
    }

    private fun createResendAuthEmailTimer() {
        viewModelScope.launch {
            resendAuthEmailTimer = object : CountDownTimer(RESEND_BUFFER, SECOND) {
                override fun onTick(millisUntilFinished: Long) = Unit
                override fun onFinish() {
                    resetResendAuthEmailTimer()
                }
            }.start()
        }
    }

    private fun startResendAuthEmailTimer() {
        createResendAuthEmailTimer()
        _resendAuthEmailTimerActive.offer(true)
    }

    private fun resetResendAuthEmailTimer() {
        resendAuthEmailTimer.cancel()
        _resendAuthEmailTimerActive.offer(false)
    }
}

enum class SignInState {
    INPUT,
    VERIFYING,
    COMPLETED,
}