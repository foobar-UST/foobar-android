package com.foobarust.android.checkout

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.foobarust.android.R
import com.foobarust.android.sellerdetail.SellerItemDetailProperty
import com.foobarust.android.utils.SingleLiveEvent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine

/**
 * Created by kevin on 1/9/21
 */

class CheckoutViewModel @ViewModelInject constructor(
    @ApplicationContext context: Context
) : ViewModel() {

    private val _showUpdatingProgress = MutableLiveData(false)
    val showUpdatingProgress: LiveData<Boolean>
        get() = _showUpdatingProgress

    private val _navigateToSellerDetail = SingleLiveEvent<String>()
    val navigateToSellerDetail: LiveData<String>
        get() = _navigateToSellerDetail

    private val _navigateToSellerMisc = SingleLiveEvent<String>()
    val navigateToSellerMisc: LiveData<String>
        get() = _navigateToSellerMisc

    private val _navigateToSellerItemDetail = SingleLiveEvent<SellerItemDetailProperty>()
    val navigateToSellerItemDetail: LiveData<SellerItemDetailProperty>
        get() = _navigateToSellerItemDetail

    private val _backPressed = SingleLiveEvent<Unit>()
    val backPressed: LiveData<Unit>
        get() = _backPressed

    private val _currentDestination = MutableStateFlow(-1)
    private val _cartItemsCount = MutableStateFlow(0)

    val appBarTitle: LiveData<String> = _currentDestination
        .combine(_cartItemsCount.asStateFlow()) { destinationId, cartItemsCount ->
            when (destinationId) {
                R.id.cartFragment -> context.getString(R.string.checkout_app_bar_title_cart, cartItemsCount)
                R.id.paymentFragment -> context.getString(R.string.checkout_app_bar_title_payment)
                R.id.orderSuccessFragment -> context.getString(R.string.checkout_app_bar_title_order_success)
                else -> ""
            }
        }
        .asLiveData(viewModelScope.coroutineContext)

    fun setShowUpdatingProgress(isShow: Boolean) {
        _showUpdatingProgress.value = isShow
    }

    fun onBackPressed() {
        _backPressed.value = Unit
    }

    fun onNavigateToSellerDetail(sellerId: String) {
        _navigateToSellerDetail.value = sellerId
    }

    fun onNavigateToSellerMisc(sellerId: String) {
        _navigateToSellerMisc.value = sellerId
    }

    fun onNavigateToSellerItemDetail(property: SellerItemDetailProperty) {
        _navigateToSellerItemDetail.value = property
    }

    fun onUpdateCurrentDestination(destinationId: Int) {
        _currentDestination.value = destinationId
    }

    fun onUpdateCartItemsCount(cartItemsCount: Int) {
        _cartItemsCount.value = cartItemsCount
    }
}