package com.foobarust.android.sellerdetail

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.foobarust.domain.models.seller.getNormalizedTitle
import com.foobarust.domain.models.seller.purchasable
import com.foobarust.domain.usecases.seller.GetSellerItemsParameters
import com.foobarust.domain.usecases.seller.GetSellerItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

/**
 * Created by kevin on 10/4/20
 */

@HiltViewModel
class SellerItemsViewModel @Inject constructor(
    private val getSellerItemsUseCase: GetSellerItemsUseCase
) : ViewModel() {

    private val _itemsProperty = MutableStateFlow<SellerItemsProperty?>(null)

    val itemsListModels: Flow<PagingData<SellerItemsListModel>> = _itemsProperty
        .filterNotNull()
        .flatMapLatest {
            val params = GetSellerItemsParameters(sellerId = it.sellerId, catalogId = it.catalogId)
            getSellerItemsUseCase(params)
        }.map { pagingData ->
            pagingData.map {
                SellerItemsListModel(
                    itemId = it.id,
                    itemTitle = it.getNormalizedTitle(),
                    itemPrice = it.price,
                    itemPurchasable = it.purchasable()
                )
            }
        }
        .cachedIn(viewModelScope)

    fun onFetchItemsForCategory(property: SellerItemsProperty)  {
        _itemsProperty.value = property
    }
}

@Parcelize
data class SellerItemsProperty(
    val sellerId: String,
    val catalogId: String
) : Parcelable