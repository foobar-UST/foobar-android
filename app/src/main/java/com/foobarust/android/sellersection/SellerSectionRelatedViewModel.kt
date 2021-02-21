package com.foobarust.android.sellersection

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.foobarust.domain.usecases.seller.GetSellerSectionsParameters
import com.foobarust.domain.usecases.seller.GetSellerSectionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

/**
 * Created by kevin on 1/3/21
 */

@HiltViewModel
class SellerSectionRelatedViewModel @Inject constructor(
    private val getSellerSectionsUseCase: GetSellerSectionsUseCase
) : ViewModel() {

    private val _sectionRelatedProperty = MutableStateFlow<SellerSectionRelatedProperty?>(null)

    val sectionsListModels: Flow<PagingData<SellerSectionsListModel>> = _sectionRelatedProperty
        .filterNotNull()
        .flatMapLatest {
            val params = GetSellerSectionsParameters(
                sellerId = it.sellerId,
                ignoreSectionId = it.ignoreSectionId
            )
            getSellerSectionsUseCase(params)
        }
        .map { pagingData ->
            pagingData.map {
                @Suppress("USELESS_CAST")
                SellerSectionsListModel.SellerSectionsItemModel(it) as SellerSectionsListModel
            }
        }
        .cachedIn(viewModelScope)

    fun onFetchRelatedSections(property: SellerSectionRelatedProperty) {
        _sectionRelatedProperty.value = SellerSectionRelatedProperty(
            sellerId = property.sellerId,
            ignoreSectionId = property.ignoreSectionId
        )
    }
}

@Parcelize
data class SellerSectionRelatedProperty(
    val sellerId: String,
    val ignoreSectionId: String
) : Parcelable
