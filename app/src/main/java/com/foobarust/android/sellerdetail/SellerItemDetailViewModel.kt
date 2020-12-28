package com.foobarust.android.sellerdetail

import android.os.Parcelable
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.states.UiFetchState
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.domain.models.seller.SellerItemDetail
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.cart.AddUserCartItemParameters
import com.foobarust.domain.usecases.cart.AddUserCartItemUseCase
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
    private val addUserCartItemUseCase: AddUserCartItemUseCase
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

    fun onFetchItemDetail(property: SellerItemDetailProperty) = viewModelScope.launch {
        this@SellerItemDetailViewModel.property = property

        val parameters = GetSellerItemDetailParameters(
            sellerId = property.sellerId,
            itemId = property.itemId
        )

        when (val resource = getSellerItemDetailUseCase(parameters)) {
            is Resource.Success -> {
                _itemDetail.value = resource.data
                setUiFetchState(UiFetchState.Success)
            }
            is Resource.Error -> {
                _dismissDialog.value = Unit
                setUiFetchState(UiFetchState.Error(resource.message))
            }
            is Resource.Loading -> setUiFetchState(UiFetchState.Loading)
        }
    }

    fun onAmountIncremented() {
        // TODO: set maximum amount
        _amountsInput.value++
    }

    fun onAmountDecremented() {
        if (_amountsInput.value > 1) {
            _amountsInput.value--
        }
    }

    fun onSubmitItemToCart() = viewModelScope.launch {
        _itemDetail.value?.let { itemDetail ->
            _cartItemSubmitting.value = true

            val params = AddUserCartItemParameters(
                sellerId = property.sellerId,
                itemId = itemDetail.id,
                amounts = _amountsInput.value
            )
            addUserCartItemUseCase(params).collect {
                when (it) {
                    is Resource.Success -> {
                        _dismissDialog.value = Unit
                        _cartItemSubmitting.value = false
                    }
                    is Resource.Error -> {
                        _cartItemSubmitting.value = false
                        showToastMessage(it.message)
                    }
                    is Resource.Loading -> Unit
                }
            }
        }
    }

    //fun isSubmittingToCart(): Boolean = _isSubmitting.value
}

@Parcelize
data class SellerItemDetailProperty(
    val sellerId: String,
    val itemId: String
) : Parcelable