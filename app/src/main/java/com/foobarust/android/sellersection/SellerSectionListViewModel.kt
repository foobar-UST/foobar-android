package com.foobarust.android.sellersection

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.foobarust.android.R
import com.foobarust.android.sellersection.SellerSectionsListModel.*
import com.foobarust.domain.models.seller.SellerDetail
import com.foobarust.domain.models.seller.isRecentSection
import com.foobarust.domain.states.getSuccessDataOr
import com.foobarust.domain.usecases.seller.GetSellerDetailUseCase
import com.foobarust.domain.usecases.seller.GetSellerSectionsParameters
import com.foobarust.domain.usecases.seller.GetSellerSectionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * Created by kevin on 2/23/21
 */

@HiltViewModel
class SellerSectionListViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    getSellerDetailUseCase: GetSellerDetailUseCase,
    getSellerSectionsUseCase: GetSellerSectionsUseCase
) : ViewModel() {

    private val _sellerId = ConflatedBroadcastChannel<String?>(null)

    val sectionsListModels: Flow<PagingData<SellerSectionsListModel>> = _sellerId
        .asFlow()
        .filterNotNull()
        .flatMapLatest {
            getSellerSectionsUseCase(GetSellerSectionsParameters(it))
        }
        .map { pagingData ->
            pagingData.map {
                SellerSectionsItemModel(it)
            }
        }.map { pagingData ->
            pagingData.insertSeparators { before, after ->
                insertSeparators(before, after)
            }
        }.cachedIn(viewModelScope)

    val sellerDetail: StateFlow<SellerDetail?> = _sellerId
        .asFlow()
        .filterNotNull()
        .flatMapLatest { getSellerDetailUseCase(it) }
        .map { it.getSuccessDataOr(null) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    fun onFetchSellerSections(sellerId: String) {
        _sellerId.offer(sellerId)
    }

    private fun insertSeparators(
        before: SellerSectionsItemModel?,
        after: SellerSectionsItemModel?
    ): SellerSectionsListModel? {
        return if (before == null && after == null) {
            SellerSectionsEmptyModel
        } else if (
            before == null &&
            after?.sellerSectionBasic?.isRecentSection() == true
        ) {
            SellerSectionsSubtitleModel(
                subtitle = context.getString(R.string.seller_section_subtitle_recent)
            )
        } else if (
            after?.sellerSectionBasic?.isRecentSection() == false && (
                before == null || before.sellerSectionBasic.isRecentSection()
                )
        ) {
            SellerSectionsSubtitleModel(
                subtitle = context.getString(R.string.seller_section_subtitle_upcoming)
            )
        } else {
            null
        }
    }
}