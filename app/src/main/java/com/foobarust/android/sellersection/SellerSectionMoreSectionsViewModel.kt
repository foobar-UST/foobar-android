package com.foobarust.android.sellersection

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.utils.asUiFetchState
import com.foobarust.domain.models.seller.SellerSectionDetail
import com.foobarust.domain.usecases.seller.GetSellerSectionsParameters
import com.foobarust.domain.usecases.seller.GetSellerSectionsUseCase
import kotlinx.coroutines.flow.*

/**
 * Created by kevin on 1/3/21
 */

class SellerSectionMoreSectionsViewModel @ViewModelInject constructor(
    private val getSellerSectionsUseCase: GetSellerSectionsUseCase
) : BaseViewModel() {

    private val _moreSectionsFilter = MutableStateFlow<MoreSectionsFilter?>(null)

    val sectionsListModels: Flow<PagingData<SellerSectionsListModel>> = _moreSectionsFilter
        .asStateFlow()
        .filterNotNull()
        .flatMapLatest {
            getSellerSectionsUseCase(
                GetSellerSectionsParameters(
                    sellerId = it.sellerId,
                    currentSectionId = it.currentSectionId
                )
            )
        }
        .map { pagingData ->
            pagingData.map {
                SellerSectionsListModel.SellerSectionsItemModel(it) as SellerSectionsListModel
            }
        }
        .cachedIn(viewModelScope)

    fun onFetchSellerSections(sectionDetail: SellerSectionDetail) {
        _moreSectionsFilter.value = MoreSectionsFilter(
            sellerId = sectionDetail.sellerId,
            currentSectionId = sectionDetail.id
        )
    }

    fun onPagingLoadStateChanged(loadState: LoadState) {
        setUiState(loadState.asUiFetchState())
    }
}

private data class MoreSectionsFilter(
    val sellerId: String,
    val currentSectionId: String
)