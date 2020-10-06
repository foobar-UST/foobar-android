package com.foobarust.android.seller

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.foobarust.android.R
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.promotion.PromotionListModel
import com.foobarust.android.promotion.PromotionListModel.*
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.domain.models.AdvertiseBasic
import com.foobarust.domain.models.SuggestBasic
import com.foobarust.domain.states.Resource
import com.foobarust.domain.states.getSuccessDataOr
import com.foobarust.domain.usecases.promotion.GetAdvertiseItemsUseCase
import com.foobarust.domain.usecases.promotion.GetSuggestItemsUseCase
import com.foobarust.domain.usecases.seller.GetSellerListUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SellerViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    getAdvertiseItemsUseCase: GetAdvertiseItemsUseCase,
    getSuggestItemsUseCase: GetSuggestItemsUseCase,
    getSellerListUseCase: GetSellerListUseCase
) : BaseViewModel() {

    private val _loadState = SingleLiveEvent<LoadState>()
    val loadState: LiveData<LoadState>
        get() = _loadState

    // Reload promotion items at launch
    private val reloadPromotionChannel = ConflatedBroadcastChannel(Unit)

    val sellerModelItems: Flow<PagingData<SellerListModel>> = getSellerListUseCase(Unit)
        .map { pagingData -> pagingData.map { SellerListModel.SellerBasicModel(it) as SellerListModel } }
        .map {
            it.insertSeparators { before, _ ->
                // Insert subtitle at the beginning
                return@insertSeparators if (before == null) {
                    SellerListModel.SellerSubtitleModel(context.getString(R.string.seller_list_subtitle))
                } else {
                    null
                }
            }
        }
        .cachedIn(viewModelScope)

    private val advertiseItemsFlow = reloadPromotionChannel.asFlow()
        .flatMapLatest {
            getAdvertiseItemsUseCase(Unit)
                .filterNot { it is Resource.Loading }
                .flatMapLatest { flowOf(it.getSuccessDataOr(emptyList())) }
        }

    private val suggestItemsFlow = reloadPromotionChannel.asFlow()
        .flatMapLatest {
            getSuggestItemsUseCase(Unit)
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

    private fun buildPromotionModelList(
        advertiseBasics: List<AdvertiseBasic>,
        suggestBasics: List<SuggestBasic>
    ): List<PromotionListModel> {
        return buildList {
            // Append advertise row
            if (advertiseBasics.isNotEmpty()) {
                add(PromotionAdvertiseModel(advertiseBasics))
            }

            // Append suggest row
            if (suggestBasics.isNotEmpty()) {
                // Append suggestion row and subtitle
                addAll(listOf(
                    PromotionSubtitleModel(context.getString(R.string.promotion_suggest_subtitle)),
                    PromotionSuggestModel(suggestBasics)
                ))
            }
        }
    }

    fun reloadPromotionItems() {
        // Trigger reload
        reloadPromotionChannel.offer(Unit)
    }

    fun onLoadStateChanged(loadState: LoadState) {
        _loadState.value = loadState
    }
}