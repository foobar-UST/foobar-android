package com.foobarust.android.sellermisc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foobarust.domain.models.seller.SellerDetail
import com.foobarust.domain.models.seller.SellerType
import com.foobarust.domain.states.Resource
import com.foobarust.domain.states.getSuccessDataOr
import com.foobarust.domain.usecases.maps.GetDirectionsParameters
import com.foobarust.domain.usecases.maps.GetDirectionsUseCase
import com.foobarust.domain.usecases.seller.GetSellerDetailUseCase
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by kevin on 10/11/20
 */

@HiltViewModel
class SellerMiscViewModel @Inject constructor(
    private val getSellerDetailUseCase: GetSellerDetailUseCase,
    private val getDirectionsUseCase: GetDirectionsUseCase
) : ViewModel() {

    private val _sellerDetail = MutableStateFlow<SellerDetail?>(null)

    private val _sellerMiscUiState = MutableStateFlow<SellerMiscUiState>(SellerMiscUiState.Loading)
    val sellerMiscUiState: StateFlow<SellerMiscUiState> = _sellerMiscUiState.asStateFlow()

    val sellerLocation: Flow<LatLng> = _sellerDetail
        .filterNotNull()
        .map {
            LatLng(
                it.location.locationPoint.latitude,
                it.location.locationPoint.longitude
            )
        }

    // Only show for off-campus seller
    val deliveryRoute: Flow<List<LatLng>?> = _sellerDetail
        .filterNotNull()
        .filter { it.type == SellerType.OFF_CAMPUS }
        .map {
            getDirectionsUseCase(
                GetDirectionsParameters(
                    destination = it.location.locationPoint
                )
            ).getSuccessDataOr(null)
        }
        .map { geolocation ->
            geolocation?.map { LatLng(it.latitude, it.longitude) }
        }


    fun onFetchSellerDetail(sellerId: String) = viewModelScope.launch {
        getSellerDetailUseCase(sellerId).collect {
            when (it) {
                is Resource.Success -> {
                    _sellerDetail.value = it.data
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

sealed class SellerMiscUiState {
    data class Success(val sellerDetail: SellerDetail) : SellerMiscUiState()
    data class Error(val message: String?) : SellerMiscUiState()
    object Loading : SellerMiscUiState()
}
