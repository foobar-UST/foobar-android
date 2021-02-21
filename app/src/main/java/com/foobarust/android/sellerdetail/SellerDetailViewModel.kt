package com.foobarust.android.sellerdetail

import android.content.Context
import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.lifecycle.*
import com.foobarust.android.R
import com.foobarust.android.selleritem.SellerItemDetailProperty
import com.foobarust.android.utils.AppBarLayoutState
import com.foobarust.domain.models.cart.UserCart
import com.foobarust.domain.models.seller.*
import com.foobarust.domain.states.Resource
import com.foobarust.domain.states.getSuccessDataOr
import com.foobarust.domain.usecases.cart.GetUserCartUseCase
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
import javax.inject.Inject

/**
 * Created by kevin on 10/4/20
 */

const val SELLER_DETAIL_ACTION_RATING = "action_rating"
const val SELLER_DETAIL_ACTION_TAG = "action_tag"

@HiltViewModel
class SellerDetailViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getSellerDetailWithCatalogsUseCase: GetSellerDetailWithCatalogsUseCase,
    private val getSellerSectionDetailUseCase: GetSellerSectionDetailUseCase,
    getUserCartUseCase: GetUserCartUseCase,
) : ViewModel() {

    private val _sellerDetailUiState = MutableStateFlow<SellerDetailUiState>(SellerDetailUiState.Loading)
    val sellerDetailUiState: LiveData<SellerDetailUiState> = _sellerDetailUiState
        .asLiveData(viewModelScope.coroutineContext)

    private val _sellerDetail = MutableStateFlow<SellerDetail?>(null)
    val sellerDetail: LiveData<SellerDetail> = _sellerDetail
        .filterNotNull()
        .asLiveData(viewModelScope.coroutineContext)

    private val _sellerCatalogs = MutableStateFlow<List<SellerCatalog>>(emptyList())
    val sellerCatalogs: LiveData<List<SellerCatalog>> = _sellerCatalogs
        .asLiveData(viewModelScope.coroutineContext)

    private val _sectionDetail = MutableStateFlow<SellerSectionDetail?>(null)

    private val _sellerDetailProperty = MutableStateFlow<SellerDetailProperty?>(null)

    private val _toolbarScrollState = MutableStateFlow(AppBarLayoutState.IDLE)

    private var fetchSellerDetailJob: Job? = null

    private val _navigateToSellerMisc = Channel<Unit>()
    val navigateToSellerMisc: Flow<Unit> = _navigateToSellerMisc.receiveAsFlow()

    private val _navigateToItemDetail = Channel<SellerItemDetailProperty>()
    val navigateToItemDetail: Flow<SellerItemDetailProperty> = _navigateToItemDetail.receiveAsFlow()

    private val _snackBarMessage = Channel<String>()
    val snackBarMessage: Flow<String> = _snackBarMessage.receiveAsFlow()

    private val _finishSwipeRefresh = Channel<Unit>()
    val finishSwipeRefresh: Flow<Unit> = _finishSwipeRefresh.receiveAsFlow()

    val chipActions: LiveData<List<SellerDetailChipAction>> = _sellerDetail
        .filterNotNull()
        .map { buildChipActionsList(it) }
        .asLiveData(viewModelScope.coroutineContext)

    val sellerInfoSpan: LiveData<String> = _sellerDetail
        .filterNotNull()
        .map { buildSellerInfoSpan(it) }
        .asLiveData(viewModelScope.coroutineContext)

    val toolbarTitle: LiveData<String?> = combine(
        _toolbarScrollState.map { it == AppBarLayoutState.COLLAPSED },
        _sellerDetail.filterNotNull().map { it.getNormalizedName() }
    ) { isCollapsed, sellerName ->
        if (isCollapsed) sellerName else null
    }
        .asLiveData(viewModelScope.coroutineContext)

    val userCart: LiveData<UserCart?> = getUserCartUseCase(Unit)
        .map { it.getSuccessDataOr(null) }
        .asLiveData(viewModelScope.coroutineContext)

    val showCartBottomBar: LiveData<Boolean> = getUserCartUseCase(Unit)
        .map { it.getSuccessDataOr(null) }
        .map { userCart -> userCart != null && userCart.itemsCount > 0 }
        .distinctUntilChanged()
        .asLiveData(viewModelScope.coroutineContext)

    val sellerName: LiveData<String> = _sellerDetail
        .filterNotNull()
        .map { it.getNormalizedName() }
        .asLiveData(viewModelScope.coroutineContext)

    val sellerTypeInfo: LiveData<String> = _sectionDetail
        .map {
            it?.let {
                context.getString(
                    R.string.seller_item_detail_type_info_off_campus,
                    it.getDeliveryTimeString(),
                    it.getDeliveryDateString()
                )
            } ?: context.getString(R.string.seller_item_detail_type_info_on_campus)
        }
        .asLiveData(viewModelScope.coroutineContext)

    fun onFetchSellerDetail(property: SellerDetailProperty, isSwipeRefresh: Boolean = false) {
        _sellerDetailProperty.value = property

        fetchSellerDetailJob?.cancelIfActive()
        fetchSellerDetailJob = viewModelScope.launch {
            fetchSellerDetailWithCatalogs(property.sellerId, isSwipeRefresh)
            property.sectionId?.let {
                fetchSectionDetail(it)
            }
        }
    }

    fun onToolbarScrollStateChanged(scrollState: AppBarLayoutState) {
        _toolbarScrollState.value = scrollState
    }

    fun onNavigateToSellerMisc() {
        _navigateToSellerMisc.offer(Unit)
    }

    fun onNavigateToSellerItemDetail(itemId: String) = viewModelScope.launch {
        // Check if the seller is online
        val sellerDetail = _sellerDetail.firstOrNull() ?: return@launch

        if (sellerDetail.online) {
            _navigateToItemDetail.offer(
                SellerItemDetailProperty(
                    sellerId = sellerDetail.id,
                    itemId = itemId,
                    sectionId = _sellerDetailProperty.value?.sectionId
                )
            )
        } else {
            _snackBarMessage.offer(context.getString(R.string.seller_detail_offline_message))
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
        add(context.getString(
            R.string.seller_detail_format_min_spend,
            sellerDetail.getNormalizedRatingString()
        ))

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

    private fun buildChipActionsList(sellerDetail: SellerDetail) = buildList {
        // Rating
        add(SellerDetailChipAction(
            id = SELLER_DETAIL_ACTION_RATING,
            title = sellerDetail.getNormalizedRatingString(),
            drawableRes = R.drawable.ic_star,
            colorRes = R.color.yellow
        ))

        // Tags
        addAll(sellerDetail.tags.map {
            SellerDetailChipAction(
                id = "${SELLER_DETAIL_ACTION_RATING}_$it",
                title = it
            )
        })
    }
}

@Parcelize
data class SellerDetailProperty(
    val sellerId: String,
    val sectionId: String? = null
) : Parcelable

data class SellerDetailChipAction(
    val id: String,
    val title: String,
    @DrawableRes val drawableRes: Int? = null,
    @ColorRes val colorRes: Int = R.color.material_on_background_emphasis_medium
)

sealed class SellerDetailUiState {
    object Success : SellerDetailUiState()
    data class Error(val message: String?) : SellerDetailUiState()
    object Loading : SellerDetailUiState()
}