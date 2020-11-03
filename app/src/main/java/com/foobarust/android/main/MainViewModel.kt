package com.foobarust.android.main

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.user.UpdateUserPhotoUseCase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Created by kevin on 9/20/20
 */

class MainViewModel @ViewModelInject constructor(
    private val updateUserPhotoUseCase: UpdateUserPhotoUseCase
) : BaseViewModel() {

    private val _scrollToTop = SingleLiveEvent<Unit>()
    val scrollToTop: LiveData<Unit>
        get() = _scrollToTop

    private val _showCartBottomBar = MutableLiveData(true)
    val showCartBottomBar: LiveData<Boolean>
        get() = _showCartBottomBar.distinctUntilChanged()

    fun onTabScrollToTop() {
        _scrollToTop.value = Unit
    }

    fun showCartBottomBar() {
        _showCartBottomBar.value = true
    }

    fun hideCartBottomBar() {
        _showCartBottomBar.value = false
    }

    fun updateUserPhoto(photoUriString: String) = viewModelScope.launch {
        updateUserPhotoUseCase(photoUriString).collect {
            when (it) {
                is Resource.Success -> showToastMessage("Photo uploaded.")
                is Resource.Error -> showToastMessage(it.message)
                is Resource.Loading -> Log.d("ProfileViewModel", "progress: ${it.progress}")
            }
        }
    }
}