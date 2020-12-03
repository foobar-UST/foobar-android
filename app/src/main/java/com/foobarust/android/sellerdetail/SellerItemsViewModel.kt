package com.foobarust.android.sellerdetail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.domain.models.seller.SellerItemBasic
import com.foobarust.domain.usecases.seller.GetSellerItemsBasicsParameters
import com.foobarust.domain.usecases.seller.GetSellerItemsUseCase
import kotlinx.coroutines.flow.Flow

/**
 * Created by kevin on 10/4/20
 */

class SellerItemsViewModel @ViewModelInject constructor(
    private val getSellerItemsUseCase: GetSellerItemsUseCase
) : ViewModel() {

    private val _loadState = SingleLiveEvent<LoadState>()
    val loadState: LiveData<LoadState>
        get() = _loadState

    lateinit var sellerItems: Flow<PagingData<SellerItemBasic>>
        private set

    fun onFetchItemsForCategory(sellerId: String, catalogId: String)  {
        sellerItems = getSellerItemsUseCase(
            GetSellerItemsBasicsParameters(
                sellerId = sellerId,
                catalogId = catalogId
            )
        ).cachedIn(viewModelScope)
    }

    fun onLoadStateChanged(loadState: LoadState) {
        _loadState.value = loadState
    }
}