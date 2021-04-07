package com.foobarust.android.seller

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.foobarust.android.seller.SellersListModel.*
import com.foobarust.domain.models.explore.getNormalizedTitle
import com.foobarust.domain.models.seller.*
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.seller.GetSellerItemCategoryUseCase
import com.foobarust.domain.usecases.seller.GetSellersPagingUseCase
import com.foobarust.domain.utils.cancelIfActive
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

/**
 * Created by kevin on 2/27/21
 */

@HiltViewModel
class SellerListViewModel @Inject constructor(
    getSellersPagingUseCase: GetSellersPagingUseCase,
    private val getSellerItemCategoryUseCase: GetSellerItemCategoryUseCase
) : ViewModel() {

    private val _sellerListProperty = ConflatedBroadcastChannel<SellerListProperty>()
    val sellerListProperty: StateFlow<SellerListProperty?> = _sellerListProperty
        .asFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

    private val _sellerListFetchUiState = MutableStateFlow<SellerListFetchUiState?>(null)
    val sellerListFetchUiState: StateFlow<SellerListFetchUiState?> = _sellerListFetchUiState
        .asStateFlow()

    private var fetchSellerItemCategoryJob: Job? = null

    val sellersListModels: Flow<PagingData<SellersListModel>> = _sellerListProperty
        .asFlow()
        .flatMapLatest {
            val filter = SellerBasicsFilter(categoryTag = it.categoryTag)
            getSellersPagingUseCase(filter)
        }
        .map { pagingData ->
            pagingData.map { sellerBasic ->
                @Suppress("USELESS_CAST")
                SellersItemModel(
                    sellerId = sellerBasic.id,
                    sellerName = sellerBasic.getNormalizedName(),
                    sellerImageUrl = sellerBasic.imageUrl,
                    sellerRating = sellerBasic.getNormalizedOrderRating(),
                    sellerMinSpend = sellerBasic.minSpend,
                    sellerOnline = sellerBasic.online,
                    sellerTags = sellerBasic.getNormalizedTags()
                ) as SellersListModel
            }
        }
        .map { pagingData ->
            pagingData.insertSeparators { before, after ->
                return@insertSeparators if (before == null && after == null) {
                    SellersEmptyModel
                } else {
                    null
                }
            }
        }
        .cachedIn(viewModelScope)

    fun onFetchCategorySellers(property: SellerListProperty) {
        if (property.fetchRequired()) {
            fetchSellerItemCategoryJob?.cancelIfActive()
            fetchSellerItemCategoryJob = viewModelScope.launch {
                getSellerItemCategoryUseCase(property.categoryTag).collect {
                    when (it) {
                        is Resource.Success -> {
                            val sellerItemCategory = it.data
                            _sellerListProperty.offer(
                                SellerListProperty(
                                    categoryTag = sellerItemCategory.tag,
                                    categoryTitle = sellerItemCategory.getNormalizedTitle(),
                                    categoryImageUrl = sellerItemCategory.imageUrl
                                )
                            )
                            _sellerListFetchUiState.value = SellerListFetchUiState.Success
                        }
                        is Resource.Error -> {
                            _sellerListFetchUiState.value = SellerListFetchUiState.Error(it.message)
                        }
                        is Resource.Loading -> {
                            _sellerListFetchUiState.value = SellerListFetchUiState.Loading
                        }
                    }
                }
            }
        } else {
            _sellerListProperty.offer(property)
        }
    }
}

@Parcelize
data class SellerListProperty(
    val categoryTag: String,
    val categoryTitle: String? = null,
    val categoryImageUrl: String? = null
) : Parcelable {
    fun fetchRequired(): Boolean = categoryTitle == null
}

sealed class SellerListFetchUiState {
    object Success : SellerListFetchUiState()
    data class Error(val message: String?) : SellerListFetchUiState()
    object Loading : SellerListFetchUiState()
}