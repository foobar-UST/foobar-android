package com.foobarust.android.signin

import android.content.Context
import android.os.CountDownTimer
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.signin.SignInState.*
import com.foobarust.domain.states.Resource.Error
import com.foobarust.domain.states.Resource.Success
import com.foobarust.domain.states.getSuccessDataOr
import com.foobarust.domain.usecases.auth.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Created by kevin on 8/26/20
 */

private const val RESEND_BUFFER = 5_000L          // TODO: Change resend buffer time
private const val SECOND = 1_000L

private const val TAG = "SignInViewModel"

private val ustEmailDomains: List<AuthEmailDomain> = listOf(
    AuthEmailDomain(domain = "connect.ust.hk", title = "@connect.ust.hk"),
    AuthEmailDomain(domain = "ust.hk", title = "@ust.hk")
)

class SignInViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val requestAuthEmailUseCase: RequestAuthEmailUseCase,
    private val signInWithAuthLinkUseCase: SignInWithAuthLinkUseCase,
    private val getEmailToBeVerifiedUseCase: GetEmailToBeVerifiedUseCase,
    private val saveEmailToBeVerifiedUseCase: SaveEmailToBeVerifiedUseCase,
    private val getSkippedSignInUseCase: GetSkippedSignInUseCase,
    private val saveSkippedSignInUseCase: SaveSkippedSignInUseCase
) : BaseViewModel() {

    // Domain list for drop down menu
    val emailDomains = liveData {
        emit(ustEmailDomains)
    }

    // Channels holding user inputs
    private var usernameChannel = ConflatedBroadcastChannel("")
    private val emailDomainChannel = ConflatedBroadcastChannel(ustEmailDomains.first())

    // Compute the sign-in email
    private val signInEmailFlow: Flow<String> = usernameChannel.asFlow()
        .combine(emailDomainChannel.asFlow()) { username, emailDomain ->
            "$username@${emailDomain.domain}"
        }

    // Timer to provide buffer time for each email resend request
    private val _resendAuthEmailTimerActive = ConflatedBroadcastChannel(false)
    private lateinit var resendAuthEmailTimer: CountDownTimer

    private val _signInState = MutableLiveData<SignInState>()
    val signInState: LiveData<SignInState>
        get() = _signInState


    fun requestAuthEmail() = viewModelScope.launch {
        // Collect one value and cancel the flow
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

        requestAuthEmailUseCase(signInEmail).collect { result ->
            // Navigate to VerifyFragment if the request is success, otherwise do nothing.
            when (result) {
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
                    showMessage(result.message)
                }
            }

            // Start timer to prevent abuse of email request
            startResendAuthEmailTimer()
        }
    }

    fun verifyEmailLinkAndSignIn(emailLink: String) = viewModelScope.launch {
        getEmailToBeVerifiedUseCase(Unit).getSuccessDataOr(null)?.let { email ->
            val signInParams = SignInWithAuthLinkParameters(
                email = email,
                authLink = emailLink
            )

            showMessage(context.getString(R.string.signin_ongoing))

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

    fun skipSignIn() = viewModelScope.launch {
        // Condition 6: skip sign-in
        saveSkippedSignInUseCase(true)

        if (getSkippedSignInUseCase(Unit).getSuccessDataOr(false)) {
            _signInState.value = COMPLETED
        }
    }

    private fun handleSuccessSignIn() {
        // Condition 2: success email verification
        viewModelScope.launch {
            saveEmailToBeVerifiedUseCase(null)
            _signInState.value = COMPLETED
            showMessage(context.getString(R.string.signin_success))
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

data class AuthEmailDomain(val domain: String, val title: String)

enum class SignInState {
    INPUT,
    VERIFYING,
    COMPLETED,
}