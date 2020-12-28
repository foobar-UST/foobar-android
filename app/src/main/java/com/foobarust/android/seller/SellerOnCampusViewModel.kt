package com.foobarust.android.seller

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.foobarust.android.R
import com.foobarust.android.promotion.PromotionListModel
import com.foobarust.android.promotion.PromotionListModel.*
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.domain.models.promotion.AdvertiseBasic
import com.foobarust.domain.models.promotion.SuggestBasic
import com.foobarust.domain.models.seller.SellerType.*
import com.foobarust.domain.states.Resource
import com.foobarust.domain.states.getSuccessDataOr
import com.foobarust.domain.usecases.promotion.GetAdvertiseBasicsUseCase
import com.foobarust.domain.usecases.promotion.GetSuggestsUseCase
import com.foobarust.domain.usecases.seller.GetSellersUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SellerOnCampusViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    getAdvertiseBasicsUseCase: GetAdvertiseBasicsUseCase,
    getSuggestsUseCase: GetSuggestsUseCase,
    getSellersUseCase: GetSellersUseCase
) : ViewModel() {

    private val _loadState = SingleLiveEvent<LoadState>()
    val loadState: LiveData<LoadState>
        get() = _loadState

    private val _reloadPromotion = MutableStateFlow(Unit)

    private val _promotionListModels = MutableLiveData<List<PromotionListModel>>()
    val promotionModelItems: LiveData<List<PromotionListModel>>
        get() = _promotionListModels

    val onCampusListModels: Flow<PagingData<SellerOnCampusListModel>> = getSellersUseCase(ON_CAMPUS)
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

    // Ignore Loading or Error state when loading promotion items
    private val advertiseBasicsFlow = _reloadPromotion.asStateFlow()
        .flatMapLatest {
            getAdvertiseBasicsUseCase(Unit)
                .filterNot { it is Resource.Loading }
                .flatMapLatest { flowOf(it.getSuccessDataOr(emptyList())) }
        }

    private val suggestItemsFlow = _reloadPromotion.asStateFlow()
        .flatMapLatest {
            getSuggestsUseCase(Unit)
                .filterNot { it is Resource.Loading }
                .flatMapLatest { flowOf(it.getSuccessDataOr(emptyList())) }
        }

    init {
        // Fetch promotion items (advertises row + user suggests row)
        viewModelScope.launch {
            advertiseBasicsFlow.zip(suggestItemsFlow) { advertises, suggests ->
                buildPromotionModelList(advertises, suggests)
            }.collect {
                _promotionListModels.value = it
            }
        }
    }

    fun onReloadPromotion() {
        _reloadPromotion.value = Unit
    }

    fun onLoadStateChanged(loadState: LoadState) {
        _loadState.value = loadState
    }

    private fun buildPromotionModelList(
        advertiseBasics: List<AdvertiseBasic>,
        suggestBasics: List<SuggestBasic>
    ): List<PromotionListModel> {
        return buildList {
            // Append advertise row
            if (advertiseBasics.isNotEmpty()) {
                addAll(listOf(
                    PromotionAdvertiseModel(advertiseBasics)
                ))
            }

            // Append suggest row
            if (suggestBasics.isNotEmpty()) {
                addAll(listOf(
                    PromotionSubtitleModel(context.getString(R.string.promotion_subtitle_suggest)),
                    PromotionSuggestModel(suggestBasics)
                ))
            }
        }
    }
}