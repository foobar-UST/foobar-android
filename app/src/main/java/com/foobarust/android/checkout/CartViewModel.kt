package com.foobarust.android.checkout

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.checkout.CartListModel.*
import com.foobarust.domain.models.cart.*
import com.foobarust.domain.models.seller.SellerDetail
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.cart.*
import com.foobarust.domain.usecases.seller.GetSellerDetailUseCase
import com.foobarust.domain.utils.cancelIfActive
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * Created by kevin on 12/1/20
 */

@HiltViewModel
class CartViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getSellerDetailUseCase: GetSellerDetailUseCase,
    private val getUserCartUseCase: GetUserCartUseCase,
    private val getUserCartItemsUseCase: GetUserCartItemsUseCase,
    private val updateUserCartItemUseCase: UpdateUserCartItemUseCase,
    private val syncUserCartUseCase: SyncUserCartUseCase,
    private val clearUserCartUseCase: ClearUserCartUseCase,
    private val checkCartModifiableUseCase: CheckCartModifiableUseCase
) : ViewModel() {

    private val _cartListModels = MutableStateFlow<List<CartListModel>>(emptyList())
    val cartListModels: StateFlow<List<CartListModel>> = _cartListModels.asStateFlow()

    private val _cartItems = MutableStateFlow<List<UserCartItem>>(emptyList())
    val cartItems: StateFlow<List<UserCartItem>> = _cartItems.asStateFlow()

    private val _userCart = MutableStateFlow<UserCart?>(null)
    val userCart: StateFlow<UserCart?> = _userCart.asStateFlow()

    private val _cartUiState = MutableStateFlow<CartUiState>(CartUiState.Loading)
    val cartUiState: StateFlow<CartUiState> = _cartUiState.asStateFlow()

    private val _cartUpdateState = MutableStateFlow<CartUpdateState>(CartUpdateState.Idle)
    val cartUpdateState: StateFlow<CartUpdateState> = _cartUpdateState.asStateFlow()

    private val _sellerDetail = MutableStateFlow<SellerDetail?>(null)
    private val _orderNotes = MutableStateFlow<String?>(null)

    private val _finishSwipeRefresh = Channel<Unit>()
    val finishSwipeRefresh: Flow<Unit> = _finishSwipeRefresh.receiveAsFlow()

    private var fetchCartJob: Job? = null

    init {
        onFetchCart()
    }

    fun onFetchCart() {
        fetchCartJob?.cancelIfActive()
        fetchCartJob = viewModelScope.launch {
            // Get user cart
            launch {
                getUserCartUseCase(Unit).collect {
                    when (it) {
                        is Resource.Success -> {
                            _userCart.value = it.data
                        }
                        is Resource.Error -> {
                            _cartUiState.value = CartUiState.Error(it.message)
                        }
                        is Resource.Loading -> Unit
                    }
                }
            }

            // Get cart items
            launch {
                getUserCartItemsUseCase(Unit).collect {
                    when (it) {
                        is Resource.Success -> {
                            _cartItems.value = it.data
                            _cartUiState.value = CartUiState.Success
                            _finishSwipeRefresh.offer(Unit)
                        }
                        is Resource.Error -> {
                            _cartUiState.value = CartUiState.Error(it.message)
                            _finishSwipeRefresh.offer(Unit)
                        }
                        is Resource.Loading -> {
                            _cartUiState.value = CartUiState.Loading
                        }
                    }
                }
            }

            // Get seller detail
            launch {
                _userCart.filterNotNull()
                    .flatMapLatest { getSellerDetailUseCase(it.sellerId) }
                    .collect {
                        when (it) {
                            is Resource.Success -> _sellerDetail.value = it.data
                            is Resource.Error -> _cartUiState.value = CartUiState.Error(it.message)
                            is Resource.Loading -> Unit
                        }
                    }
            }

            // Build cart list
            launch {
                combine(
                    _userCart.filterNotNull(),
                    _cartItems,
                    _sellerDetail.filterNotNull(),
                    _orderNotes
                ) { userCart, cartItems, sellerDetail, orderNotes ->
                    buildCartListModels(userCart, cartItems, sellerDetail, orderNotes)
                }.collect {
                    _cartListModels.value = it
                }
            }

            // Check cart modifiable
            launch {
                combine(
                    _userCart.filterNotNull(),
                    _cartItems,
                    _sellerDetail.filterNotNull()
                ) { userCart, cartItems, sellerDetail ->
                    checkCartModifiableUseCase(userCart, cartItems, sellerDetail)
                }.collect { modifiable ->
                    _cartUpdateState.value = if (modifiable) {
                        CartUpdateState.Idle
                    } else {
                        CartUpdateState.Disabled
                    }
                }
            }
        }
    }

    fun onRemoveCartItem(userCartItem: UserCartItem) = viewModelScope.launch {
        if (_cartUpdateState.value != CartUpdateState.Idle) {
            return@launch
        }

        val updateUserCartItem = UpdateUserCartItem(
            cartItemId = userCartItem.id,
            amounts = userCartItem.amounts - 1
        )

        updateUserCartItemUseCase(updateUserCartItem).collect {
            _cartUpdateState.value = when (it) {
                is Resource.Success -> CartUpdateState.Idle
                is Resource.Error -> CartUpdateState.Error(it.message)
                is Resource.Loading -> CartUpdateState.Loading
            }
        }
    }

    fun onSyncUserCart() = viewModelScope.launch {
        if (_cartUpdateState.value == CartUpdateState.Loading) {
            return@launch
        }

        syncUserCartUseCase(Unit).collect {
            _cartUpdateState.value = when (it) {
                is Resource.Success -> CartUpdateState.Idle
                is Resource.Error -> CartUpdateState.Error(it.message)
                is Resource.Loading -> CartUpdateState.Loading
            }
        }
    }

    fun onClearUsersCart() = viewModelScope.launch {
        if (_cartUpdateState.value != CartUpdateState.Idle) {
            return@launch
        }

        clearUserCartUseCase(Unit).collect {
            _cartUpdateState.value = when (it) {
                is Resource.Success -> CartUpdateState.Idle
                is Resource.Error -> CartUpdateState.Error(it.message)
                is Resource.Loading -> CartUpdateState.Loading
            }
        }
    }

    fun onRestoreOrderNotes(notes: String) {
        _orderNotes.value = notes
    }

    private fun buildCartListModels(
        userCart: UserCart,
        cartItems: List<UserCartItem>,
        sellerDetail: SellerDetail,
        orderNotes: String?
    ): List<CartListModel> {
        if (cartItems.isEmpty()) {
            return listOf(CartEmptyItemModel)
        }

        return buildList {
            add(CartInfoItemModel(
                cartTitle = userCart.getNormalizedTitle(),
                cartImageUrl = userCart.imageUrl,
                cartPickupAddress = userCart.getNormalizedPickupAddress(),
                cartDeliveryTime = userCart.deliveryTime,
                sellerId = userCart.sellerId,
                sellerName = userCart.getNormalizedSellerName(),
                sellerOnline = sellerDetail.online,
                sectionId = userCart.sectionId,
                sellerType = userCart.sellerType,
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

sealed class CartUiState {
    object Success : CartUiState()
    data class Error(val message: String?) : CartUiState()
    object Loading : CartUiState()
}

sealed class CartUpdateState {
    object Idle : CartUpdateState()
    data class Error(val message: String?) : CartUpdateState()
    object Loading : CartUpdateState()
    object Disabled : CartUpdateState()
}