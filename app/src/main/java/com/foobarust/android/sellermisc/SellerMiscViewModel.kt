package com.foobarust.android.sellermisc

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.states.UiState
import com.foobarust.domain.models.seller.SellerDetail
import com.foobarust.domain.models.seller.SellerType
import com.foobarust.domain.states.Resource
import com.foobarust.domain.states.getSuccessDataOr
import com.foobarust.domain.usecases.maps.GetDirectionsParameters
import com.foobarust.domain.usecases.maps.GetDirectionsUseCase
import com.foobarust.domain.usecases.seller.GetSellerDetailUseCase
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Created by kevin on 10/11/20
 */

class SellerMiscViewModel @ViewModelInject constructor(
    private val getSellerDetailUseCase: GetSellerDetailUseCase,
    private val getDirectionsUseCase: GetDirectionsUseCase
) : BaseViewModel() {

    private val _sellerDetail = MutableStateFlow<SellerDetail?>(null)
    val sellerDetail: LiveData<SellerDetail> = _sellerDetail.asStateFlow()
        .filterNotNull()
        .asLiveData(viewModelScope.coroutineContext)

    val latLng: LiveData<LatLng> = _sellerDetail.asStateFlow()
        .filterNotNull()
        .map {
            LatLng(
                it.location.geolocation.latitude,
                it.location.geolocation.longitude
            )
        }
        .asLiveData(viewModelScope.coroutineContext)

    val polyline: LiveData<List<LatLng>?> = _sellerDetail.asStateFlow()
        .filterNotNull()
        .filter { it.type == SellerType.OFF_CAMPUS }
        .map {
            getDirectionsUseCase(
                GetDirectionsParameters(
                    sellerLatitude = it.location.geolocation.latitude,
                    sellerLongitude = it.location.geolocation.longitude
                )
            ).getSuccessDataOr(null)
        }
        .map { geolocation ->
            geolocation?.map { LatLng(it.latitude, it.longitude) }
        }
        .asLiveData(viewModelScope.coroutineContext)


    fun onFetchSellerDetail(sellerId: String) = viewModelScope.launch {
        setUiState(UiState.Loading)
        when (val result = getSellerDetailUseCase(sellerId)) {
            is Resource.Success -> {
                _sellerDetail.value = result.data
                setUiState(UiState.Success)
            }
            is Resource.Error -> setUiState(UiState.Error(result.message))
        }
    }
}
