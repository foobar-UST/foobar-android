package com.foobarust.android.sellerdetail

import android.content.Context
import android.os.Parcelable
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.common.UiState
import com.foobarust.android.sellerdetail.SellerItemDetailListModel.*
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.domain.models.cart.AddUserCartItem
import com.foobarust.domain.models.cart.UpdateUserCartItem
import com.foobarust.domain.models.cart.UserCart
import com.foobarust.domain.models.seller.SellerItemBasic
import com.foobarust.domain.models.seller.SellerItemDetail
import com.foobarust.domain.models.seller.getNormalizedDescription
import com.foobarust.domain.models.seller.getNormalizedTitle
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.cart.*
import com.foobarust.domain.usecases.seller.GetMoreSellerItemsUseCase
import com.foobarust.domain.usecases.seller.GetMoreSellerItemsUseCaseParameters
import com.foobarust.domain.usecases.seller.GetSellerItemDetailParameters
import com.foobarust.domain.usecases.seller.GetSellerItemDetailUseCase
import com.foobarust.domain.utils.cancelIfActive
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

/**
 * Created by kevin on 10/13/20
 */

private const val MORE_ITEMS_SIZE = 5

class SellerItemDetailViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val getSellerItemDetailUseCase: GetSellerItemDetailUseCase,
    private val getMoreSellerItemsUseCase: GetMoreSellerItemsUseCase,
    private val addUserCartItemUseCase: AddUserCartItemUseCase,
    private val updateUserCartItemUseCase: UpdateUserCartItemUseCase
) : BaseViewModel() {

    private val _itemProperty = MutableStateFlow<SellerItemDetailProperty?>(null)

    private val _toolbarCollapsed = MutableStateFlow(false)

    private val _bundleItems = MutableStateFlow<List<SellerItemBasic>>(emptyList())

    private val _checkedBundleItems = MutableStateFlow<List<SellerItemBasic>>(emptyList())

    private val _itemDetail = MutableStateFlow<SellerItemDetail?>(null)
    val itemDetail: LiveData<SellerItemDetail?> = _itemDetail
        .asLiveData(viewModelScope.coroutineContext)

    private val _amountsInput = MutableStateFlow(1)
    val amountsInput: LiveData<Int> = _amountsInput
        .asLiveData(viewModelScope.coroutineContext)

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: LiveData<Boolean> = _isSubmitting
        .asLiveData(viewModelScope.coroutineContext)

    private val checkedBundleItemsPrice: Flow<Double> = _checkedBundleItems
        .map { checkedBundleItems -> checkedBundleItems.sumOf { it.price } }

    private val totalPrice: Flow<Double> = combine(
        _itemDetail.filterNotNull(),
        _amountsInput,
        checkedBundleItemsPrice
    ) { itemDetail, amountsInput, bundlePrice ->
        itemDetail.price * amountsInput + bundlePrice
    }

    private val _dismissDialog = SingleLiveEvent<Unit>()
    val dismissDialog: LiveData<Unit>
        get() = _dismissDialog

    private val _showDiffSellerDialog = SingleLiveEvent<Unit>()
    val showDiffSellerDialog: LiveData<Unit>
        get() = _showDiffSellerDialog

    val itemDetailListModels: LiveData<List<SellerItemDetailListModel>> = combine(
        _itemDetail.filterNotNull(),
        _bundleItems,
        checkedBundleItemsPrice
    ) { itemDetail, bundleItems, bundlePrice ->
        buildItemDetailListModels(itemDetail, bundleItems, bundlePrice)
    }
        .asLiveData(viewModelScope.coroutineContext)

    val toolbarTitle: LiveData<String?> = _toolbarCollapsed.combine(
            _itemProperty.filterNotNull()
        ) { collapsed, property ->
            when {
                property.isUpdateItemState() -> context.getString(R.string.seller_item_detail_toolbar_title_update)
                collapsed -> context.getString(R.string.seller_item_detail_toolbar_title_add)
                else -> null
            }
        }
        .asLiveData(viewModelScope.coroutineContext)

    val submitButtonTitle: LiveData<String?> = combine(
        totalPrice,
        _itemProperty.filterNotNull(),
        _isSubmitting
    ) { totalPrice, itemProperty, isSubmitting ->
        when {
            isSubmitting -> null
            itemProperty.isUpdateItemState() -> context.getString(
                R.string.seller_item_submit_update,
                totalPrice
            )
            else -> context.getString(R.string.seller_item_submit_add, totalPrice)
        }
    }
    .asLiveData(viewModelScope.coroutineContext)

    private var fetchItemDetailJob: Job? = null

    fun onFetchItemDetail(property: SellerItemDetailProperty) {
        _itemProperty.value = property
        fetchItemDetailJob?.cancelIfActive()
        fetchItemDetailJob = viewModelScope.launch {
            val params = GetSellerItemDetailParameters(
                sellerId = property.sellerId,
                itemId = property.itemId
            )
            getSellerItemDetailUseCase(params).collect {
                when (it) {
                    is Resource.Success -> {
                        setUiState(UiState.Success)
                        _itemDetail.value = it.data
                        fetchMoreItems(property)
                    }
                    is Resource.Error -> {
                        setUiState(UiState.Error(it.message))
                    }
                    is Resource.Loading -> {
                        setUiState(UiState.Loading)
                    }
                }
            }
        }
        // Setup initial amount for update action
        property.amounts?.let { _amountsInput.value = it }
    }

    fun onAmountIncrement() {
        // Increment the amounts input if there is adequate item left.
        _itemDetail.value?.let { property ->
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

    fun onSubmitItem(currentUserCart: UserCart?) = viewModelScope.launch {
        val property = _itemProperty.value ?: return@launch
        if (property.isUpdateItemState()) {
            updateUserCartItem(cartItemId = property.cartItemId!!)
        } else {
            // Require cart seller id for comparison with item's seller id
            addUserCartItem(cartSellerId = currentUserCart?.sellerId)
        }
    }

    fun onExtraItemCheckedChange(itemBasic: SellerItemBasic, isChecked: Boolean) {
        val currentExtraItems =  _checkedBundleItems.value.toMutableList()
        if (isChecked) {
            currentExtraItems.add(itemBasic)
        } else {
            currentExtraItems.remove(itemBasic)
        }
        _checkedBundleItems.value = currentExtraItems
    }

    fun onToolbarCollapsed(isCollapsed: Boolean) {
        _toolbarCollapsed.value = isCollapsed
    }

    private fun fetchMoreItems(property: SellerItemDetailProperty) = viewModelScope.launch {
        val parameters = GetMoreSellerItemsUseCaseParameters(
            sellerId = property.sellerId,
            currentItemId = property.itemId,
            limit = MORE_ITEMS_SIZE
        )
        getMoreSellerItemsUseCase(parameters).collect {
            when (it) {
                is Resource.Success -> _bundleItems.value = it.data
                is Resource.Error -> showToastMessage(it.message)
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
            when (it) {
                is Resource.Success -> {
                    _dismissDialog.value = Unit
                    _isSubmitting.value = false
                }
                is Resource.Error -> {
                    _isSubmitting.value = false
                    showToastMessage(it.message)
                }
                is Resource.Loading -> {
                    _isSubmitting.value = true
                }
            }
        }
    }

    private suspend fun addUserCartItem(cartSellerId: String?) {
        val property = _itemProperty.value ?: return
        val currentItemId = property.itemId
        val bundleItemsIds = _checkedBundleItems.value.map { it.id }

        val addUserCartItems: List<AddUserCartItem> = buildList {
            add(AddUserCartItem(
                itemId = currentItemId,
                amounts = _amountsInput.value,
                sectionId = property.sectionId
            ))
            // Add bundle item
            if (bundleItemsIds.isNotEmpty()) {
                addAll(bundleItemsIds.map {
                    AddUserCartItem(
                        itemId = it,
                        amounts = 1,
                        sectionId = property.sectionId
                    )
                })
            }
        }
        val params = AddUserCartItemParameters(
            addUserCartItems = addUserCartItems,
            itemSellerId = property.sellerId,
            cartSellerId = cartSellerId
        )

        addUserCartItemUseCase(params).collect {
            when (it) {
                is Resource.Success -> {
                    _dismissDialog.value = Unit
                    _isSubmitting.value = false
                }
                is Resource.Error -> {
                    _isSubmitting.value = false
                    if (it.message == ERROR_DIFFERENT_SELLER) {
                        _showDiffSellerDialog.value = Unit
                    } else {
                        showToastMessage(it.message)
                    }
                }
                is Resource.Loading -> {
                    _isSubmitting.value = true
                }
            }
        }
    }

    private fun buildItemDetailListModels(
        itemDetail: SellerItemDetail,
        bundleItems: List<SellerItemBasic>,
        checkedBundlePrice: Double
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
        // Add more items
        if (bundleItems.isNotEmpty()) {
            add(SellerItemDetailSubtitleItemModel(extraPrice = checkedBundlePrice))
            addAll(bundleItems.map {
                SellerItemDetailMoreItemModel(itemBasic = it)
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
    fun isOrderSectionState(): Boolean = sectionId != null
    fun isUpdateItemState(): Boolean = cartItemId != null
}