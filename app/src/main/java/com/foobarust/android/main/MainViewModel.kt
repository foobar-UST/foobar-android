package com.foobarust.android.main

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.domain.models.cart.UserCart
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.auth.GetAuthProfileUseCase
import com.foobarust.domain.usecases.cart.GetUserCartUseCase
import com.foobarust.domain.usecases.user.UpdateUserPhotoUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Created by kevin on 9/20/20
 */

class MainViewModel @ViewModelInject constructor(
    private val getAuthProfileUseCase: GetAuthProfileUseCase,
    private val getUserCartUseCase: GetUserCartUseCase,
    private val updateUserPhotoUseCase: UpdateUserPhotoUseCase
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
                    is Resource.Success -> _userCart.value = it.data
                    is Resource.Error -> showToastMessage(it.message)
                    is Resource.Loading -> Unit
                }
            }
        }
    }

    private fun stopObserveUserCart() {
        getUserCartJob?.cancel()
        getUserCartJob = null
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

    // TODO: fix updateUserPhoto
    fun updateUserPhoto(uriString: String) = viewModelScope.launch {
        updateUserPhotoUseCase(uriString).collect {
            when (it) {
                is Resource.Success -> showToastMessage("Photo uploaded.")
                is Resource.Error -> showToastMessage(it.message)
                is Resource.Loading -> Log.d("ProfileViewModel", "progress: ${it.progress}")
            }
        }
    }
}