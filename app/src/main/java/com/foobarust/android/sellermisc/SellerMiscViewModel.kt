package com.foobarust.android.sellermisc

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.sellermisc.SellerMiscListModel.*
import com.foobarust.android.states.UiFetchState
import com.foobarust.domain.models.seller.SellerDetail
import com.foobarust.domain.models.seller.getNormalizedAddress
import com.foobarust.domain.models.seller.getNormalizedName
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.seller.GetSellerDetailUseCase
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

/**
 * Created by kevin on 10/11/20
 */

class SellerMiscViewModel @ViewModelInject constructor(
    private val getSellerDetailUseCase: GetSellerDetailUseCase
) : BaseViewModel() {

    private val _latLng = MutableLiveData<LatLng>()
    val latLng: LiveData<LatLng>
        get() = _latLng

    private val _sellerMiscListModels = MutableLiveData<List<SellerMiscListModel>>()
    val sellerMiscListModels: LiveData<List<SellerMiscListModel>>
        get() = _sellerMiscListModels

    fun onFetchSellerDetail(sellerId: String) = viewModelScope.launch {
        setUiFetchState(UiFetchState.Loading)

        when (val result = getSellerDetailUseCase(sellerId)) {
            is Resource.Success -> {
                val sellerDetail = result.data
                _latLng.value = LatLng(
                    sellerDetail.location.geolocation.latitude,
                    sellerDetail.location.geolocation.longitude
                )
                setUiFetchState(UiFetchState.Success)
                buildSellerMiscList(sellerDetail)
            }
            is Resource.Error -> setUiFetchState(UiFetchState.Error(result.message))
        }
    }

    private fun buildSellerMiscList(sellerDetail: SellerDetail) {
        _sellerMiscListModels.value = buildList {
            addAll(listOf(
                SellerMiscAddressModel(
                    name = sellerDetail.getNormalizedName(),
                    address = sellerDetail.location.getNormalizedAddress(),
                ),
                SellerMiscOpeningHoursModel(
                    openingHours = sellerDetail.openingHours
                ),
                SellerMiscContactModel(
                    phoneNum = sellerDetail.phoneNum,
                    website = sellerDetail.website
                )
            ))

            sellerDetail.description?.let {
                add(SellerMiscDescriptionModel(description = it))
            }
        }
    }
}
