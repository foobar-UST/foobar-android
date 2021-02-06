package com.foobarust.android.seller

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.foobarust.android.R
import com.foobarust.android.promotion.PromotionListModel
import com.foobarust.android.sellersection.SellerSectionsListModel
import com.foobarust.domain.models.seller.isRecentSection
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.promotion.GetAdvertiseBasicsUseCase
import com.foobarust.domain.usecases.seller.GetSellerSectionsParameters
import com.foobarust.domain.usecases.seller.GetSellerSectionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * Created by kevin on 12/21/20
 */

@HiltViewModel
class SellerOffCampusViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    getAdvertiseBasicsUseCase: GetAdvertiseBasicsUseCase,
    getSellerSectionsUseCase: GetSellerSectionsUseCase
) : ViewModel() {

    private val _fetchPromotion = ConflatedBroadcastChannel(Unit)

    val promotionListModels: LiveData<List<PromotionListModel>> = _fetchPromotion
        .asFlow()
        .flatMapLatest { getAdvertiseBasicsUseCase(Unit) }
        .map { result ->
            if (result is Resource.Success && result.data.isNotEmpty()) {
                listOf(PromotionListModel.PromotionAdvertiseModel(result.data))
            } else {
                emptyList()
            }
        }
        .filter { it.isNotEmpty() }
        .asLiveData(viewModelScope.coroutineContext)

    val sectionsListModels: Flow<PagingData<SellerSectionsListModel>> =
        getSellerSectionsUseCase(
            GetSellerSectionsParameters()
        ).map { pagingData ->
            pagingData.map { SellerSectionsListModel.SellerSectionsItemModel(it) }
        }.map { pagingData ->
            pagingData.insertSeparators { before, after ->
                return@insertSeparators if (before == null) {
                    SellerSectionsListModel.SellerSectionsSubtitleModel(
                        subtitle = context.getString(R.string.seller_section_subtitle_recent)
                    )
                } else if (after !== null &&
                    before.sellerSectionBasic.isRecentSection() &&
                    !after.sellerSectionBasic.isRecentSection()
                ) {
                    SellerSectionsListModel.SellerSectionsSubtitleModel(
                        subtitle = context.getString(R.string.seller_section_subtitle_upcoming)
                    )
                } else {
                    null
                }
            }
        }.cachedIn(viewModelScope)

    fun onReloadPromotion() {
        _fetchPromotion.offer(Unit)
    }
}