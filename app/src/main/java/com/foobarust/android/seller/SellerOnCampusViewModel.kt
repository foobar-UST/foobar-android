package com.foobarust.android.seller

import android.content.Context
import androidx.lifecycle.*
import androidx.paging.*
import com.foobarust.android.R
import com.foobarust.android.promotion.PromotionListModel
import com.foobarust.android.seller.SellerOnCampusListModel.*
import com.foobarust.domain.models.seller.SellerType
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


    val onCampusListModels: Flow<PagingData<SellerOnCampusListModel>> = getSellersPagingUseCase(SellerType.ON_CAMPUS)
        .map { pagingData ->
            pagingData.map { SellerOnCampusItemModel(it) }
        }
        .map { pagingData ->
            pagingData.insertSeparators { before, _ ->
                return@insertSeparators if (before == null) {
                    SellerOnCampusSubtitleModel(
                        subtitle = context.getString(R.string.seller_subtitle)
                    )
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