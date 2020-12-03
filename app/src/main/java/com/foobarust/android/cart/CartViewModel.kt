package com.foobarust.android.cart

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.cart.CartListModel.CartPurchaseItemModel
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.states.UiFetchState
import com.foobarust.domain.models.user.UserCartItem
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.seller.GetSellerBasicUseCase
import com.foobarust.domain.usecases.user.GetUserCartItemsUseCase
import com.foobarust.domain.usecases.user.RemoveUserCartItemUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * Created by kevin on 12/1/20
 */
class CartViewModel @ViewModelInject constructor(
    getUserCartItemsUseCase: GetUserCartItemsUseCase,
    private val getSellerBasicUseCase: GetSellerBasicUseCase,
    private val removeUserCartItemUseCase: RemoveUserCartItemUseCase
) : BaseViewModel() {

    private val _cartListModels = MutableLiveData<List<CartListModel>>()
    val cartListModels: LiveData<List<CartListModel>>
        get() = _cartListModels

    init {
        getUserCartItemsUseCase(Unit).onEach {
            when (it) {
                is Resource.Success -> {
                    setUiFetchState(UiFetchState.Success)
                    buildCartList(userCartItems = it.data)
                }
                is Resource.Loading -> setUiFetchState(UiFetchState.Loading)
                is Resource.Error -> setUiFetchState(UiFetchState.Error(it.message))
            }
        }.launchIn(viewModelScope)
    }

    fun onRemoveCartItem(userCartItem: UserCartItem) = viewModelScope.launch {
        when (val result = removeUserCartItemUseCase(userCartItem)) {
            is Resource.Success -> Unit
            is Resource.Error -> showToastMessage(result.message)
            is Resource.Loading -> Unit
        }
    }

    private fun buildCartList(userCartItems: List<UserCartItem>) {
        _cartListModels.value = buildList {
            addAll(userCartItems.map {
                CartPurchaseItemModel(userCartItem = it)
            })
        }
    }
}