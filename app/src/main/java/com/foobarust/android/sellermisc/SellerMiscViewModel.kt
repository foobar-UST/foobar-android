package com.foobarust.android.sellermisc

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.common.UiState
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
    val sellerDetail: LiveData<SellerDetail> = _sellerDetail.filterNotNull()
        .asLiveData(viewModelScope.coroutineContext)

    val latLng: LiveData<LatLng> = _sellerDetail
        .filterNotNull()
        .map {
            LatLng(
                it.location.locationPoint.latitude,
                it.location.locationPoint.longitude
            )
        }
        .asLiveData(viewModelScope.coroutineContext)

    val polyline: LiveData<List<LatLng>?> = _sellerDetail
        .filterNotNull()
        .filter { it.type == SellerType.OFF_CAMPUS }
        .map {
            getDirectionsUseCase(
                GetDirectionsParameters(
                    latitude = it.location.locationPoint.latitude,
                    longitude = it.location.locationPoint.longitude
                )
            ).getSuccessDataOr(null)
        }
        .map { geolocation ->
            geolocation?.map { LatLng(it.latitude, it.longitude) }
        }
        .asLiveData(viewModelScope.coroutineContext)


    fun onFetchSellerDetail(sellerId: String) = viewModelScope.launch {
        getSellerDetailUseCase(sellerId).collect {
            when (it) {
                is Resource.Success -> {
                    _sellerDetail.value = it.data
                    setUiState(UiState.Success)
                }
                is Resource.Error -> {
                    setUiState(UiState.Error(it.message))
                }
                is Resource.Loading -> {
                    setUiState(UiState.Loading)
                }
            }
        }
    }
}
