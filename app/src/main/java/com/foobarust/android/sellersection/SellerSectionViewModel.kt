package com.foobarust.android.sellersection

import android.os.Parcelable
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.states.UiFetchState
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.domain.models.seller.SellerSectionDetail
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.seller.GetSellerSectionDetailParameters
import com.foobarust.domain.usecases.seller.GetSellerSectionDetailUseCase
import com.foobarust.domain.utils.cancelIfActive
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

/**
 * Created by kevin on 1/3/21
 */

class SellerSectionViewModel @ViewModelInject constructor(
    private val getSellerSectionDetailUseCase: GetSellerSectionDetailUseCase
) : BaseViewModel() {

    private val _sectionDetail = MutableSharedFlow<SellerSectionDetail?>()
    val sectionDetail: SharedFlow<SellerSectionDetail?> = _sectionDetail
        .asSharedFlow()
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            replay = 1
        )
    val sectionDetailLiveData: LiveData<SellerSectionDetail?> = sectionDetail
        .asLiveData(viewModelScope.coroutineContext)

    private val _backPressed = SingleLiveEvent<Unit>()
    val backPressed: LiveData<Unit>
        get() = _backPressed

    private val _navigateToSellerDetail = SingleLiveEvent<String>()
    val navigateToSellerDetail: LiveData<String>
        get() = _navigateToSellerDetail

    private val _navigateToSellerSection = SingleLiveEvent<SellerSectionProperty>()
    val navigateToSellerSection: LiveData<SellerSectionProperty>
        get() = _navigateToSellerSection

    var currentDestinationId: Int? = null

    private var fetchSectionDetailJob: Job? = null

    fun onFetchSectionDetail(property: SellerSectionProperty) {
        setUiFetchState(UiFetchState.Loading)
        fetchSectionDetailJob?.cancelIfActive()
        fetchSectionDetailJob = viewModelScope.launch {
            when (val result = getSellerSectionDetailUseCase(
                GetSellerSectionDetailParameters(
                    sellerId = property.sellerId,
                    sectionId = property.sectionId
                )
            )) {
                is Resource.Success -> {
                    _sectionDetail.emit(result.data)
                    setUiFetchState(UiFetchState.Success)
                }
                is Resource.Loading -> Unit
                is Resource.Error -> {
                    _sectionDetail.emit(null)
                    setUiFetchState(UiFetchState.Error(result.message))
                }
            }
        }
    }

    fun onBackPressed() {
        _backPressed.value = Unit
    }

    fun onNavigateToSellerDetail() = viewModelScope.launch {
        val sellerId = sectionDetail.first()?.sellerId
        sellerId?.let { _navigateToSellerDetail.value = it }
    }

    fun onNavigateToSellerSection(sectionId: String) = viewModelScope.launch {
        val sellerId = sectionDetail.first()?.sellerId
        sellerId?.let {
            _navigateToSellerSection.value = SellerSectionProperty(
                sectionId = sectionId,
                sellerId = it
            )
        }
    }
}

@Parcelize
data class SellerSectionProperty(
    val sectionId: String,
    val sellerId: String
) : Parcelable