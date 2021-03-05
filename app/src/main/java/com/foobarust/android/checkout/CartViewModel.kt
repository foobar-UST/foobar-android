package com.foobarust.android.checkout

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.checkout.CartListModel.*
import com.foobarust.domain.models.cart.*
import com.foobarust.domain.models.seller.SellerDetail
import com.foobarust.domain.models.seller.SellerType
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
    val cartListModels: LiveData<List<CartListModel>> = _cartListModels
        .asLiveData(viewModelScope.coroutineContext)

    private val _cartUiState = MutableStateFlow<CartUiState>(CartUiState.Loading)
    val cartUiState: LiveData<CartUiState> = _cartUiState
        .asLiveData(viewModelScope.coroutineContext)

    private val _cartUpdateState = MutableStateFlow<CartUpdateState>(CartUpdateState.Idle)
    val cartUpdateState: LiveData<CartUpdateState> = _cartUpdateState
        .asLiveData(viewModelScope.coroutineContext)

    private val _userCart = MutableStateFlow<UserCart?>(null)
    private val _cartItems = MutableStateFlow<List<UserCartItem>>(emptyList())
    private val _sellerDetail = MutableStateFlow<SellerDetail?>(null)
    private val _orderNotes = MutableStateFlow<String?>(null)

    private val _finishSwipeRefresh = Channel<Unit>()
    val finishSwipeRefresh: Flow<Unit> = _finishSwipeRefresh.receiveAsFlow()

    private var fetchCartJob: Job? = null

    // Number of cart items show in app bar
    val cartItemsCount: LiveData<Int> = _cartItems
        .map { it.size }
        .asLiveData(viewModelScope.coroutineContext)

    val showSyncRequired: LiveData<Boolean> = _userCart
        .filterNotNull()
        .distinctUntilChanged()
        .map { it.syncRequired }
        .asLiveData(viewModelScope.coroutineContext)

    val toolbarTitle: LiveData<String> = _userCart
        .map { userCart ->
            userCart?.getNormalizedTitle() ?:
            context.getString(R.string.checkout_toolbar_title_cart)
        }
        .asLiveData(viewModelScope.coroutineContext)

    init {
        onFetchCart()
    }

    fun onFetchCart(isSwipeRefresh: Boolean = false) {
        fetchCartJob?.cancelIfActive()
        fetchCartJob = viewModelScope.launch {
            // Get user cart
            viewModelScope.launch {
                getUserCartUseCase(Unit).collect {
                    when (it) {
                        is Resource.Success -> {
                            _userCart.value = it.data
                        }
                        is Resource.Error -> {
                            _userCart.value = null
                            _cartUiState.value = CartUiState.Error(it.message)
                        }
                        is Resource.Loading -> Unit
                    }
                }
            }

            // Get cart items
            viewModelScope.launch {
                getUserCartItemsUseCase(Unit).collect {
                    when (it) {
                        is Resource.Success -> {
                            _cartItems.value = it.data
                            _cartUiState.value = CartUiState.Success

                            if (isSwipeRefresh) {
                                _finishSwipeRefresh.offer(Unit)
                            }
                        }
                        is Resource.Error -> {
                            _cartUiState.value = CartUiState.Error(it.message)

                            if (isSwipeRefresh) {
                                _finishSwipeRefresh.offer(Unit)
                            }
                        }
                        is Resource.Loading -> {
                            _cartUiState.value = CartUiState.Loading
                        }
                    }
                }
            }

            // Get seller detail
            viewModelScope.launch {
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
            viewModelScope.launch {
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
            viewModelScope.launch {
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
        // Return if there is no item in cart
        if (cartItems.isEmpty()) {
            return listOf(
                CartEmptyItemModel(
                    drawableRes = R.drawable.undraw_empty_cart,
                    emptyMessage = context.getString(R.string.cart_empty_message)
                )
            )
        }

        return buildList {
            add(CartInfoItemModel(
                cartTitle = userCart.getNormalizedTitle(),
                cartImageUrl = userCart.imageUrl,
                cartPickupAddress = userCart.getNormalizedPickupAddress(),
                cartDeliveryTime = context.getString(
                    R.string.cart_info_nav_format_section,
                    userCart.getDeliveryDateString(),
                    userCart.getDeliveryTimeString()
                ),
                sellerId = userCart.sellerId,
                sellerOnline = sellerDetail.online,
                sectionId = userCart.sectionId,
                sectionNavSubtitle = context.getString(
                    R.string.cart_info_nav_subtitle_section,
                    userCart.getNormalizedSellerName()
                ),
                miscNavSubtitle = if (userCart.sellerType == SellerType.ON_CAMPUS) {
                    context.getString(R.string.cart_info_nav_subtitle_misc_on_campus)
                } else {
                    context.getString(R.string.cart_info_nav_subtitle_misc_off_campus)
                }
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