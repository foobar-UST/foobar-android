package com.foobarust.android.checkout

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.foobarust.android.R
import com.foobarust.android.checkout.CartListModel.*
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.states.UiState
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.domain.models.cart.UserCart
import com.foobarust.domain.models.cart.UserCartItem
import com.foobarust.domain.models.seller.SellerBasic
import com.foobarust.domain.models.seller.SellerType
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.cart.*
import com.foobarust.domain.usecases.checkout.GetDeliveryAddressUseCase
import com.foobarust.domain.usecases.seller.GetSellerBasicUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Created by kevin on 12/1/20
 */
class CartViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val getUserCartUseCase: GetUserCartUseCase,
    private val getUserCartItemsUseCase: GetUserCartItemsUseCase,
    private val getSellerBasicUseCase: GetSellerBasicUseCase,
    private val getDeliveryAddressUseCase: GetDeliveryAddressUseCase,
    private val updateUserCartItemUseCase: UpdateUserCartItemUseCase,
    private val syncUserCartUseCase: SyncUserCartUseCase,
    private val clearUserCartUseCase: ClearUserCartUseCase
) : BaseViewModel() {

    private val _cartItems = MutableStateFlow<List<UserCartItem>>(emptyList())
    private val _userCart = MutableStateFlow<UserCart?>(null)
    private val _sellerBasic = MutableStateFlow<SellerBasic?>(null)
    private val _deliveryAddress = MutableStateFlow<String?>(null)

    private val _cartListModels = MutableLiveData<List<CartListModel>>()
    val cartListModels: LiveData<List<CartListModel>>
        get() = _cartListModels

    // No item layout show when network error or there is no item in cart
    val showNoItemLayout: LiveData<Boolean> = _cartItems
        .asStateFlow()
        .combine(uiState.asFlow()) { cartItems, uiState ->
            cartItems.isEmpty() && uiState is UiState.Success || uiState is UiState.Error
        }
        .asLiveData(viewModelScope.coroutineContext)

    // Number of cart items show in app bar
    val cartItemsCount: LiveData<Int> = _cartItems
        .asStateFlow()
        .map { it.size }
        .asLiveData(viewModelScope.coroutineContext)

    // Show cart sync snack bar
    val isCartSyncRequired: LiveData<Boolean> = _userCart
        .asStateFlow()
        .filterNotNull()
        .onEach {
            // Block the user from modifying cart before synchronization
            blockUserAction = it.syncRequired
        }
        .map { it.syncRequired }
        .distinctUntilChanged()
        .asLiveData(viewModelScope.coroutineContext)

    private val _showTimeoutMessage = SingleLiveEvent<Unit>()
    val showTimeoutMessage: LiveData<Unit>
        get() = _showTimeoutMessage

    private val _showSnackBarMessage = SingleLiveEvent<String>()
    val showSnackBarMessage: LiveData<String>
        get() = _showSnackBarMessage

    // Block user action when the current transaction is not finished
    private var blockUserAction: Boolean = false

    init {
        // Fetch data from multiple data sources
        fetchCartItems()
        fetchUserCartDetails()
        fetchSellerDetails()
        fetchDeliveryAddress()

        // Build list from multiple data sources
        viewModelScope.launch {
            combine(_cartItems.asStateFlow(),
                _sellerBasic.asStateFlow().filterNotNull(),
                _userCart.asStateFlow().filterNotNull(),
                _deliveryAddress.asStateFlow().filterNotNull()
            ) { cartItems, sellerBasic, userCart, deliveryAddress ->
                buildCartListModels(cartItems, sellerBasic, userCart, deliveryAddress)
            }.collect {
                _cartListModels.value = it
            }
        }
    }

    private fun fetchCartItems() = viewModelScope.launch {
        getUserCartItemsUseCase(Unit).collect {
            when (it) {
                is Resource.Success -> {
                    setUiState(UiState.Success)
                    _cartItems.value = it.data
                }
                is Resource.Error -> {
                    setUiState(UiState.Error(it.message))
                    _cartItems.value = emptyList()
                }
                is Resource.Loading -> {
                    setUiState(UiState.Loading)
                    _cartItems.value = emptyList()
                }
            }
        }
    }

    private fun fetchUserCartDetails() = viewModelScope.launch {
        getUserCartUseCase(Unit).collect {
            when (it) {
                is Resource.Success -> {
                    _userCart.value = it.data
                }
                is Resource.Error -> {
                    _userCart.value = null
                    showToastMessage(it.message)
                }
                is Resource.Loading -> {
                    _userCart.value = null
                }
            }
        }
    }

    private fun fetchSellerDetails() = viewModelScope.launch {
        _userCart.asStateFlow()
            .filterNotNull()
            .mapNotNull { it.sellerId }
            .flatMapLatest { getSellerBasicUseCase(it) }
            .collect {
                when (it) {
                    is Resource.Success -> {
                        _sellerBasic.value = it.data
                    }
                    is Resource.Error -> {
                        _sellerBasic.value = null
                        showToastMessage(it.message)
                    }
                    is Resource.Loading -> {
                        _sellerBasic.value = null
                    }
                }
            }
    }

    private fun fetchDeliveryAddress() = viewModelScope.launch {
        _userCart.asStateFlow()
            .filterNotNull()
            .filter { it.sellerType != null }
            .flatMapLatest { getDeliveryAddressUseCase(it) }
            .collect {
                when (it) {
                    is Resource.Success -> {
                        _deliveryAddress.value = it.data
                    }
                    is Resource.Error -> {
                        _deliveryAddress.value = null
                        showToastMessage(it.message)
                    }
                    is Resource.Loading -> {
                        _deliveryAddress.value = null
                    }
                }
            }
    }

    fun onRemoveCartItem(userCartItem: UserCartItem) = viewModelScope.launch {
        if (!blockUserAction) {
            blockUserAction = true
            val params = UpdateUserCartItemParameters(
                cartItemId = userCartItem.id,
                amounts = userCartItem.amounts - 1
            )
            updateUserCartItemUseCase(params).collect {
                when (it) {
                    is Resource.Success -> {
                        blockUserAction = false
                        setUiState(UiState.Success)
                    }
                    is Resource.Error -> {
                        blockUserAction = false
                        setUiState(UiState.Error(it.message))
                    }
                    is Resource.Loading -> setUiState(UiState.Loading)
                }
            }
        }
    }

    fun onSyncUserCart() = viewModelScope.launch {
        syncUserCartUseCase(Unit).collect {
            when (it) {
                is Resource.Success -> {
                    setUiState(UiState.Success)
                    _showSnackBarMessage.value = context.getString(
                        R.string.cart_sync_required_complete_message
                    )
                }
                is Resource.Error -> setUiState(UiState.Error(it.message))
                is Resource.Loading -> setUiState(UiState.Loading)
            }
        }
    }

    fun onClearUsersCart() = viewModelScope.launch {
        clearUserCartUseCase(Unit).collect {
            when (it) {
                is Resource.Success -> {
                    setUiState(UiState.Success)
                    _showSnackBarMessage.value = context.getString(R.string.cart_cleared_message)
                }
                is Resource.Error -> setUiState(UiState.Error(it.message))
                is Resource.Loading -> setUiState(UiState.Loading)
            }
        }
    }

    private fun buildCartListModels(
        cartItems: List<UserCartItem>,
        sellerBasic: SellerBasic,
        userCart: UserCart,
        deliveryAddress: String
    ): List<CartListModel> {
        if (cartItems.isEmpty()) return emptyList()

        return buildList {
            // Add seller info section
            add(CartSellerInfoItemModel(sellerBasic = sellerBasic))

            // Add cart items section
            addAll(cartItems.map {
                CartPurchaseItemModel(userCartItem = it)
            })

            // Add delivery option section
            add(CartDeliveryInfoItemModel(
                title = if (userCart.sellerType == SellerType.ON_CAMPUS) {
                    context.getString(R.string.cart_delivery_info_title_pickup)
                } else {
                    context.getString(R.string.cart_delivery_info_title_delivery)
                },
                address = deliveryAddress,
                drawable = if (userCart.sellerType == SellerType.ON_CAMPUS) {
                    R.drawable.ic_directions_run
                } else {
                    R.drawable.ic_local_shipping
                }
            ))

            // Add notes section
            add(CartNotesItemModel)

            // Add total price section
            add(CartTotalPriceItemModel(
                subtotal = userCart.subtotalCost,
                deliveryFee = userCart.deliveryCost,
                total = userCart.totalCost
            ))

            // Add action buttons section
            // User can only place order when the cart is up-to-date and
            // all cart items are available
            val canPlaceOrder = !userCart.syncRequired && !cartItems.any { !it.available }
            add(CartActionsItemModel(allowOrder = canPlaceOrder))
        }
    }
}