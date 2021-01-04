package com.foobarust.android.seller

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.*
import com.foobarust.android.R
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.promotion.PromotionListModel
import com.foobarust.android.utils.asUiFetchState
import com.foobarust.domain.models.seller.SellerType.*
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.promotion.GetAdvertiseBasicsUseCase
import com.foobarust.domain.usecases.seller.GetSellersUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*

class SellerOnCampusViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    getAdvertiseBasicsUseCase: GetAdvertiseBasicsUseCase,
    getSellersUseCase: GetSellersUseCase,
) : BaseViewModel() {

    private val _startFetching = ConflatedBroadcastChannel(Unit)
    private val _reloadPromotion = ConflatedBroadcastChannel<Unit>()

    val promotionListModels: LiveData<List<PromotionListModel>> = flowOf(
        _startFetching.asFlow(),
        _reloadPromotion.asFlow()
    )
        .flattenMerge()
        .flatMapLatest { getAdvertiseBasicsUseCase(Unit) }
        .map { result ->
            if (result is Resource.Success && result.data.isNotEmpty()) {
                listOf(PromotionListModel.PromotionAdvertiseModel(result.data))
            } else {
                emptyList()
            }
        }
        .asLiveData(viewModelScope.coroutineContext)

    val onCampusListModels: Flow<PagingData<SellerOnCampusListModel>> = _startFetching.asFlow()
        .flatMapLatest { getSellersUseCase(ON_CAMPUS) }
        .map { pagingData ->
            pagingData.map { SellerOnCampusListModel.SellerOnCampusItemModel(it) }
        }
        .map { pagingData ->
            pagingData.insertSeparators { before, _ ->
                // Insert subtitle before sellers list
                return@insertSeparators if (before == null) {
                    SellerOnCampusListModel.SellerOnCampusSubtitleModel(
                        subtitle = context.getString(R.string.seller_subtitle)
                    )
                } else {
                    null
                }
            }
        }
        .cachedIn(viewModelScope)

    fun onReloadPromotion() {
        _reloadPromotion.offer(Unit)
    }

    fun onPagingLoadStateChanged(loadState: LoadState) {
        setUiFetchState(loadState.asUiFetchState())
    }
}