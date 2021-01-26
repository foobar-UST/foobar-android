package com.foobarust.android.sellersection

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.common.UiState
import com.foobarust.android.sellerdetail.SellerDetailProperty
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.domain.models.seller.SellerSectionDetail
import com.foobarust.domain.models.seller.getNormalizedTitle
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.seller.GetSellerSectionDetailParameters
import com.foobarust.domain.usecases.seller.GetSellerSectionDetailUseCase
import com.foobarust.domain.utils.cancelIfActive
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

/**
 * Created by kevin on 1/3/21
 */

@HiltViewModel
class SellerSectionViewModel @Inject constructor(
    private val getSellerSectionDetailUseCase: GetSellerSectionDetailUseCase
) : BaseViewModel() {

    private val _sectionDetail = MutableSharedFlow<SellerSectionDetail?>()
    val sectionDetail: SharedFlow<SellerSectionDetail?> = _sectionDetail
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            replay = 1
        )

    private val _backPressed = SingleLiveEvent<Unit>()
    val backPressed: LiveData<Unit>
        get() = _backPressed

    private val _navigateToSellerDetail = SingleLiveEvent<SellerDetailProperty>()
    val navigateToSellerDetail: LiveData<SellerDetailProperty>
        get() = _navigateToSellerDetail

    private val _navigateToSellerSection = SingleLiveEvent<SellerSectionProperty>()
    val navigateToSellerSection: LiveData<SellerSectionProperty>
        get() = _navigateToSellerSection

    private val _navigateToSellerMisc = SingleLiveEvent<String>()
    val navigateToSellerMisc: LiveData<String>
        get() = _navigateToSellerMisc

    private val _currentDestination = MutableStateFlow(-1)

    private var fetchSectionDetailJob: Job? = null

    val toolbarTitle: LiveData<String> = _sectionDetail
        .asSharedFlow()
        .map { it?.getNormalizedTitle() ?: "" }
        .asLiveData(viewModelScope.coroutineContext)

    fun onFetchSectionDetail(property: SellerSectionProperty) {
        fetchSectionDetailJob?.cancelIfActive()
        fetchSectionDetailJob = viewModelScope.launch {
            val params = GetSellerSectionDetailParameters(
                sellerId = property.sellerId,
                sectionId = property.sectionId
            )

            getSellerSectionDetailUseCase(params).collect {
                when (it) {
                    is Resource.Success -> {
                        _sectionDetail.emit(it.data)
                        setUiState(UiState.Success)
                    }
                    is Resource.Error -> {
                        _sectionDetail.emit(null)
                        setUiState(UiState.Error(it.message))
                    }
                    is Resource.Loading -> {
                        setUiState(UiState.Loading)
                    }
                }
            }
        }
    }

    fun onBackPressed() {
        _backPressed.value = Unit
    }

    fun onNavigateToSellerDetail() = viewModelScope.launch {
        val sectionDetail = sectionDetail.first()
        sectionDetail?.let {
            _navigateToSellerDetail.value = SellerDetailProperty(
                sellerId = it.sellerId,
                sectionId = it.id
            )
        }
    }

    fun onNavigateToSellerMisc() = viewModelScope.launch {
        val sectionDetail = sectionDetail.first()
        sectionDetail?.let {
            _navigateToSellerMisc.value = it.sellerId
        }
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

    fun onUpdateCurrentDestination(destinationId: Int) {
        _currentDestination.value = destinationId
    }
}

@Parcelize
data class SellerSectionProperty(
    val sectionId: String,
    val sellerId: String
) : Parcelable