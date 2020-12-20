package com.foobarust.android.cart

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
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
import com.foobarust.domain.usecases.cart.GetUserCartUseCase
import com.foobarust.domain.usecases.cart.RemoveUserCartItemUseCase
import com.foobarust.domain.usecases.cart.SyncUserCartUseCase
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
    private val getUserCartUseCase: GetUserCartUseCase,
    private val getUserCartItemsUseCase: GetUserCartItemsUseCase,
    private val removeUserCartItemUseCase: RemoveUserCartItemUseCase,
    private val syncUserCartUseCase: SyncUserCartUseCase
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

    private val _showTimeoutMessage = SingleLiveEvent<Unit>()
    val showTimeoutMessage: LiveData<Unit>
        get() = _showTimeoutMessage

    private val _showSnackBarMessage = SingleLiveEvent<String>()
    val showSnackBarMessage: LiveData<String>
        get() = _showSnackBarMessage

    private val _isUpdatingProgress = MutableStateFlow(false)
    val isUpdatingProgress: LiveData<Boolean>
        get() = _isUpdatingProgress.asStateFlow().asLiveData(viewModelScope.coroutineContext)

    private var blockAction: Boolean = false

    val showSyncRequiredAction: LiveData<Boolean> = _userCart.asStateFlow()
        .filterNotNull()
        .map {
            blockAction = it.syncRequired
            it.syncRequired
        }
        .distinctUntilChanged()
        .asLiveData(viewModelScope.coroutineContext)

    fun onFetchCartItems() {
        fetchUserCart()
        fetchSellerBasic()
        fetchUserCartItems()
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
                is Resource.Error -> {
                    _cartItems.value = emptyList()
                    setUiFetchState(UiFetchState.Error(it.message))
                }
            }
        }
    }

    private fun buildCartList() {
        // Merge the CartSellerInfo and CartPurchaseItems first, then add CartTotalPrice
        cartItemsFlow.combine(sellerBasicFlow) { cartItems, sellerBasic ->
            if (cartItems.isNotEmpty()) {
                Pair(cartItems, sellerBasic)
            } else {
                null
            }
        }.combine(userCartFlow) { pairResult, userCart ->
            setUiFetchState(UiFetchState.Success)

            _cartListModels.value = if (pairResult != null) {
                buildList {
                    val (cartItems, sellerBasic) = pairResult
                    val allowOrder = !userCart.syncRequired && !cartItems.any { !it.available }

                    add(CartSellerInfoModel(sellerBasic = sellerBasic))
                    addAll(cartItems.map {
                        CartPurchaseItemModel(userCartItem = it)
                    })
                    add(CartTotalPriceModel(
                        subtotal = userCart.subtotalCost,
                        deliveryFee = userCart.deliveryCost,
                        total = userCart.totalCost
                    ))
                    add(CartActionsModel(allowOrder = allowOrder))
                }
            } else {
                emptyList()
            }
        }.launchIn(viewModelScope)
    }

    fun onRemoveCartItem(userCartItem: UserCartItem) = viewModelScope.launch {
        if (!blockAction) {
            blockAction = true
            removeUserCartItemUseCase(userCartItem.id).collect {
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
                    is Resource.Loading -> _isUpdatingProgress.value = true
                }
            }
        }
    }

    fun onSyncUserCart() = viewModelScope.launch {
        syncUserCartUseCase(Unit).collect {
            when (it) {
                is Resource.Success -> {
                    _showSnackBarMessage.value = context.getString(
                        R.string.cart_sync_required_complete_message
                    )
                }
                is Resource.Error -> showToastMessage(it.message)
                is Resource.Loading -> Unit
            }
        }
    }
}