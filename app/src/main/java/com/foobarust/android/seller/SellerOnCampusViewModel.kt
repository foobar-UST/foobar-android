package com.foobarust.android.seller

import android.content.Context
import androidx.lifecycle.*
import androidx.paging.*
import com.foobarust.android.R
import com.foobarust.android.promotion.PromotionListModel
import com.foobarust.android.seller.SellersListModel.*
import com.foobarust.domain.models.seller.*
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.promotion.GetAdvertiseBasicsParameters
import com.foobarust.domain.usecases.promotion.GetAdvertiseBasicsUseCase
import com.foobarust.domain.usecases.seller.GetSellersPagingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

private const val NUM_OF_ADVERTISES = 5

@HiltViewModel
class SellerOnCampusViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    getAdvertiseBasicsUseCase: GetAdvertiseBasicsUseCase,
    getSellersPagingUseCase: GetSellersPagingUseCase
) : ViewModel() {

    private val _fetchPromotion = ConflatedBroadcastChannel(Unit)

    val promotionListModels: LiveData<List<PromotionListModel>> = _fetchPromotion
        .asFlow()
        .flatMapLatest {
            val params = GetAdvertiseBasicsParameters(
                sellerType = SellerType.ON_CAMPUS,
                numOfAdvertises = NUM_OF_ADVERTISES
            )
            getAdvertiseBasicsUseCase(params)
        }
        .map { result ->
            if (result is Resource.Success && result.data.isNotEmpty()) {
                listOf(PromotionListModel.PromotionAdvertiseModel(result.data))
            } else {
                emptyList()
            }
        }
        .filter { it.isNotEmpty() }
        .asLiveData(viewModelScope.coroutineContext)

    val sellersListModels: Flow<PagingData<SellersListModel>> = getSellersPagingUseCase(
        SellerBasicsFilter(
            sellerType = SellerType.ON_CAMPUS
        )
    )
        .map { pagingData ->
            pagingData.map { sellerBasic ->
                SellersItemModel(
                    sellerId = sellerBasic.id,
                    sellerName = sellerBasic.getNormalizedName(),
                    sellerImageUrl = sellerBasic.imageUrl,
                    sellerRating = sellerBasic.getNormalizedOrderRating(),
                    sellerMinSpend = context.getString(
                        R.string.seller_on_campus_item_min_spend,
                        sellerBasic.getNormalizedMinSpendString()
                    ),
                    sellerOnline = sellerBasic.online,
                    sellerTags = sellerBasic.getNormalizedTags()
                )
            }
        }
        .map { pagingData ->
            pagingData.insertSeparators { before, after ->
                return@insertSeparators if (before == null && after == null) {
                    SellersEmptyModel(
                        drawableRes = R.drawable.undraw_empty,
                        emptyMessage = context.getString(R.string.seller_empty_message)
                    )
                } else if (before == null && after is SellersItemModel) {
                    SellersSubtitleModel(subtitle = context.getString(R.string.seller_subtitle))
                } else {
                    null
                }
            }
        }
        .cachedIn(viewModelScope)

    fun onReloadPromotion() {
        _fetchPromotion.offer(Unit)
    }
}