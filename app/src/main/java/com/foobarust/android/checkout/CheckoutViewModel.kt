package com.foobarust.android.checkout

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.sellerdetail.SellerDetailProperty
import com.foobarust.android.sellerdetail.SellerItemDetailProperty
import com.foobarust.android.sellersection.SellerSectionProperty
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.domain.models.cart.UserCartItem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Created by kevin on 1/9/21
 */

class CheckoutViewModel @ViewModelInject constructor() : ViewModel() {

    private val _toolbarTitle = MutableStateFlow<String?>(null)
    val toolbarTitle: LiveData<String?> = _toolbarTitle
        .asLiveData(viewModelScope.coroutineContext)

    private val _submitButtonTitle = MutableStateFlow<String?>(null)
    val submitButtonTitle: LiveData<String?> = _submitButtonTitle
        .asLiveData(viewModelScope.coroutineContext)

    private val _showSubmitButton = MutableStateFlow(false)
    val showSubmitButton: LiveData<Boolean> = _showSubmitButton
        .asLiveData(viewModelScope.coroutineContext)


    private val _showUpdatingProgress = MutableStateFlow(false)
    val showUpdatingProgress: LiveData<Boolean> = _showUpdatingProgress
        .asLiveData(viewModelScope.coroutineContext)

    private val _navigateToSellerDetail = SingleLiveEvent<SellerDetailProperty>()
    val navigateToSellerDetail: LiveData<SellerDetailProperty>
        get() = _navigateToSellerDetail

    private val _navigateToSellerMisc = SingleLiveEvent<String>()
    val navigateToSellerMisc: LiveData<String>
        get() = _navigateToSellerMisc

    private val _navigateToSellerItemDetail = SingleLiveEvent<SellerItemDetailProperty>()
    val navigateToSellerItemDetail: LiveData<SellerItemDetailProperty>
        get() = _navigateToSellerItemDetail

    private val _navigateToSellerSection = SingleLiveEvent<SellerSectionProperty>()
    val navigateToSellerSection: LiveData<SellerSectionProperty>
        get() = _navigateToSellerSection

    // Handle dialog back pressed event, will be observed by a single child fragment at a time
    private val _backPressed = SingleLiveEvent<Unit>()
    val backPressed: LiveData<Unit>
        get() = _backPressed

    // Handle submit button click event, will be observed by multiple child fragments
    private val _onClickSubmitButton = MutableSharedFlow<Unit>()
    val onClickSubmitButton: SharedFlow<Unit> = _onClickSubmitButton.asSharedFlow()

    private val _currentDestination = MutableStateFlow(-1)
    private val _cartItemsCount = MutableStateFlow(0)

    // Expand collapsing toolbar during navigation
    val expandCollapsingToolbar: LiveData<Unit> = _currentDestination
        .map { Unit }
        .asLiveData(viewModelScope.coroutineContext)

    var savedOrderNotes: String? = null
    var savedPaymentIdentifier: String? = null

    fun onSubmitButtonClicked() = viewModelScope.launch {
        _onClickSubmitButton.emit(Unit)
    }

    fun onShowSubmitButton(isShow: Boolean) {
        _showSubmitButton.value = isShow
    }

    fun setShowUpdatingProgress(isShow: Boolean) {
        _showUpdatingProgress.value = isShow
    }

    fun onBackPressed() {
        _backPressed.value = Unit
    }

    fun onNavigateToSellerDetail(sellerId: String, sectionId: String?) {
        _navigateToSellerDetail.value = SellerDetailProperty(
            sellerId = sellerId,
            sectionId = sectionId
        )
    }

    fun onNavigateToSellerMisc(sellerId: String) {
        _navigateToSellerMisc.value = sellerId
    }

    fun onNavigateToSellerSection(sellerId: String, sectionId: String?) {
        _navigateToSellerSection.value = SellerSectionProperty(
            sellerId = sellerId,
            sectionId = sectionId!!
        )
    }

    fun onNavigateToSellerItemDetail(userCartItem: UserCartItem) {
        _navigateToSellerItemDetail.value = SellerItemDetailProperty(
            sellerId = userCartItem.itemSellerId,
            itemId = userCartItem.itemId,
            cartItemId = userCartItem.id,
            amounts = userCartItem.amounts
        )
    }

    fun onUpdateCurrentDestination(destinationId: Int) {
        _currentDestination.value = destinationId
    }

    fun onUpdateCartItemsCount(cartItemsCount: Int) {
        _cartItemsCount.value = cartItemsCount
    }

    fun onUpdateToolbarTitle(title: String) {
        _toolbarTitle.value = title
    }

    fun onUpdateSubmitButtonTitle(title: String) {
        _submitButtonTitle.value = title
    }
}