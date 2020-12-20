package com.foobarust.android.main

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
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
import com.foobarust.domain.usecases.user.UpdateUserPhotoUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
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
    private val clearUserCartUseCase: ClearUserCartUseCase
) : BaseViewModel() {

    private val _scrollToTop = SingleLiveEvent<Unit>()
    val scrollToTop: LiveData<Unit>
        get() = _scrollToTop

    private val _showCartBottomBar = MutableLiveData(true)
    val showCartBottomBar: LiveData<Boolean>
        get() = _showCartBottomBar.distinctUntilChanged()

    private val _userCart = MutableLiveData<UserCart?>(null)
    val userCart: LiveData<UserCart?>
        get() = _userCart

    private val _showSnackBarMessage = SingleLiveEvent<String>()
    val showSnackBarMessage: LiveData<String>
        get() = _showSnackBarMessage

    private val _navigateToTimeoutDialog = SingleLiveEvent<CartTimeoutProperty>()
    val navigateToTimeoutDialog: LiveData<CartTimeoutProperty>
        get() = _navigateToTimeoutDialog

    private var hasCheckedCartTimeout: Boolean = false

    private var getUserCartJob: Job? = null

    init {
        fetchUserAuthStatus()
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
                cartItemsCount = userCart.itemsCount ?: 0
            )
        }
        hasCheckedCartTimeout = true
    }

    fun onTabScrollToTop() {
        _scrollToTop.value = Unit
    }

    fun showCartBottomBar() {
        _showCartBottomBar.value = true
    }

    fun hideCartBottomBar() {
        _showCartBottomBar.value = false
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
}