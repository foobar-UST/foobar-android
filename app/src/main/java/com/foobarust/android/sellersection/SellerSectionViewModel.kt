package com.foobarust.android.sellersection

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.sellerdetail.SellerDetailProperty
import com.foobarust.android.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by kevin on 1/3/21
 */

@HiltViewModel
class SellerSectionViewModel @Inject constructor(): ViewModel() {

    private val _backPressed = SingleLiveEvent<Unit>()
    val backPressed: LiveData<Unit>
        get() = _backPressed

    private val _navigateToSellerDetail = SingleLiveEvent<SellerDetailProperty>()
    val navigateToSellerDetail: LiveData<SellerDetailProperty>
        get() = _navigateToSellerDetail

    // Argument: section id
    private val _navigateToSellerSection = SingleLiveEvent<String>()
    val navigateToSellerSection: LiveData<String>
        get() = _navigateToSellerSection

    // Argument: seller id
    private val _navigateToSellerMisc = SingleLiveEvent<String>()
    val navigateToSellerMisc: LiveData<String>
        get() = _navigateToSellerMisc

    private val _toolbarTitle = MutableStateFlow<String?>(null)
    val toolbarTitle: LiveData<String?> = _toolbarTitle
        .asLiveData(viewModelScope.coroutineContext)

    private val _currentDestination = MutableStateFlow(-1)

    fun onBackPressed() {
        _backPressed.value = Unit
    }

    fun onNavigateToSellerDetail(sellerDetailProperty: SellerDetailProperty) = viewModelScope.launch {
        _navigateToSellerDetail.value = sellerDetailProperty
    }

    fun onNavigateToSellerMisc(sellerId: String) = viewModelScope.launch {
        _navigateToSellerMisc.value = sellerId
    }

    fun onNavigateToSellerSection(sectionId: String) = viewModelScope.launch {
        _navigateToSellerSection.value = sectionId
    }

    fun onUpdateCurrentDestination(destinationId: Int) {
        _currentDestination.value = destinationId
    }

    fun onUpdateToolbarTitle(title: String) {
        _toolbarTitle.value = title
    }
}
