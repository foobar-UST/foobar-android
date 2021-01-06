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
import com.foobarust.domain.models.user.UserDetail
import com.foobarust.domain.states.Resource
import com.foobarust.domain.states.getSuccessDataOr
import com.foobarust.domain.usecases.cart.CheckCartTimeOutUseCase
import com.foobarust.domain.usecases.cart.ClearUserCartUseCase
import com.foobarust.domain.usecases.cart.GetUserCartUseCase
import com.foobarust.domain.usecases.onboarding.GetOnboardingCompletedUseCase
import com.foobarust.domain.usecases.onboarding.UpdateOnboardingCompletedUseCase
import com.foobarust.domain.usecases.user.GetUserDetailUseCase
import com.foobarust.domain.usecases.user.UpdateUserPhotoUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Created by kevin on 9/20/20
 */

class MainViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    getUserDetailUseCase: GetUserDetailUseCase,
    getUserCartUseCase: GetUserCartUseCase,
    private val updateUserPhotoUseCase: UpdateUserPhotoUseCase,
    private val checkCartTimeOutUseCase: CheckCartTimeOutUseCase,
    private val clearUserCartUseCase: ClearUserCartUseCase,
    private val getOnboardingCompletedUseCase: GetOnboardingCompletedUseCase,
    private val updateOnboardingCompletedUseCase: UpdateOnboardingCompletedUseCase
) : BaseViewModel() {

    val userDetail: SharedFlow<Resource<UserDetail>> = getUserDetailUseCase(Unit)
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            replay = 1
        )

    val userCart: SharedFlow<Resource<UserCart?>> = getUserCartUseCase(Unit)
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            replay = 1
        )

    val userCartLiveData: LiveData<UserCart?> = userCart
        .map { it.getSuccessDataOr(null) }
        .asLiveData(viewModelScope.coroutineContext)

    val topLevelDestinations = listOf(
        R.id.sellerFragment,
        R.id.orderFragment,
        R.id.exploreFragment,
        R.id.settingsFragment
    )

    private val _currentNavGraphId = MutableStateFlow<Int?>(null)

    val showCartBottomBar: LiveData<Boolean> = _currentNavGraphId
        .asStateFlow()
        .combine(
            userCart.map { it.getSuccessDataOr(null) }
        ) { currentGraphId, userCart ->
            // Show bottom bar only in seller tab
            currentGraphId == R.id.navigation_seller &&
                userCart != null &&
                userCart.itemsCount > 0
        }
        .distinctUntilChanged()
        .asLiveData(viewModelScope.coroutineContext)


    private val _scrollToTop = SingleLiveEvent<Unit>()
    val scrollToTop: LiveData<Unit>
        get() = _scrollToTop

    private val _showSnackBarMessage = SingleLiveEvent<String>()
    val showSnackBarMessage: LiveData<String>
        get() = _showSnackBarMessage

    private val _navigateToTimeoutDialog = SingleLiveEvent<CartTimeoutProperty>()
    val navigateToTimeoutDialog: LiveData<CartTimeoutProperty>
        get() = _navigateToTimeoutDialog

    private val _showOnboardingTutorial = SingleLiveEvent<Unit>()
    val showOnboardingTutorial: LiveData<Unit>
        get() = _showOnboardingTutorial

    private val _launchCustomTab = SingleLiveEvent<String>()
    val launchCustomTab: LiveData<String>
        get() = _launchCustomTab

    private var hasCheckedCartTimeout: Boolean = false

    init {
        showOnboardingTutorial()
        fetchUserCart()
    }

    private fun fetchUserCart() = viewModelScope.launch {
        userCart.collect { result ->
            when (result) {
                is Resource.Success -> {
                    val userCart = result.data
                    //_userCart.value = userCart
                    if (!hasCheckedCartTimeout && userCart != null) {
                        checkUserCartTimeout(userCart = userCart)
                    }
                }
                is Resource.Error -> showToastMessage(result.message)
                is Resource.Loading -> Unit
            }
        }
    }

    fun onCurrentNavGraphChanged(destinationId: Int) {
        _currentNavGraphId.value = destinationId
    }

    fun onTabScrollToTop() {
        _scrollToTop.value = Unit
    }

    fun onUpdateUserPhoto(uriString: String) = viewModelScope.launch {
        updateUserPhotoUseCase(uriString).collect {
            when (it) {
                is Resource.Success -> _showSnackBarMessage.value = context.getString(
                        R.string.profile_user_photo_uploaded_message
                    )
                is Resource.Error -> showToastMessage(it.message)
                is Resource.Loading -> Unit
            }
        }
    }

    fun onClearUsersCart() = viewModelScope.launch {
        clearUserCartUseCase(Unit).collect {
            when (it) {
                is Resource.Success -> _showSnackBarMessage.value = context.getString(
                        R.string.cart_cleared_message
                    )
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

    fun onLaunchCustomTab(url: String) {
        _launchCustomTab.value = url
    }

    private fun showOnboardingTutorial() = viewModelScope.launch {
        val completed = getOnboardingCompletedUseCase(Unit).getSuccessDataOr(false)
        if (!completed) {
            _showOnboardingTutorial.value = Unit
        }
    }

    private suspend fun checkUserCartTimeout(userCart: UserCart) {
        val isTimeout = checkCartTimeOutUseCase(userCart).getSuccessDataOr(false)
        if (isTimeout) {
            _navigateToTimeoutDialog.value = CartTimeoutProperty(
                cartItemsCount = userCart.itemsCount
            )
        }
        hasCheckedCartTimeout = true
    }
}