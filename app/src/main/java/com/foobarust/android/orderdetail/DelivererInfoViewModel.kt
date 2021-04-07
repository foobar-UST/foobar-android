package com.foobarust.android.orderdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foobarust.domain.models.user.UserDelivery
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.user.GetDelivererProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * Created by kevin on 4/7/21
 */

@HiltViewModel
class DelivererInfoViewModel @Inject constructor(
    getDelivererProfileUseCase: GetDelivererProfileUseCase
) : ViewModel() {

    private val _delivererId = MutableStateFlow<String?>(null)

    val delivererProfile: StateFlow<Resource<UserDelivery>> = _delivererId
        .filterNotNull()
        .flatMapLatest { getDelivererProfileUseCase(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = Resource.Loading()
        )

    fun onFetchDelivererProfile(delivererId: String) {
        _delivererId.value = delivererId
    }
}