package com.foobarust.android.seller

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.*
import com.foobarust.android.R
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.common.OnSwipeRefreshListener
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
) : BaseViewModel(), OnSwipeRefreshListener {

    private val _fetchOnCampus = ConflatedBroadcastChannel(Unit)
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


    val onCampusListModels: Flow<PagingData<SellerOnCampusListModel>> = _fetchOnCampus
        .asFlow()
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

    private val _isSwipeRefreshing = MutableLiveData(false)
    val isSwipeRefreshing: LiveData<Boolean>
        get() = _isSwipeRefreshing

    override fun onSwipeRefresh() {
        _isSwipeRefreshing.value = true
    }

    fun onReloadPromotion() {
        _fetchPromotion.offer(Unit)
    }

    fun onPagingLoadStateChanged(loadState: LoadState) {
        if (_isSwipeRefreshing.value == true && loadState is LoadState.Loading) {
            return
        }
        if (loadState is LoadState.NotLoading || loadState is LoadState.Error) {
            _isSwipeRefreshing.value = false
        }

        setUiState(loadState.asUiFetchState())
    }
}