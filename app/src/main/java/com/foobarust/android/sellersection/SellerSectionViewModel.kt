package com.foobarust.android.sellersection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foobarust.android.sellerdetail.SellerDetailProperty
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by kevin on 1/3/21
 */

@HiltViewModel
class SellerSectionViewModel @Inject constructor(): ViewModel() {

    private val _backPressed = Channel<Unit>()
    val backPressed: Flow<Unit> = _backPressed.receiveAsFlow()

    private val _navigateToSellerDetail = Channel<SellerDetailProperty>()
    val navigateToSellerDetail: Flow<SellerDetailProperty> = _navigateToSellerDetail.receiveAsFlow()

    // Argument: section id
    private val _navigateToSellerSection = Channel<String>()
    val navigateToSellerSection: Flow<String> = _navigateToSellerSection.receiveAsFlow()

    // Argument: seller id
    private val _navigateToSellerMisc = Channel<String>()
    val navigateToSellerMisc: Flow<String> = _navigateToSellerMisc.receiveAsFlow()

    private val _toolbarTitle = MutableStateFlow<String?>(null)
    val toolbarTitle: StateFlow<String?> = _toolbarTitle.asStateFlow()

    private val _currentDestination = MutableStateFlow(-1)

    fun onBackPressed() {
        _backPressed.offer(Unit)
    }

    fun onNavigateToSellerDetail(sellerDetailProperty: SellerDetailProperty) = viewModelScope.launch {
        _navigateToSellerDetail.offer(sellerDetailProperty)
    }

    fun onNavigateToSellerMisc(sellerId: String) = viewModelScope.launch {
        _navigateToSellerMisc.offer(sellerId)
    }

    fun onNavigateToSellerSection(sectionId: String) = viewModelScope.launch {
        _navigateToSellerSection.offer(sectionId)
    }

    fun onUpdateCurrentDestination(destinationId: Int) {
        _currentDestination.value = destinationId
    }

    fun onUpdateToolbarTitle(title: String) {
        _toolbarTitle.value = title
    }
}
