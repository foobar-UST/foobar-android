package com.foobarust.android.seller

import androidx.lifecycle.*
import androidx.paging.*
import com.foobarust.android.promotion.PromotionListModel
import com.foobarust.android.seller.SellersListModel.*
import com.foobarust.domain.models.seller.*
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.promotion.GetAdvertiseBasicsParameters
import com.foobarust.domain.usecases.promotion.GetAdvertiseBasicsUseCase
import com.foobarust.domain.usecases.seller.GetSellersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

private const val NUM_OF_ADVERTISES = 5

@HiltViewModel
class SellerOnCampusViewModel @Inject constructor(
    getAdvertiseBasicsUseCase: GetAdvertiseBasicsUseCase,
    getSellersUseCase: GetSellersUseCase
) : ViewModel() {

    private val _fetchPromotion = ConflatedBroadcastChannel(Unit)

    val promotionListModels: StateFlow<List<PromotionListModel>> = _fetchPromotion
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
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    val sellersListModels: Flow<PagingData<SellersListModel>> = getSellersUseCase(
        SellerBasicsFilter(
            sellerType = SellerType.ON_CAMPUS
        )
    )
        .map { pagingData ->
            pagingData.map { sellerBasic ->
                SellersItemModel(
                    sellerId = sellerBasic.id,
                    sellerName = sellerBasic.getNormalizedName(),
                    sellerType = sellerBasic.type,
                    sellerImageUrl = sellerBasic.imageUrl,
                    sellerRating = sellerBasic.getNormalizedOrderRating(),
                    sellerMinSpend = sellerBasic.minSpend,
                    sellerOnline = sellerBasic.online,
                    sellerTags = sellerBasic.getNormalizedTags()
                )
            }
        }
        .map { pagingData ->
            pagingData.insertSeparators { before, after ->
                return@insertSeparators if (before == null && after == null) {
                    SellersEmptyModel
                } else if (before == null && after is SellersItemModel) {
                    SellersSubtitleModel
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