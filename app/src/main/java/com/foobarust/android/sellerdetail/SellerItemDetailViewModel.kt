package com.foobarust.android.sellerdetail

import android.os.Parcelable
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.states.UiState
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.domain.models.cart.UserCart
import com.foobarust.domain.models.seller.SellerItemDetail
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.cart.*
import com.foobarust.domain.usecases.seller.GetSellerItemDetailParameters
import com.foobarust.domain.usecases.seller.GetSellerItemDetailUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

/**
 * Created by kevin on 10/13/20
 */

class SellerItemDetailViewModel @ViewModelInject constructor(
    private val getSellerItemDetailUseCase: GetSellerItemDetailUseCase,
    private val addUserCartItemUseCase: AddUserCartItemUseCase,
    private val updateUserCartItemUseCase: UpdateUserCartItemUseCase
) : BaseViewModel() {

    lateinit var property: SellerItemDetailProperty

    private val _itemDetail = MutableStateFlow<SellerItemDetail?>(null)
    val itemDetail: LiveData<SellerItemDetail?>
        get() = _itemDetail.asStateFlow().asLiveData(viewModelScope.coroutineContext)

    private val _amountsInput = MutableStateFlow(1)
    val amountsInput: LiveData<Int>
        get() = _amountsInput.asStateFlow().asLiveData(viewModelScope.coroutineContext)

    private val _cartItemSubmitting = MutableStateFlow(false)
    val cartItemSubmitting: LiveData<Boolean>
        get() = _cartItemSubmitting.asStateFlow().asLiveData(viewModelScope.coroutineContext)

    val finalPrice: LiveData<Double> = _itemDetail.asStateFlow()
        .combine(_amountsInput.asStateFlow()) { itemDetail, amount ->
            itemDetail?.let { it.price * amount } ?: 0.0
        }
        .asLiveData(viewModelScope.coroutineContext)

    private val _dismissDialog = SingleLiveEvent<Unit>()
    val dismissDialog: LiveData<Unit>
        get() = _dismissDialog

    private val _showDiffSellerDialog = SingleLiveEvent<Unit>()
    val showDiffSellerDialog: LiveData<Unit>
        get() = _showDiffSellerDialog

    fun onFetchItemDetail(property: SellerItemDetailProperty) = viewModelScope.launch {
        this@SellerItemDetailViewModel.property = property
        // Setup initial amount for update action
        property.amounts?.let {
            _amountsInput.value = it
        }

        val parameters = GetSellerItemDetailParameters(
            sellerId = property.sellerId,
            itemId = property.itemId
        )

        when (val result = getSellerItemDetailUseCase(parameters)) {
            is Resource.Success -> {
                _itemDetail.value = result.data
                setUiState(UiState.Success)
            }
            is Resource.Error -> {
                _dismissDialog.value = Unit
                setUiState(UiState.Error(result.message))
            }
            is Resource.Loading -> setUiState(UiState.Loading)
        }
    }

    fun onAmountIncremented() {
        _itemDetail.value?.let { itemDetail ->
            if (_amountsInput.value + 1 <= itemDetail.count) {
                _amountsInput.value++
            }
        }
    }

    fun onAmountDecremented() {
        if (_amountsInput.value > 1) {
            _amountsInput.value--
        }
    }

    fun onSubmitItemToCart(userCart: UserCart?) = viewModelScope.launch {
        if (property.isUpdateAction()) {
            updateUserCartItem()
        } else {
            addUserCartItem(userCart)
        }
    }

    private fun updateUserCartItem() = viewModelScope.launch {
        val params = UpdateUserCartItemParameters(
            cartItemId = property.cartItemId!!,
            amounts = _amountsInput.value
        )

        updateUserCartItemUseCase(params).collect {
            when (it) {
                is Resource.Success -> {
                    _dismissDialog.value = Unit
                    _cartItemSubmitting.value = false
                }
                is Resource.Error -> {
                    _cartItemSubmitting.value = false
                    showToastMessage(it.message)
                }
                is Resource.Loading -> {
                    _cartItemSubmitting.value = true
                }
            }
        }
    }

    private fun addUserCartItem(userCart: UserCart?) = viewModelScope.launch {
        val itemId = _itemDetail.value?.id ?: return@launch
        val params = AddUserCartItemParameters(
            sellerId = property.sellerId,
            sectionId = property.sectionId,
            itemId = itemId,
            amounts = _amountsInput.value,
            currentSellerId = userCart?.sellerId
        )

        addUserCartItemUseCase(params).collect {
            when (it) {
                is Resource.Success -> {
                    _dismissDialog.value = Unit
                    _cartItemSubmitting.value = false
                }
                is Resource.Error -> {
                    _cartItemSubmitting.value = false
                    if (it.message == ERROR_DIFFERENT_SELLER) {
                        _showDiffSellerDialog.value = Unit
                    } else {
                        showToastMessage(it.message)
                    }
                }
                is Resource.Loading -> {
                    _cartItemSubmitting.value = true
                }
            }
        }
    }
}

@Parcelize
data class SellerItemDetailProperty(
    val sellerId: String,
    val itemId: String,
    val sectionId: String? = null,
    // These fields will be used for updating cart item.
    val cartItemId: String? = null,
    val amounts: Int? = null
) : Parcelable {
    fun isUpdateAction(): Boolean = cartItemId != null
}