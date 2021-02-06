package com.foobarust.android.sellersection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.foobarust.domain.models.seller.SellerSectionDetail
import com.foobarust.domain.usecases.seller.GetSellerSectionsParameters
import com.foobarust.domain.usecases.seller.GetSellerSectionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * Created by kevin on 1/3/21
 */

@HiltViewModel
class SellerSectionMoreSectionsViewModel @Inject constructor(
    private val getSellerSectionsUseCase: GetSellerSectionsUseCase
) : ViewModel() {

    private val _moreSectionsFilter = MutableStateFlow<MoreSectionsFilter?>(null)

    val sectionsListModels: Flow<PagingData<SellerSectionsListModel>> = _moreSectionsFilter
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
                @Suppress("USELESS_CAST")
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
}

private data class MoreSectionsFilter(
    val sellerId: String,
    val currentSectionId: String
)