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
import com.foobarust.domain.models.seller.SellerType
import com.foobarust.domain.states.Resource
import com.foobarust.domain.states.getSuccessDataOr
import com.foobarust.domain.usecases.promotion.GetAdvertisesUseCase
import com.foobarust.domain.usecases.promotion.GetSuggestsUseCase
import com.foobarust.domain.usecases.seller.GetSellersUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SellerOnCampusViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    getAdvertisesUseCase: GetAdvertisesUseCase,
    getSuggestsUseCase: GetSuggestsUseCase,
    getSellersUseCase: GetSellersUseCase
) : ViewModel() {

    // Loading state
    private val _loadState = SingleLiveEvent<LoadState>()
    val loadState: LiveData<LoadState>
        get() = _loadState

    // Reload promotion items at launch
    private val reloadPromotionChannel = ConflatedBroadcastChannel(Unit)

    val sellerModelItems: Flow<PagingData<SellerOnCampusListModel>> = getSellersUseCase(SellerType.ON_CAMPUS)
        .map { pagingData -> pagingData.map { SellerOnCampusListModel.SellerOnCampusItemModel(it) /* as SellerOnCampusListModel */ } }
        .map {
            it.insertSeparators { before, after ->
                // Insert subtitle at the beginning
                return@insertSeparators if (before == null) {
                    SellerOnCampusListModel.SellerOnCampusSubtitleModel(context.getString(R.string.seller_subtitle_restaurants))
                } else {
                    null
                }
            }
        }
        .cachedIn(viewModelScope)

    private val advertiseItemsFlow = reloadPromotionChannel.asFlow()
        .flatMapLatest {
            getAdvertisesUseCase(Unit)
                .filterNot { it is Resource.Loading }
                .flatMapLatest { flowOf(it.getSuccessDataOr(emptyList())) }
        }

    private val suggestItemsFlow = reloadPromotionChannel.asFlow()
        .flatMapLatest {
            getSuggestsUseCase(Unit)
                .filterNot { it is Resource.Loading }
                .flatMapLatest { flowOf(it.getSuccessDataOr(emptyList())) }
        }

    private val _promotionModelItems = MutableLiveData<List<PromotionListModel>>()
    val promotionModelItems: LiveData<List<PromotionListModel>>
        get() = _promotionModelItems

    init {
        // Collect promotion items at launch
        viewModelScope.launch {
            advertiseItemsFlow
                .zip(suggestItemsFlow) { advertises, suggests ->
                    buildPromotionModelList(advertises, suggests)
                }
                .collect { _promotionModelItems.value = it }
        }
    }

    fun reloadPromotionItems() {
        // Trigger reload
        reloadPromotionChannel.offer(Unit)
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
                    //PromotionSubtitleModel(context.getString(R.string.promotion_advertise_subtitle)),
                    PromotionAdvertiseModel(advertiseBasics)
                ))
            }

            // Append suggest row
            if (suggestBasics.isNotEmpty()) {
                // Append suggestion row and subtitle
                addAll(listOf(
                    PromotionSubtitleModel(context.getString(R.string.promotion_subtitle_suggest)),
                    PromotionSuggestModel(suggestBasics)
                ))
            }
        }
    }
}