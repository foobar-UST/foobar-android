package com.foobarust.android.sellerdetail

import android.os.Parcelable
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.domain.models.seller.getNormalizedTitle
import com.foobarust.domain.models.seller.purchasable
import com.foobarust.domain.usecases.seller.GetSellerItemsBasicsParameters
import com.foobarust.domain.usecases.seller.GetSellerItemsUseCase
import kotlinx.coroutines.flow.*
import kotlinx.parcelize.Parcelize

/**
 * Created by kevin on 10/4/20
 */

class SellerItemsViewModel @ViewModelInject constructor(
    private val getSellerItemsUseCase: GetSellerItemsUseCase
) : ViewModel() {

    private val _itemsProperty = MutableStateFlow<SellerItemsProperty?>(null)

    val itemsListModels: Flow<PagingData<SellerItemsListModel>> = _itemsProperty
        .filterNotNull()
        .flatMapLatest {
            val params = GetSellerItemsBasicsParameters(
                sellerId = it.sellerId,
                catalogId = it.catalogId
            )
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

    private val _loadState = SingleLiveEvent<LoadState>()
    val loadState: LiveData<LoadState>
        get() = _loadState

    fun onFetchItemsForCategory(property: SellerItemsProperty)  {
        _itemsProperty.value = property
    }

    fun onLoadStateChanged(loadState: LoadState) {
        _loadState.value = loadState
    }
}

@Parcelize
data class SellerItemsProperty(
    val sellerId: String,
    val catalogId: String
) : Parcelable