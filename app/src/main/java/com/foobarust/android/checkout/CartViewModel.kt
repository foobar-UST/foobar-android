package com.foobarust.android.checkout

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.checkout.CartListModel.*
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.common.UiState
import com.foobarust.domain.models.cart.*
import com.foobarust.domain.models.seller.SellerBasic
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.cart.*
import com.foobarust.domain.usecases.seller.GetSellerBasicUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by kevin on 12/1/20
 */

@HiltViewModel
class CartViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getUserCartUseCase: GetUserCartUseCase,
    private val getUserCartItemsUseCase: GetUserCartItemsUseCase,
    private val getSellerBasicUseCase: GetSellerBasicUseCase,
    private val updateUserCartItemUseCase: UpdateUserCartItemUseCase,
    private val syncUserCartUseCase: SyncUserCartUseCase,
    private val clearUserCartUseCase: ClearUserCartUseCase
) : BaseViewModel() {

    private val _userCart = MutableStateFlow<UserCart?>(null)
    private val _cartItems = MutableStateFlow<List<UserCartItem>>(emptyList())
    private val _sellerBasic = MutableStateFlow<SellerBasic?>(null)
    private val _orderNotes = MutableStateFlow<String?>(null)

    // Block user action when the current transaction is not finished
    private var blockUserAction: Boolean = false

    // Allow submit order when
    // 1. Cart is already synchronized and up-to-date
    // 2. All items in cart are available
    // 3. Seller is currently online
    val allowSubmitOrder: LiveData<Boolean> = combine(
        _userCart.filterNotNull(),
        _cartItems,
        _sellerBasic.filterNotNull()
    ) { userCart, cartItems, sellerBasic ->
        val result = !userCart.syncRequired &&
            !cartItems.any { !it.available } &&
            cartItems.isNotEmpty()
            sellerBasic.online
        blockUserAction = !result
        result
    }
        .asLiveData(viewModelScope.coroutineContext)

    val cartListModels: LiveData<List<CartListModel>> = combine(
        _userCart.filterNotNull(),
        _cartItems,
        _sellerBasic.filterNotNull(),
        _orderNotes
    ) { userCart, cartItems, sellerBasic, orderNotes ->
        buildCartListModels(userCart, cartItems, sellerBasic, orderNotes)
    }
        .asLiveData(viewModelScope.coroutineContext)

    // Number of cart items show in app bar
    val cartItemsCount: LiveData<Int> = _cartItems
        .map { it.size }
        .asLiveData(viewModelScope.coroutineContext)

    val showSyncRequiredSnackBar: LiveData<Boolean> = _userCart
        .filterNotNull()
        .distinctUntilChanged()
        .map { it.syncRequired }
        .asLiveData(viewModelScope.coroutineContext)

    val cartToolbarTitle: LiveData<String> = _userCart
        .map { userCart ->
            userCart?.getNormalizedTitle() ?:
            context.getString(R.string.checkout_toolbar_title_cart)
        }
        .asLiveData(viewModelScope.coroutineContext)

    init {
        // Parent coroutine for fetching data from multiple data sources
        viewModelScope.launch {
            fetchUserCart()
            fetchCartItems()
            fetchSellerBasic()
        }
    }

    fun onRemoveCartItem(userCartItem: UserCartItem) = viewModelScope.launch {
        if (!blockUserAction) {
            blockUserAction = true
            val updateUserCartItem = UpdateUserCartItem(
                cartItemId = userCartItem.id,
                amounts = userCartItem.amounts - 1
            )
            updateUserCartItemUseCase(updateUserCartItem).collect {
                when (it) {
                    is Resource.Success -> {
                        blockUserAction = false
                        setUiState(UiState.Success)
                    }
                    is Resource.Error -> {
                        blockUserAction = false
                        setUiState(UiState.Error(it.message))
                    }
                    is Resource.Loading -> {
                        setUiState(UiState.Loading)
                    }
                }
            }
        }
    }

    fun onSyncUserCart() = viewModelScope.launch {
        syncUserCartUseCase(Unit).collect {
            when (it) {
                is Resource.Success -> setUiState(UiState.Success)
                is Resource.Error -> setUiState(UiState.Error(it.message))
                is Resource.Loading -> setUiState(UiState.Loading)
            }
        }
    }

    fun onClearUsersCart() = viewModelScope.launch {
        clearUserCartUseCase(Unit).collect {
            when (it) {
                is Resource.Success -> setUiState(UiState.Success)
                is Resource.Error -> setUiState(UiState.Error(it.message))
                is Resource.Loading -> setUiState(UiState.Loading)
            }
        }
    }

    fun onRestoreOrderNotes(notes: String) {
        _orderNotes.value = notes
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

    private fun fetchUserCart() = viewModelScope.launch {
        getUserCartUseCase(Unit).collect {
            when (it) {
                is Resource.Success -> {
                    _userCart.value = it.data
                }
                is Resource.Error -> {
                    _userCart.value = null
                }
                is Resource.Loading -> {
                    _userCart.value = null
                }
            }
        }
    }

    private fun fetchSellerBasic() = viewModelScope.launch {
        _userCart.filterNotNull()
            .flatMapLatest { getSellerBasicUseCase(it.sellerId) }
            .collect {
                when (it) {
                    is Resource.Success -> {
                        _sellerBasic.value = it.data
                    }
                    is Resource.Error -> {
                        _sellerBasic.value = null
                    }
                    is Resource.Loading -> {
                        _sellerBasic.value = null
                    }
                }
            }
    }

    private fun buildCartListModels(
        userCart: UserCart,
        cartItems: List<UserCartItem>,
        sellerBasic: SellerBasic,
        orderNotes: String?
    ): List<CartListModel> {
        // Return if there is no item in cart
        if (cartItems.isEmpty()) {
            return listOf(CartEmptyItemModel)
        }

        return buildList {
            add(CartInfoItemModel(
                cartTitle = userCart.getNormalizedTitle(),
                cartImageUrl = userCart.imageUrl,
                cartPickupAddress = userCart.getNormalizedPickupAddress(),
                cartDeliveryTime = context.getString(
                    R.string.cart_info_option_format_section,
                    userCart.getDeliveryDateString(),
                    userCart.getDeliveryTimeString()
                ),
                sellerId = userCart.sellerId,
                sellerOnline = sellerBasic.online,
                sectionId = userCart.sectionId
            ))

            // Add cart items section
            add(CartPurchaseSubtitleItemModel(
                subtitle = context.getString(R.string.cart_purchase_subtitle)
            ))
            addAll(cartItems.map {
                CartPurchaseItemModel(userCartItem = it)
            })
            add(CartPurchaseActionsItemModel(
                sellerId = userCart.sellerId,
                sectionId = userCart.sectionId
            ))

            // Add notes section
            add(CartPurchaseSubtitleItemModel(
                subtitle = context.getString(R.string.cart_notes_subtitle)
            ))
            add(CartOrderNotesItemModel(
                orderNotes = orderNotes
            ))

            // Add total price section
            add(CartTotalPriceItemModel(
                subtotal = userCart.subtotalCost,
                deliveryFee = userCart.deliveryCost,
                total = userCart.totalCost
            ))
        }
    }
}