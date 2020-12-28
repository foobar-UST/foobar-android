package com.foobarust.android.main

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.cart.CartTimeoutProperty
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.domain.models.cart.UserCart
import com.foobarust.domain.states.Resource
import com.foobarust.domain.states.getSuccessDataOr
import com.foobarust.domain.usecases.auth.GetAuthProfileUseCase
import com.foobarust.domain.usecases.cart.CheckCartTimeOutUseCase
import com.foobarust.domain.usecases.cart.ClearUserCartUseCase
import com.foobarust.domain.usecases.cart.GetUserCartUseCase
import com.foobarust.domain.usecases.onboarding.GetOnboardingCompletedUseCase
import com.foobarust.domain.usecases.onboarding.UpdateOnboardingCompletedUseCase
import com.foobarust.domain.usecases.user.UpdateUserPhotoUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Created by kevin on 9/20/20
 */

class MainViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val getAuthProfileUseCase: GetAuthProfileUseCase,
    private val getUserCartUseCase: GetUserCartUseCase,
    private val updateUserPhotoUseCase: UpdateUserPhotoUseCase,
    private val checkCartTimeOutUseCase: CheckCartTimeOutUseCase,
    private val clearUserCartUseCase: ClearUserCartUseCase,
    private val getOnboardingCompletedUseCase: GetOnboardingCompletedUseCase,
    private val updateOnboardingCompletedUseCase: UpdateOnboardingCompletedUseCase
) : BaseViewModel() {

    private val _scrollToTop = SingleLiveEvent<Unit>()
    val scrollToTop: LiveData<Unit>
        get() = _scrollToTop

    private val _currentGraphId = MutableStateFlow<Int?>(null)

    private val _userCart = MutableStateFlow<UserCart?>(null)
    val userCart: LiveData<UserCart?>
        get() = _userCart.asStateFlow().asLiveData(viewModelScope.coroutineContext)

    val showCartBottomBar: LiveData<Boolean> = _currentGraphId.asStateFlow()
        .combine(_userCart.asStateFlow()) { currentGraphId, userCart ->
            currentGraphId == R.id.navigation_seller &&
                userCart != null &&
                userCart.itemsCount > 0
        }
        .distinctUntilChanged()
        .asLiveData(viewModelScope.coroutineContext)

    private val _showSnackBarMessage = SingleLiveEvent<String>()
    val showSnackBarMessage: LiveData<String>
        get() = _showSnackBarMessage

    private val _navigateToTimeoutDialog = SingleLiveEvent<CartTimeoutProperty>()
    val navigateToTimeoutDialog: LiveData<CartTimeoutProperty>
        get() = _navigateToTimeoutDialog

    private val _showOnboardingTutorial = SingleLiveEvent<Unit>()
    val showOnboardingTutorial: LiveData<Unit>
        get() = _showOnboardingTutorial

    private var hasCheckedCartTimeout: Boolean = false

    private var getUserCartJob: Job? = null

    init {
        showOnboardingTutorial()
        fetchUserAuthStatus()
    }

    private fun showOnboardingTutorial() = viewModelScope.launch {
        val onBoardingCompleted = getOnboardingCompletedUseCase(Unit).getSuccessDataOr(false)
        if (!onBoardingCompleted) {
            _showOnboardingTutorial.value = Unit
        }
    }

    private fun fetchUserAuthStatus() = viewModelScope.launch {
        getAuthProfileUseCase(Unit).collect {
            when (it) {
                is Resource.Success -> startObserveUserCart()
                is Resource.Error -> {
                    stopObserveUserCart()
                    _userCart.value = null
                }
                is Resource.Loading -> Unit
            }
        }
    }

    private fun startObserveUserCart() {
        getUserCartJob = viewModelScope.launch {
            getUserCartUseCase(Unit).collect {
                when (it) {
                    is Resource.Success -> {
                        if (!hasCheckedCartTimeout) checkUserCartTimeout(userCart = it.data)
                        _userCart.value = it.data
                    }
                    is Resource.Error -> {
                        showToastMessage(it.message)
                        _userCart.value = null
                    }
                    is Resource.Loading -> Unit
                }
            }
        }
    }

    private fun stopObserveUserCart() {
        getUserCartJob?.cancel()
        getUserCartJob = null
    }

    private fun checkUserCartTimeout(userCart: UserCart) = viewModelScope.launch {
        val isTimeout = checkCartTimeOutUseCase(userCart).getSuccessDataOr(false)
        if (isTimeout) {
            _navigateToTimeoutDialog.value = CartTimeoutProperty(
                cartItemsCount = userCart.itemsCount
            )
        }
        hasCheckedCartTimeout = true
    }

    fun onCurrentGraphChanged(currentGraphId: Int?) {
        _currentGraphId.value = currentGraphId
    }

    fun onTabScrollToTop() {
        _scrollToTop.value = Unit
    }

    fun onUpdateUserPhoto(uriString: String) = viewModelScope.launch {
        updateUserPhotoUseCase(uriString).collect {
            when (it) {
                is Resource.Success -> { _showSnackBarMessage.value =
                    context.getString(R.string.profile_user_photo_uploaded_message)
                }
                is Resource.Error -> showToastMessage(it.message)
                is Resource.Loading -> Unit
            }
        }
    }

    fun onClearUsersCart() = viewModelScope.launch {
        clearUserCartUseCase(Unit).collect {
            when (it) {
                is Resource.Success -> { _showSnackBarMessage.value =
                    context.getString(R.string.cart_cleared_message)
                }
                is Resource.Error -> showToastMessage(it.message)
                is Resource.Loading -> Unit
            }
        }
    }

    fun onTutorialDismissed() = viewModelScope.launch {
        val onboardingCompleted = getOnboardingCompletedUseCase(Unit).getSuccessDataOr(false)
        if (!onboardingCompleted) {
            updateOnboardingCompletedUseCase(true)
        }
    }
}