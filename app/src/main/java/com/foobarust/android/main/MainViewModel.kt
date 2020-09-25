package com.foobarust.android.main

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.foobarust.android.common.BaseViewModel
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

    fun updateUserPhoto(photoUriString: String) = viewModelScope.launch {
        updateUserPhotoUseCase(photoUriString).collect {
            when (it) {
                is Resource.Success -> showMessage("Photo uploaded.")
                is Resource.Error -> showMessage(it.message)
                is Resource.Loading -> Log.d("ProfileViewModel", "progress: ${it.progress}")
            }
        }
    }
}