package com.foobarust.android.sellermisc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foobarust.domain.models.common.GeolocationPoint
import com.foobarust.domain.models.seller.SellerDetail
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.seller.GetSellerDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by kevin on 10/11/20
 */

@HiltViewModel
class SellerMiscViewModel @Inject constructor(
    private val getSellerDetailUseCase: GetSellerDetailUseCase
) : ViewModel() {

    private val _sellerId = ConflatedBroadcastChannel<String>()

    private val _sellerLocation = MutableStateFlow<GeolocationPoint?>(null)
    val sellerLocation: StateFlow<GeolocationPoint?> = _sellerLocation.asStateFlow()

    private val _sellerMiscUiState = MutableStateFlow<SellerMiscUiState>(SellerMiscUiState.Loading)
    val sellerMiscUiState: StateFlow<SellerMiscUiState> = _sellerMiscUiState.asStateFlow()

    init {
        viewModelScope.launch {
            _sellerId.asFlow().flatMapLatest {
                getSellerDetailUseCase(it)
            }.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        _sellerLocation.value = it.data.location.locationPoint
                        _sellerMiscUiState.value = SellerMiscUiState.Success(it.data)
                    }
                    is Resource.Error -> {
                        _sellerMiscUiState.value = SellerMiscUiState.Error(it.message)
                    }
                    is Resource.Loading -> {
                        _sellerMiscUiState.value = SellerMiscUiState.Loading
                    }
                }
            }
        }
    }

    fun onFetchSellerMisc(sellerId: String) {
        _sellerId.offer(sellerId)
    }
}

sealed class SellerMiscUiState {
    data class Success(val sellerDetail: SellerDetail) : SellerMiscUiState()
    data class Error(val message: String?) : SellerMiscUiState()
    object Loading : SellerMiscUiState()
}
