package com.foobarust.android.cart

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.cart.CartListModel.*
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.states.UiFetchState
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.domain.models.cart.UserCart
import com.foobarust.domain.models.cart.UserCartItem
import com.foobarust.domain.models.seller.SellerBasic
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.cart.*
import com.foobarust.domain.usecases.seller.GetSellerBasicUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Created by kevin on 12/1/20
 */
class CartViewModel @ViewModelInject constructor(
    private val getSellerBasicUseCase: GetSellerBasicUseCase,
    private val getUserCartUseCase: GetUserCartUseCase,
    private val getUserCartItemsUseCase: GetUserCartItemsUseCase,
    private val removeUserCartItemUseCase: RemoveUserCartItemUseCase,
    private val checkCartTimeOutUseCase: CheckCartTimeOutUseCase,
    private val clearUserCartUseCase: ClearUserCartUseCase
) : BaseViewModel() {

    private val _userCart = MutableStateFlow<UserCart?>(null)
    private val userCartFlow = _userCart.asStateFlow().filterNotNull()

    private val _sellerBasic = MutableStateFlow<SellerBasic?>(null)
    private val sellerBasicFlow = _sellerBasic.asStateFlow().filterNotNull()

    private val _cartItems = MutableStateFlow<List<UserCartItem>>(emptyList())
    private val cartItemsFlow = _cartItems.asStateFlow()
    val cartItemsLiveData = cartItemsFlow.asLiveData(viewModelScope.coroutineContext)

    private val _cartListModels = MutableLiveData<List<CartListModel>>()
    val cartListModels: LiveData<List<CartListModel>>
        get() = _cartListModels

    private val _showCartTimeoutMessage = SingleLiveEvent<Unit>()
    val showCartTimeoutMessage: LiveData<Unit>
        get() = _showCartTimeoutMessage

    val showSyncAction: LiveData<Boolean> = _userCart.asStateFlow()
        .filterNotNull()
        .map { it.syncRequired }
        .distinctUntilChanged()
        .asLiveData(viewModelScope.coroutineContext)

    fun onFetchCartItems() {
        fetchUserCart()
        fetchSellerBasic()
        fetchUserCartItems()
        clearCartWhenTimeout()
        buildCartList()
    }

    private fun fetchUserCart() = viewModelScope.launch {
        getUserCartUseCase(Unit).collect {
            when (it) {
                is Resource.Success -> _userCart.value = it.data
                is Resource.Loading -> setUiFetchState(UiFetchState.Loading)
                is Resource.Error -> setUiFetchState(UiFetchState.Error(it.message))
            }
        }
    }

    private fun fetchSellerBasic() = viewModelScope.launch {
        _userCart.asStateFlow().filterNotNull()
            .collect { userCart ->
                userCart.sellerId?.let {
                    when (val result = getSellerBasicUseCase(it)) {
                        is Resource.Success -> _sellerBasic.value = result.data
                        is Resource.Loading -> setUiFetchState(UiFetchState.Loading)
                        is Resource.Error -> setUiFetchState(UiFetchState.Error(result.message))
                    }
                }
            }
    }

    private fun fetchUserCartItems() = viewModelScope.launch {
        getUserCartItemsUseCase(Unit).collect {
            when (it) {
                is Resource.Success -> _cartItems.value = it.data
                is Resource.Loading -> setUiFetchState(UiFetchState.Loading)
                is Resource.Error -> setUiFetchState(UiFetchState.Error(it.message))
            }
        }
    }

    private fun buildCartList() {
        // Merge the seller info and cart items sections first,
        // then add the cost section.
        cartItemsFlow.combine(sellerBasicFlow) { cartItems, sellerBasic ->
            if (cartItems.isNotEmpty()) {
                buildList {
                    add(CartSellerInfoModel(sellerBasic = sellerBasic))
                    addAll(cartItems.map { CartPurchaseItemModel(userCartItem = it) })
                }
            } else {
                emptyList()
            }
        }.combine(userCartFlow) { mergedList, userCart ->
            setUiFetchState(UiFetchState.Success)
            _cartListModels.value = if (mergedList.isNotEmpty()) {
                buildList {
                    addAll(mergedList)
                    add(CartTotalPriceModel(
                        subtotal = userCart.subtotalCost,
                        deliveryFee = userCart.deliveryCost,
                        total = userCart.totalCost
                    ))
                    add(CartActionsModel(
                        allowOrder = !userCart.syncRequired
                    ))
                }
            } else {
                emptyList()
            }
        }.launchIn(viewModelScope)
    }

    private fun clearCartWhenTimeout() = viewModelScope.launch {
        userCartFlow.collect { userCart ->
            when (val result = checkCartTimeOutUseCase(userCart)) {
                is Resource.Success -> {
                    val isTimeout = result.data
                    if (isTimeout) {
                        onClearCart()
                        _showCartTimeoutMessage.value = Unit
                    }
                }
                is Resource.Error -> showToastMessage("Error checking cart timeout.")
            }
        }
    }

    fun onRemoveCartItem(userCartItem: UserCartItem) = viewModelScope.launch {
        when (val result = removeUserCartItemUseCase(userCartItem.id)) {
            is Resource.Success -> Unit
            is Resource.Error -> showToastMessage(result.message)
        }
    }

    fun onClearCart() = viewModelScope.launch {
        when (val result = clearUserCartUseCase(Unit)) {
            is Resource.Success -> Unit
            is Resource.Error -> showToastMessage(result.message)
        }
    }
}