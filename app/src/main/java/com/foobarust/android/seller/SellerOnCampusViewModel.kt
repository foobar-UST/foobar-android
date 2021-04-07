package com.foobarust.android.seller

import androidx.lifecycle.*
import androidx.paging.*
import com.foobarust.android.promotion.PromotionListModel
import com.foobarust.android.seller.SellersListModel.*
import com.foobarust.domain.models.seller.*
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.promotion.GetAdvertiseBasicsParameters
import com.foobarust.domain.usecases.promotion.GetAdvertiseBasicsUseCase
import com.foobarust.domain.usecases.seller.GetSellersPagingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

private const val NUM_OF_ADVERTISES = 5

@HiltViewModel
class SellerOnCampusViewModel @Inject constructor(
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