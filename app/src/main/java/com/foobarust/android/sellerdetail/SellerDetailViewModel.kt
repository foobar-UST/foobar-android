package com.foobarust.android.sellerdetail

import android.content.Context
import android.os.Parcelable
import androidx.lifecycle.*
import androidx.viewpager2.widget.ViewPager2
import com.foobarust.android.R
import com.foobarust.android.sellerdetail.SellerDetailChipAction.*
import com.foobarust.android.selleritem.SellerItemDetailProperty
import com.foobarust.android.utils.AppBarLayoutState
import com.foobarust.domain.models.seller.*
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.seller.GetSellerDetailWithCatalogsUseCase
import com.foobarust.domain.usecases.seller.GetSellerSectionDetailUseCase
import com.foobarust.domain.utils.cancelIfActive
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import java.util.*
import javax.inject.Inject

/**
 * Created by kevin on 10/4/20
 */

@HiltViewModel
class SellerDetailViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getSellerDetailWithCatalogsUseCase: GetSellerDetailWithCatalogsUseCase,
    private val getSellerSectionDetailUseCase: GetSellerSectionDetailUseCase
) : ViewModel() {

    private val _sellerDetailUiState = MutableStateFlow<SellerDetailUiState>(SellerDetailUiState.Loading)
    val sellerDetailUiState: StateFlow<SellerDetailUiState> = _sellerDetailUiState.asStateFlow()

    private val _sellerDetail = MutableStateFlow<SellerDetail?>(null)
    val sellerDetail: StateFlow<SellerDetail?> = _sellerDetail.asStateFlow()

    private val _sellerCatalogs = MutableStateFlow<List<SellerCatalog>>(emptyList())
    val sellerCatalogs: StateFlow<List<SellerCatalog>> = _sellerCatalogs.asStateFlow()

    private val _sectionDetail = MutableStateFlow<SellerSectionDetail?>(null)
    val sectionDetail: StateFlow<SellerSectionDetail?> = _sectionDetail.asStateFlow()

    private val _sellerDetailProperty = MutableStateFlow<SellerDetailProperty?>(null)

    private val _appBarLayoutState = MutableStateFlow(AppBarLayoutState.IDLE)

    private val _viewPagerState = MutableStateFlow(ViewPager2.SCROLL_STATE_IDLE)

    private val _navigateToItemDetail = Channel<SellerItemDetailProperty>()
    val navigateToItemDetail: Flow<SellerItemDetailProperty> = _navigateToItemDetail.receiveAsFlow()

    private val _snackBarMessage = Channel<String>()
    val snackBarMessage: Flow<String> = _snackBarMessage.receiveAsFlow()

    private val _finishSwipeRefresh = Channel<Unit>()
    val finishSwipeRefresh: Flow<Unit> = _finishSwipeRefresh.receiveAsFlow()

    private var fetchSellerDetailJob: Job? = null

    val sellerInfo: Flow<String> = _sellerDetail
        .filterNotNull()
        .map { buildSellerInfoSpan(it) }

    val chipActions: Flow<List<SellerDetailChipAction>> = _sellerDetail
        .filterNotNull()
        .map { buildChipActionsList(it) }

    val toolbarTitle: Flow<String?> = combine(
        _appBarLayoutState.map { it == AppBarLayoutState.COLLAPSED },
        _sellerDetail.filterNotNull().map { it.getNormalizedName() }
    ) { isCollapsed, sellerName ->
        if (isCollapsed) sellerName else null
    }

    val enableSwipeRefresh: Flow<Boolean> = _appBarLayoutState.combine(
        _viewPagerState
    ) { appBarLayoutState, viewPagerState ->
        appBarLayoutState == AppBarLayoutState.EXPANDED &&
            viewPagerState == ViewPager2.SCROLL_STATE_IDLE
    }

    fun onFetchSellerDetail(property: SellerDetailProperty, isSwipeRefresh: Boolean = false) {
        _sellerDetailProperty.value = property

        fetchSellerDetailJob?.cancelIfActive()
        fetchSellerDetailJob = viewModelScope.launch {
            // Fetch seller detail
            fetchSellerDetailWithCatalogs(property.sellerId, isSwipeRefresh)

            // Fetch section detail
            property.sectionId?.let {
                fetchSectionDetail(it)
            }
        }
    }

    fun onAppBarLayoutStateChanged(state: AppBarLayoutState) {
        _appBarLayoutState.value = state
    }

    fun onViewPagerStateChanged(state: Int) {
        _viewPagerState.value = state
    }

    fun onNavigateToSellerItemDetail(itemId: String) = viewModelScope.launch {
        val sellerDetail = _sellerDetail.value ?: return@launch
        if (sellerDetail.online) {
            _navigateToItemDetail.offer(
                SellerItemDetailProperty(
                    sellerId = sellerDetail.id,
                    itemId = itemId,
                    sectionId = _sellerDetailProperty.value?.sectionId
                )
            )
        } else {
            _snackBarMessage.offer(
                context.getString(R.string.seller_detail_offline_message)
            )
        }
    }

    private fun fetchSellerDetailWithCatalogs(
        sellerId: String,
        isSwipeRefresh: Boolean
    ) = viewModelScope.launch {
        getSellerDetailWithCatalogsUseCase(sellerId).collect {
            when (it) {
                is Resource.Success -> {
                    _sellerDetail.value = it.data.sellerDetail
                    _sellerCatalogs.value = it.data.sellerCatalogs

                    _sellerDetailUiState.value = SellerDetailUiState.Success

                    if (isSwipeRefresh) {
                        _finishSwipeRefresh.offer(Unit)
                    }
                }
                is Resource.Error -> {
                    _sellerDetailUiState.value = SellerDetailUiState.Error(it.message)

                    if (isSwipeRefresh) {
                        _finishSwipeRefresh.offer(Unit)
                    }
                }
                is Resource.Loading -> {
                    if (!isSwipeRefresh) {
                        _sellerDetailUiState.value = SellerDetailUiState.Loading
                    }
                }
            }
        }
    }

    private fun fetchSectionDetail(sectionId: String) = viewModelScope.launch {
        getSellerSectionDetailUseCase(sectionId).collect {
            when (it) {
                is Resource.Success -> {
                    _sectionDetail.value = it.data
                }
                is Resource.Error -> {
                    _sellerDetailUiState.value = SellerDetailUiState.Error(it.message)
                }
                is Resource.Loading -> Unit
            }
        }
    }

    private fun buildSellerInfoSpan(sellerDetail: SellerDetail): String = buildList {
        // Min spend
        add(context.getString(R.string.seller_detail_format_min_spend, sellerDetail.minSpend))

        // Delivery type
        if (sellerDetail.type == SellerType.ON_CAMPUS) {
            add(context.getString(R.string.seller_detail_deliver_type_pick_up))
        } else {
            // TODO: buildSellerInfoSpan
            /*
            add(getString(
                R.string.seller_detail_format_delivery_cost,
                sellerDetail.getNormalizedDeliveryCostString()
            ))

             */
        }
    }.joinToString("  ·  ")

    private fun buildChipActionsList(
        sellerDetail: SellerDetail
    ): List<SellerDetailChipAction> = buildList {
        // Rating chip
        add(SellerDetailChipRating(
            ratingTitle = sellerDetail.getNormalizedOrderRating()
        ))

        // Tags chips
        addAll(sellerDetail.tags.map { tag ->
            SellerDetailChipCategory(categoryTag = tag)
        })
    }
}

@Parcelize
data class SellerDetailProperty(
    val sellerId: String,
    val sectionId: String? = null
) : Parcelable

sealed class SellerDetailChipAction {
    data class SellerDetailChipRating(
        val ratingTitle: String
    ) : SellerDetailChipAction()

    data class SellerDetailChipCategory(
        val categoryTag: String
    ) : SellerDetailChipAction()
}

sealed class SellerDetailUiState {
    object Success : SellerDetailUiState()
    data class Error(val message: String?) : SellerDetailUiState()
    object Loading : SellerDetailUiState()
}