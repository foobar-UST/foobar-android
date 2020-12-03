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
import com.foobarust.domain.models.user.UserCartItem
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.seller.GetSellerItemDetailParameters
import com.foobarust.domain.usecases.seller.GetSellerItemDetailUseCase
import com.foobarust.domain.usecases.user.AddUserCartItemUseCase
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.*

/**
 * Created by kevin on 10/13/20
 */

class SellerItemDetailViewModel @ViewModelInject constructor(
    private val getSellerItemDetailUseCase: GetSellerItemDetailUseCase,
    private val addUserCartItemUseCase: AddUserCartItemUseCase
) : BaseViewModel() {

    private val itemDetailChannel = ConflatedBroadcastChannel<SellerItemDetail?>()
    private val amountInputChannel = ConflatedBroadcastChannel(1)
    private val isSubmittingToCartChannel = ConflatedBroadcastChannel(false)

    val itemDetail: LiveData<SellerItemDetail?> = itemDetailChannel.asFlow()
        .asLiveData(viewModelScope.coroutineContext)

    val amountInput: LiveData<Int> = amountInputChannel.asFlow()
        .asLiveData(viewModelScope.coroutineContext)

    val isSubmittingToCart: LiveData<Boolean> = isSubmittingToCartChannel.asFlow()
        .asLiveData(viewModelScope.coroutineContext)

    val finalPrice: LiveData<Double> = itemDetailChannel.asFlow()
        .combine(amountInputChannel.asFlow()) { itemDetail, amount ->
            itemDetail?.let { it.price * amount } ?: 0.toDouble()
        }
        .asLiveData(viewModelScope.coroutineContext)

    var notesInput: String? = null

    private val _dismissDialog = SingleLiveEvent<Unit>()
    val closeDialog: LiveData<Unit>
        get() = _dismissDialog

    /*
    private val _itemDetailModels = MutableLiveData<List<SellerItemDetailListModel>>()
    val itemDetailModels: LiveData<List<SellerItemDetailListModel>>
        get() = _itemDetailModels

     */

    fun onFetchItemDetail(property: SellerItemDetailProperty) = viewModelScope.launch {
        when (val resource = getSellerItemDetailUseCase(
            GetSellerItemDetailParameters(
                sellerId = property.sellerId,
                itemId = property.itemId
            )
        )) {
            is Resource.Success -> {
                itemDetailChannel.offer(resource.data)
                //buildItemDetailList()
                setUiFetchState(UiFetchState.Success)
            }
            is Resource.Error -> {
                setUiFetchState(UiFetchState.Error(resource.message))
                _dismissDialog.value = Unit
            }
            is Resource.Loading -> setUiFetchState(UiFetchState.Loading)
        }
    }

    fun onAmountIncremented() {
        amountInputChannel.run {
            // TODO: set maximum amount
            offer(value + 1)
        }
    }

    fun onAmountDecremented() {
        amountInputChannel.run {
            if (value > 1) offer(value - 1)
        }
    }

    fun onNotesChanged(notes: String) {
        notesInput = notes
    }

    fun onSubmitItemToCart() = viewModelScope.launch {
        // TODO: Migrate to backend, also fix id
        itemDetailChannel.valueOrNull?.let { itemDetail ->
            isSubmittingToCartChannel.offer(true)

            val newCartItem = UserCartItem(
                id = UUID.randomUUID().toString(),
                itemId = itemDetail.id,
                itemTitle = itemDetail.title,
                itemTitleZh = itemDetail.titleZh,
                itemPrice = itemDetail.price,
                itemImageUrl = itemDetail.imageUrl,
                amounts = amountInputChannel.value,
                totalPrice = itemDetail.price * amountInputChannel.value,
                notes = notesInput,
                updatedAt = null
            )

            when (val result = addUserCartItemUseCase(newCartItem)) {
                is Resource.Success -> {
                    _dismissDialog.value = Unit
                    isSubmittingToCartChannel.offer(false)
                    showToastMessage("Added to Cart.")
                }
                is Resource.Error -> {
                    isSubmittingToCartChannel.offer(false)
                    showToastMessage(result.message)
                }

            }
        }
    }

    fun isSubmittingToCart(): Boolean = isSubmittingToCartChannel.value

    /*
    private fun buildItemDetailList() {
        _itemDetailModels.value = buildList {
            // Add special notes
            addAll(listOf(
                SellerItemDetailSubtitleModel(subtitle = context.getString(R.string.seller_item_subtitle_notes)),
                SellerItemDetailNotesModel
            ))

            // Add amount widget
            addAll(listOf(
                SellerItemDetailSubtitleModel(subtitle = context.getString(R.string.seller_item_subtitle_amount)),
                SellerItemDetailAmountModel()
            ))
        }
    }

     */
}

@Parcelize
data class SellerItemDetailProperty(
    val sellerId: String,
    val itemId: String
) : Parcelable