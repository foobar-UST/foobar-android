package com.foobarust.android.cart

import android.content.Context
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.foobarust.android.R
import com.foobarust.android.cart.CartListModel.*
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.states.UiFetchState
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.domain.models.cart.UserCart
import com.foobarust.domain.models.cart.UserCartItem
import com.foobarust.domain.models.seller.SellerBasic
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.cart.GetUserCartItemsUseCase
import com.foobarust.domain.usecases.cart.SyncUserCartUseCase
import com.foobarust.domain.usecases.cart.UpdateUserCartItemParameters
import com.foobarust.domain.usecases.cart.UpdateUserCartItemUseCase
import com.foobarust.domain.usecases.seller.GetSellerBasicUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Created by kevin on 12/1/20
 */
class CartViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val getSellerBasicUseCase: GetSellerBasicUseCase,
    private val getUserCartItemsUseCase: GetUserCartItemsUseCase,
    private val updateUserCartItemUseCase: UpdateUserCartItemUseCase,
    private val syncUserCartUseCase: SyncUserCartUseCase
) : BaseViewModel() {

    private val _userCart = MutableStateFlow<UserCart?>(null)
    private val _sellerBasic = MutableStateFlow<SellerBasic?>(null)
    private val _cartItems = MutableStateFlow<List<UserCartItem>>(emptyList())

    private val _cartListModels = MutableLiveData<List<CartListModel>>(emptyList())
    val cartListModels: LiveData<List<CartListModel>>
        get() = _cartListModels

    private val _showTimeoutMessage = SingleLiveEvent<Unit>()
    val showTimeoutMessage: LiveData<Unit>
        get() = _showTimeoutMessage

    private val _showSnackBarMessage = SingleLiveEvent<String>()
    val showSnackBarMessage: LiveData<String>
        get() = _showSnackBarMessage

    private val _isUpdatingProgress = MutableLiveData(false)
    val isUpdatingProgress: LiveData<Boolean>
        get() = _isUpdatingProgress

    val cartItemsCount: LiveData<Int> = _cartItems
        .asStateFlow()
        .map { it.size }
        .asLiveData(viewModelScope.coroutineContext)

    val showNoItemLayout: LiveData<Boolean> = _cartItems
        .asStateFlow()
        .combine(uiFetchState.asFlow()) { cartItems, uiFetchState ->
            cartItems.isEmpty() && uiFetchState is UiFetchState.Success ||
                uiFetchState is UiFetchState.Error
        }
        .asLiveData(viewModelScope.coroutineContext)

    val showSyncRequiredAction: LiveData<Boolean> = _userCart
        .asStateFlow()
        .filterNotNull()
        .map { it.syncRequired }
        .distinctUntilChanged()
        .asLiveData(viewModelScope.coroutineContext)

    // Block user action when the current transaction is not finished
    private var blockAction: Boolean = false

    fun onFetchCartItems(userCart: UserCart?) {
        setUiFetchState(UiFetchState.Loading)
        _userCart.value = userCart
        fetchUserCartItems()
        fetchSellerBasic()
        buildCartList()
    }

    fun onRemoveCartItem(userCartItem: UserCartItem) = viewModelScope.launch {
        if (!blockAction) {
            blockAction = true
            val params = UpdateUserCartItemParameters(
                cartItemId = userCartItem.id,
                amounts = userCartItem.amounts - 1
            )
            updateUserCartItemUseCase(params).collect {
                when (it) {
                    is Resource.Success -> {
                        blockAction = false
                        _isUpdatingProgress.value = false
                    }
                    is Resource.Error -> {
                        blockAction = false
                        _isUpdatingProgress.value = false
                        showToastMessage(it.message)
                    }
                    is Resource.Loading -> {
                        _isUpdatingProgress.value = true
                    }
                }
            }
        }
    }

    fun onSyncUserCart() = viewModelScope.launch {
        Log.d("CartViewModel", "blockAction: $blockAction")

        if (!blockAction) {
            blockAction = true
            syncUserCartUseCase(Unit).collect {
                when (it) {
                    is Resource.Success -> {
                        blockAction = false
                        _isUpdatingProgress.value = false
                        _showSnackBarMessage.value = context.getString(
                            R.string.cart_sync_required_complete_message
                        )
                    }
                    is Resource.Error -> {
                        blockAction = false
                        _isUpdatingProgress.value = false
                        showToastMessage(it.message)
                    }
                    is Resource.Loading -> {
                        _isUpdatingProgress.value = true
                    }
                }
            }
        }
    }

    private fun fetchSellerBasic() = viewModelScope.launch {
        _userCart.asStateFlow()
            .filterNotNull()
            .collect { userCart ->
                userCart.sellerId?.let {
                    when (val result = getSellerBasicUseCase(it)) {
                        is Resource.Success -> {
                            _sellerBasic.value = result.data
                        }
                        is Resource.Loading -> Unit
                        is Resource.Error -> {
                            _sellerBasic.value = null
                            setUiFetchState(UiFetchState.Error(result.message))
                        }
                    }
                }
            }
    }

    private fun fetchUserCartItems() = viewModelScope.launch {
        getUserCartItemsUseCase(Unit).collect {
            when (it) {
                is Resource.Success -> {
                    _cartItems.value = it.data
                }
                is Resource.Loading -> Unit
                is Resource.Error -> {
                    _cartItems.value = emptyList()
                    setUiFetchState(UiFetchState.Error(it.message))
                }
            }
        }
    }

    private fun buildCartList() = viewModelScope.launch {
        combine(
            _cartItems.asStateFlow(),
            _sellerBasic.asStateFlow().filterNotNull(),
            _userCart.asStateFlow().filterNotNull()
        ) { cartItems, sellerBasic, userCart ->
            setUiFetchState(UiFetchState.Success)

            if (cartItems.isEmpty()) return@combine emptyList()

            buildList {
                // Add seller info
                add(CartSellerInfoModel(sellerBasic = sellerBasic))

                // Add cart items
                addAll(cartItems.map {
                    CartPurchaseItemModel(userCartItem = it)
                })

                // Add price section
                add(CartTotalPriceModel(
                    subtotal = userCart.subtotalCost,
                    deliveryFee = userCart.deliveryCost,
                    total = userCart.totalCost
                ))

                // Add action buttons
                // Show the order button only when the cart is up-to-date and
                // all cart items are available
                val allowOrder = !userCart.syncRequired && !cartItems.any { !it.available }
                add(CartActionsModel(allowOrder = allowOrder))
            }
        }.collectLatest { _cartListModels.value = it }
    }
}