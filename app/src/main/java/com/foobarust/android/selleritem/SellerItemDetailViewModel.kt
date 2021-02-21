package com.foobarust.android.selleritem

import android.content.Context
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.selleritem.SellerItemDetailListModel.*
import com.foobarust.android.shared.BaseViewModel
import com.foobarust.android.utils.AppBarLayoutState
import com.foobarust.domain.models.cart.AddUserCartItem
import com.foobarust.domain.models.cart.UpdateUserCartItem
import com.foobarust.domain.models.seller.*
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.cart.*
import com.foobarust.domain.usecases.seller.*
import com.foobarust.domain.utils.cancelIfActive
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

/**
 * Created by kevin on 10/13/20
 */

private const val NUM_OF_SUGGESTED_ITEM = 5

@HiltViewModel
class SellerItemDetailViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getSellerItemDetailUseCase: GetSellerItemDetailUseCase,
    private val getSuggestedItemsUseCase: GetSuggestedItemsUseCase,
    private val addUserCartItemUseCase: AddUserCartItemUseCase,
    private val updateUserCartItemUseCase: UpdateUserCartItemUseCase
) : BaseViewModel() {

    private val _sellerItemDetail = MutableStateFlow<SellerItemDetail?>(null)
    val sellerItemDetail: LiveData<SellerItemDetail> = _sellerItemDetail
        .filterNotNull()
        .asLiveData(viewModelScope.coroutineContext)

    private val _suggestedItems = MutableStateFlow<List<SellerItemBasic>>(emptyList())

    private val _checkedSuggestedItems = MutableStateFlow<List<SellerItemBasic>>(emptyList())
    private val _checkedSuggestedItemsPrice: Flow<Double> = _checkedSuggestedItems
        .map { checkedBundleItems -> checkedBundleItems.sumOf { it.price } }

    private val _sellerItemDetailListModels = MutableStateFlow<List<SellerItemDetailListModel>>(emptyList())
    val sellerItemDetailListModels: LiveData<List<SellerItemDetailListModel>> = _sellerItemDetailListModels
        .asLiveData(viewModelScope.coroutineContext)

    private val _sellerItemDetailUiState = MutableStateFlow<SellerItemDetailUiState>(
        SellerItemDetailUiState.Loading
    )
    val sellerItemDetailUiState: LiveData<SellerItemDetailUiState> = _sellerItemDetailUiState
        .asLiveData(viewModelScope.coroutineContext)

    private val _sellerItemDetailUpdateState = MutableStateFlow<SellerItemDetailUiState?>(null)
    val sellerItemDetailUpdateState: LiveData<SellerItemDetailUiState?> = _sellerItemDetailUpdateState
        .asLiveData(viewModelScope.coroutineContext)

    private val _amountsInput = MutableStateFlow(1)
    val amountsInput: LiveData<String> = _amountsInput
        .map { it.toString() }
        .asLiveData(viewModelScope.coroutineContext)

    private val totalPrice: Flow<Double> = combine(
        _sellerItemDetail.filterNotNull(),
        _amountsInput,
        _checkedSuggestedItemsPrice
    ) { itemDetail, amountsInput, suggestedItemsPrice ->
        itemDetail.price * amountsInput + suggestedItemsPrice
    }

    private val _itemProperty = MutableStateFlow<SellerItemDetailProperty?>(null)

    private val _toolbarScrollState = MutableStateFlow(AppBarLayoutState.IDLE)

    val toolbarTitle: LiveData<String?> = combine(
        _toolbarScrollState.map { it == AppBarLayoutState.COLLAPSED },
        _itemProperty.filterNotNull().map { it.isUpdateItemState() }
    ) { isCollapsed, isUpdateItemState ->
        when {
            isCollapsed && isUpdateItemState -> context.getString(
                R.string.seller_item_detail_toolbar_title_update
            )
            isCollapsed && !isUpdateItemState -> context.getString(
                R.string.seller_item_detail_toolbar_title_add
            )
            else -> null
        }
    }
        .asLiveData(viewModelScope.coroutineContext)

    val submitButtonTitle: LiveData<String?> = combine(
        totalPrice,
        _itemProperty.filterNotNull()
    ) { totalPrice, property ->
        when {
            property.isUpdateItemState() -> context.getString(
                R.string.seller_item_submit_update,
                totalPrice
            )
            else -> context.getString(R.string.seller_item_submit_add, totalPrice)
        }
    }
        .asLiveData(viewModelScope.coroutineContext)

    val showItemImage: LiveData<Boolean> = _sellerItemDetail
        .map { it?.imageUrl != null }
        .asLiveData(viewModelScope.coroutineContext)

    val showModifyButtons: LiveData<Boolean> = _sellerItemDetailUiState.combine(
        _sellerItemDetailUpdateState
    ) { uiState, updateState ->
        uiState is SellerItemDetailUiState.Success &&
            updateState !is SellerItemDetailUiState.Loading
    }
        .asLiveData(viewModelScope.coroutineContext)

    private var fetchItemDetailJob: Job? = null

    init {
        // Build item detail list
        viewModelScope.launch {
            combine(
                _sellerItemDetail.filterNotNull(),
                _suggestedItems,
                _checkedSuggestedItemsPrice
            ) { itemDetail, suggestedItems, checkedSuggestedItemsPrice ->
                buildItemDetailListModels(
                    itemDetail,
                    suggestedItems,
                    checkedSuggestedItemsPrice
                )
            }.collect {
                _sellerItemDetailListModels.value = it
            }
        }
    }

    fun onFetchItemDetail(property: SellerItemDetailProperty) {
        property.let {
            _itemProperty.value = it
            _amountsInput.value = it.amounts ?: 1
        }

        fetchItemDetailJob?.cancelIfActive()
        fetchItemDetailJob = viewModelScope.launch {
            fetchSellerItemDetail(property.itemId)

            if (!property.isUpdateItemState()) {
                fetchSuggestedItems(
                    sellerId = property.sellerId,
                    currentItemId = property.itemId
                )
            }
        }
    }

    fun onAmountIncrement() {
        // Increment the amounts input if there is adequate item left.
        _sellerItemDetail.value?.let { property ->
            if (_amountsInput.value + 1 <= property.count) {
                _amountsInput.value++
            }
        }
    }

    fun onAmountDecrement() {
        // Decrement the amounts input until reaching zero.
        if (_amountsInput.value > 1) {
            _amountsInput.value--
        }
    }

    fun onSubmitItem(cartSellerId: String?) = viewModelScope.launch {
        val property = _itemProperty.value ?: return@launch

        property.cartItemId?.let {
            updateUserCartItem(cartItemId = it)
        } ?:
            // Require cart seller id for comparison with item's seller id
            addUserCartItem(cartSellerId = cartSellerId)
    }

    fun onSuggestedItemChecked(itemBasic: SellerItemBasic, isChecked: Boolean) {
        val currentExtraItems =  _checkedSuggestedItems.value.toMutableList()
        if (isChecked) {
            currentExtraItems.add(itemBasic)
        } else {
            currentExtraItems.remove(itemBasic)
        }
        _checkedSuggestedItems.value = currentExtraItems
    }

    fun onToolbarScrollStateChanged(scrollState: AppBarLayoutState) {
        _toolbarScrollState.value = scrollState
    }

    private fun fetchSellerItemDetail(itemId: String) = viewModelScope.launch {
        getSellerItemDetailUseCase(itemId).collect {
            when (it) {
                is Resource.Success -> {
                    _sellerItemDetail.value = it.data
                    _sellerItemDetailUiState.value = SellerItemDetailUiState.Success
                }
                is Resource.Error -> {
                    _sellerItemDetailUiState.value = SellerItemDetailUiState.Error(it.message)
                }
                is Resource.Loading -> {
                    _sellerItemDetailUiState.value = SellerItemDetailUiState.Loading
                }
            }
        }
    }

    private fun fetchSuggestedItems(sellerId: String, currentItemId: String) = viewModelScope.launch {
        val params = GetSuggestedItemsParameters(
            sellerId = sellerId,
            ignoreItemId = currentItemId,
            numOfItems = NUM_OF_SUGGESTED_ITEM
        )
        getSuggestedItemsUseCase(params).collect {
            when (it) {
                is Resource.Success -> {
                    _suggestedItems.value = it.data
                }
                is Resource.Error -> {

                }
                is Resource.Loading -> Unit
            }
        }
    }

    private suspend fun updateUserCartItem(cartItemId: String) {
        val updateUserCartItem = UpdateUserCartItem(
            cartItemId = cartItemId,
            amounts = _amountsInput.value
        )
        updateUserCartItemUseCase(updateUserCartItem).collect {
            _sellerItemDetailUpdateState.value = when (it) {
                is Resource.Success -> SellerItemDetailUiState.Success
                is Resource.Error -> SellerItemDetailUiState.Error(it.message)
                is Resource.Loading -> SellerItemDetailUiState.Loading
            }
        }
    }

    private suspend fun addUserCartItem(cartSellerId: String?) {
        val property = _itemProperty.value ?: return
        val currentItemId = property.itemId
        val suggestedItemsIds = _checkedSuggestedItems.value.map { it.id }

        val addUserCartItems: List<AddUserCartItem> = buildList {
            add(AddUserCartItem(
                itemId = currentItemId,
                amounts = _amountsInput.value,
                sectionId = property.sectionId
            ))

            // Add bundle item
            if (suggestedItemsIds.isNotEmpty()) {
                addAll(suggestedItemsIds.map {
                    AddUserCartItem(itemId = it, amounts = 1, sectionId = property.sectionId)
                })
            }
        }

        val params = AddUserCartItemParameters(
            addUserCartItems = addUserCartItems,
            itemSellerId = property.sellerId,
            cartSellerId = cartSellerId
        )

        addUserCartItemUseCase(params).collect {
            _sellerItemDetailUpdateState.value = when (it) {
                is Resource.Success -> SellerItemDetailUiState.Success
                is Resource.Error -> {
                    val isInvalidSeller = it.message == INVALID_SELLER_ERROR
                    val errorMessage = if (isInvalidSeller) {
                        context.getString(R.string.seller_item_detail_invalid_seller_message)
                    } else {
                        it.message
                    }

                    SellerItemDetailUiState.Error(errorMessage)
                }
                is Resource.Loading -> SellerItemDetailUiState.Loading
            }
        }
    }

    private fun buildItemDetailListModels(
        itemDetail: SellerItemDetail,
        suggestedItems: List<SellerItemBasic>,
        checkedSuggestedItemsPrice: Double
    ): List<SellerItemDetailListModel> = buildList {
        // Add item info
        add(SellerItemDetailInfoItemModel(
            itemTitle = itemDetail.getNormalizedTitle(),
            itemPrice = itemDetail.price,
            itemCount = itemDetail.count
        ))

        // Add description
        itemDetail.getNormalizedDescription()?.let {
            add(SellerItemDetailDescriptionItemModel(itemDescription = it))
        }

        // Add suggested items
        if (suggestedItems.isNotEmpty()) {
            add(SellerItemDetailSubtitleItemModel(extraPrice = checkedSuggestedItemsPrice))
            addAll(suggestedItems.map {
                SellerItemDetailSuggestItemModel(itemBasic = it)
            })
        }
    }
}

@Parcelize
data class SellerItemDetailProperty(
    val sellerId: String,
    val itemId: String,
    val sectionId: String? = null,
    // Fields used for update action
    val cartItemId: String? = null,
    val amounts: Int? = null
) : Parcelable {
    fun isUpdateItemState() = cartItemId != null
}

sealed class SellerItemDetailUiState {
    object Success : SellerItemDetailUiState()
    data class Error(val message: String?) : SellerItemDetailUiState()
    object Loading : SellerItemDetailUiState()
}