package com.foobarust.android.seller

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.foobarust.android.R
import com.foobarust.android.promotion.PromotionListModel
import com.foobarust.android.promotion.PromotionListModel.PromotionAdvertiseModel
import com.foobarust.android.sellersection.SellerSectionsListModel
import com.foobarust.android.sellersection.SellerSectionsListModel.*
import com.foobarust.domain.models.seller.SellerType
import com.foobarust.domain.models.seller.isRecentSection
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.promotion.GetAdvertiseBasicsParameters
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

private const val NUM_OF_ADVERTISES = 5

@HiltViewModel
class SellerOffCampusViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    getAdvertiseBasicsUseCase: GetAdvertiseBasicsUseCase,
    getSellerSectionsUseCase: GetSellerSectionsUseCase
) : ViewModel() {

    private val _fetchPromotion = ConflatedBroadcastChannel(Unit)

    val promotionListModels: LiveData<List<PromotionListModel>> = _fetchPromotion
        .asFlow()
        .flatMapLatest {
            val params = GetAdvertiseBasicsParameters(
                sellerType = SellerType.OFF_CAMPUS,
                numOfAdvertises = NUM_OF_ADVERTISES
            )
            getAdvertiseBasicsUseCase(params)
        }
        .map { result ->
            if (result is Resource.Success && result.data.isNotEmpty()) {
                listOf(PromotionAdvertiseModel(result.data))
            } else {
                emptyList()
            }
        }
        .filter { it.isNotEmpty() }
        .asLiveData(viewModelScope.coroutineContext)

    val sectionsListModels: Flow<PagingData<SellerSectionsListModel>> = getSellerSectionsUseCase(
            GetSellerSectionsParameters()
        ).map { pagingData ->
            pagingData.map {
                SellerSectionsItemModel(it)
            }
        }.map { pagingData ->
            pagingData.insertSeparators { before, after ->
                insertSeparators(before, after)
            }
        }.cachedIn(viewModelScope)

    fun onReloadPromotion() {
        _fetchPromotion.offer(Unit)
    }

    private fun insertSeparators(
        before: SellerSectionsListModel?,
        after: SellerSectionsListModel?
    ): SellerSectionsListModel? {
        return if (before == null && after == null) {
            SellerSectionsEmptyModel(
                drawableRes = R.drawable.undraw_empty,
                emptyMessage = context.getString(R.string.seller_section_empty_message)
            )
        } else if (
            before == null &&
            after is SellerSectionsItemModel && after.sellerSectionBasic.isRecentSection()
        ) {
            // Insert recent section subtitle
            SellerSectionsSubtitleModel(
                subtitle = context.getString(R.string.seller_section_subtitle_recent)
            )
        } else if (
            after is SellerSectionsItemModel && !after.sellerSectionBasic.isRecentSection() &&
            (before == null || before is SellerSectionsItemModel &&
                before.sellerSectionBasic.isRecentSection())
        ) {
            // Insert upcoming section subtitle
            SellerSectionsSubtitleModel(
                subtitle = context.getString(R.string.seller_section_subtitle_upcoming)
            )
        } else {
            null
        }
    }
}