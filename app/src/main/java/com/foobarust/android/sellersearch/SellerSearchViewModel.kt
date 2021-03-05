package com.foobarust.android.sellersearch

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.sellersearch.SellerSearchListModel.SellerSearchItemModel
import com.foobarust.domain.models.seller.SellerBasic
import com.foobarust.domain.models.seller.getNormalizedName
import com.foobarust.domain.models.seller.getNormalizedTags
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.seller.SearchSellersParameters
import com.foobarust.domain.usecases.seller.SearchSellersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by kevin on 2/23/21
 */

private const val NUM_OF_RESULTS = 5
private const val SEARCH_RATE_LIMIT = 250L

@HiltViewModel
class SellerSearchViewModel @Inject constructor(
    searchSellersUseCase: SearchSellersUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    private val _searchListModels = MutableStateFlow<List<SellerSearchListModel>>(emptyList())
    val searchListModels: LiveData<List<SellerSearchListModel>> = _searchListModels
        .asLiveData(viewModelScope.coroutineContext)

    private val _searchUiState = MutableStateFlow<SellerSearchUiState?>(null)
    val searchUiState: LiveData<SellerSearchUiState?> = _searchUiState
        .asLiveData(viewModelScope.coroutineContext)

    init {
        viewModelScope.launch {
            _searchQuery
                .flatMapLatest {
                    if (it.isNotBlank()) {
                        // Apply search rate limit to reduce the number of search requests
                        delay(SEARCH_RATE_LIMIT)
                        val params = SearchSellersParameters(
                            searchQuery = it,
                            numOfSellers = NUM_OF_RESULTS,
                        )
                        searchSellersUseCase(params)
                    } else {
                        // Clear list if the input is blank
                        flowOf(Resource.Success(emptyList()))
                    }
                }
                .collectLatest {
                    when (it) {
                        is Resource.Success -> {
                            _searchListModels.value = buildSellerSearchListModels(it.data)
                            _searchUiState.value = SellerSearchUiState.Success
                        }
                        is Resource.Error -> {
                            _searchUiState.value = SellerSearchUiState.Error(it.message)
                        }
                        is Resource.Loading -> {
                            _searchUiState.value = SellerSearchUiState.Loading
                        }
                    }
                }
        }
    }

    fun onUpdateSearchQuery(searchQuery: String) {
        _searchQuery.value = searchQuery
    }

    private fun buildSellerSearchListModels(sellerBasics: List<SellerBasic>): List<SellerSearchListModel> {
        return sellerBasics.map {
            SellerSearchItemModel(
                sellerId = it.id,
                sellerName = it.getNormalizedName(),
                sellerType = it.type,
                sellerTags = it.getNormalizedTags(),
                sellerImageUrl = it.imageUrl
            )
        }
    }
}

sealed class SellerSearchUiState {
    object Success : SellerSearchUiState()
    data class Error(val message: String?) : SellerSearchUiState()
    object Loading : SellerSearchUiState()
}