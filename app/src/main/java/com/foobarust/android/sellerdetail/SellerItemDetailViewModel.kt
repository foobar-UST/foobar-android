package com.foobarust.android.sellerdetail

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.sellerdetail.SellerItemDetailListModel.*
import com.foobarust.android.states.UiFetchState
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.domain.models.SellerItemDetail
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.seller.GetSellerItemDetailUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch

/**
 * Created by kevin on 10/13/20
 */

class SellerItemDetailViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val getSellerItemDetailUseCase: GetSellerItemDetailUseCase
) : BaseViewModel() {

    private val _itemDetailModels = MutableLiveData<List<SellerItemDetailListModel>>()
    val itemDetailModels: LiveData<List<SellerItemDetailListModel>>
        get() = _itemDetailModels

    private val _submittedToCart = SingleLiveEvent<Unit>()
    val submittedToCart: LiveData<Unit>
        get() = _submittedToCart

    fun onReceiveItemInfo(title: String, description: String?, price: Double) {
        _itemDetailModels.value = listOf(
            SellerItemDetailInfoModel(
                itemTitle = title,
                itemDescription = description,
                itemPrice = price
            )
        )
    }

    fun onFetchItemDetail(itemId: String) = viewModelScope.launch {
        when (val resource = getSellerItemDetailUseCase(itemId)) {
            is Resource.Success -> {
                buildItemDetailList(resource.data)
                setUiFetchState(UiFetchState.Success)
            }
            is Resource.Error -> setUiFetchState(UiFetchState.Error(resource.message))
            is Resource.Loading -> setUiFetchState(UiFetchState.Loading)
        }
    }

    fun onSubmitToCart() = viewModelScope.launch {
        _submittedToCart.value = Unit
    }

    private fun buildItemDetailList(itemDetail: SellerItemDetail) {
        _itemDetailModels.value = buildList {
            // Add info item
            add(SellerItemDetailInfoModel(
                itemTitle = itemDetail.title,
                itemDescription = itemDetail.description,
                itemPrice = itemDetail.price
            ))

            // Add choices subtitle and radio group
            if (itemDetail.choices.isNotEmpty()) {
                addAll(listOf(
                    SellerItemDetailSubtitleModel(subtitle = context.getString(R.string.item_detail_choices_subtitle)),
                    SellerItemDetailChoicesModel(choices = itemDetail.choices)
                ))
            }

            // Add extra items subtitle and check boxes
            if (itemDetail.extraItems.isNotEmpty()) {
                add(SellerItemDetailSubtitleModel(subtitle = context.getString(R.string.item_detail_extra_items_subtitle)))
                addAll(itemDetail.extraItems.map { SellerItemDetailExtraItemModel(it) })
            }

            // Add special notes
            addAll(listOf(
                SellerItemDetailSubtitleModel(subtitle = context.getString(R.string.item_detail_notes_subtitle)),
                SellerItemDetailNotesModel
            ))

            // Add submit button
            add(SellerItemDetailSubmitModel())
        }
    }
}