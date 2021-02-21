package com.foobarust.android.checkout

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.sellerdetail.SellerDetailProperty
import com.foobarust.android.selleritem.SellerItemDetailProperty
import com.foobarust.domain.models.cart.UserCartItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by kevin on 1/9/21
 */

@HiltViewModel
class CheckoutViewModel @Inject constructor() : ViewModel() {

    private val _toolbarTitle = MutableStateFlow<String?>(null)
    val toolbarTitle: LiveData<String?> = _toolbarTitle
        .asLiveData(viewModelScope.coroutineContext)

    private val _submitButtonTitle = MutableStateFlow<String?>(null)
    val submitButtonTitle: LiveData<String?> = _submitButtonTitle
        .asLiveData(viewModelScope.coroutineContext)

    private val _showSubmitButton = MutableStateFlow(false)
    val showSubmitButton: LiveData<Boolean> = _showSubmitButton
        .asLiveData(viewModelScope.coroutineContext)

    private val _showLoadingProgressBar = MutableStateFlow(false)
    val showLoadingProgressBar: LiveData<Boolean> = _showLoadingProgressBar
        .asLiveData(viewModelScope.coroutineContext)

    // Handle submit button click event, will be observed by multiple child fragments
    private val _onClickSubmitButton = MutableSharedFlow<Unit>()
    val onClickSubmitButton: SharedFlow<Unit> = _onClickSubmitButton.asSharedFlow()

    private val _currentDestination = MutableStateFlow(-1)

    private val _cartItemsCount = MutableStateFlow(0)

    private val _navigateToSellerDetail = Channel<SellerDetailProperty>()
    val navigateToSellerDetail: Flow<SellerDetailProperty> = _navigateToSellerDetail.receiveAsFlow()

    private val _navigateToSellerMisc = Channel<String>()
    val navigateToSellerMisc: Flow<String> = _navigateToSellerMisc.receiveAsFlow()

    private val _navigateToSellerItemDetail = Channel<SellerItemDetailProperty>()
    val navigateToSellerItemDetail: Flow<SellerItemDetailProperty> = _navigateToSellerItemDetail
        .receiveAsFlow()

    private val _navigateToSellerSection = Channel<String>()
    val navigateToSellerSection: Flow<String> = _navigateToSellerSection.receiveAsFlow()

    // Handle dialog back pressed event, will be observed by a single child fragment at a time
    private val _backPressed = Channel<Unit>()
    val backPressed: Flow<Unit> = _backPressed.receiveAsFlow()

    private val _dismissCheckoutDialog = Channel<Unit>()
    val dismissCheckoutDialog: Flow<Unit> = _dismissCheckoutDialog.receiveAsFlow()

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

    fun showLoadingProgressBar(isShow: Boolean) {
        _showLoadingProgressBar.value = isShow
    }

    fun onBackPressed() {
        _backPressed.offer(Unit)
    }

    fun onNavigateToSellerDetail(sellerId: String, sectionId: String?) {
        _navigateToSellerDetail.offer(
            SellerDetailProperty(
                sellerId = sellerId,
                sectionId = sectionId
            )
        )
    }

    fun onNavigateToSellerMisc(sellerId: String) {
        _navigateToSellerMisc.offer(sellerId)
    }

    fun onNavigateToSellerSection(sectionId: String) {
        _navigateToSellerSection.offer(sectionId)
    }

    fun onNavigateToSellerItemDetail(userCartItem: UserCartItem) {
        _navigateToSellerItemDetail.offer(
            SellerItemDetailProperty(
                sellerId = userCartItem.itemSellerId,
                itemId = userCartItem.itemId,
                cartItemId = userCartItem.id,
                amounts = userCartItem.amounts
            )
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

    fun onDismissCheckoutDialog() {
        _dismissCheckoutDialog.offer(Unit)
    }

    fun onClearCheckoutData() {
        savedOrderNotes = null
        savedPaymentIdentifier = null
    }
}