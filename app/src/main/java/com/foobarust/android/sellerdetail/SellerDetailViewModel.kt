package com.foobarust.android.sellerdetail

import android.content.Context
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.common.UiState
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.domain.models.cart.UserCart
import com.foobarust.domain.models.seller.*
import com.foobarust.domain.states.Resource
import com.foobarust.domain.states.getSuccessDataOr
import com.foobarust.domain.usecases.cart.GetUserCartUseCase
import com.foobarust.domain.usecases.seller.GetSellerDetailWithCatalogsUseCase
import com.foobarust.domain.usecases.seller.GetSellerSectionBasicParameters
import com.foobarust.domain.usecases.seller.GetSellerSectionUseCase
import com.foobarust.domain.utils.cancelIfActive
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
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
    private val getSellerSectionUseCase: GetSellerSectionUseCase,
    private val getSellerDetailWithCatalogsUseCase: GetSellerDetailWithCatalogsUseCase,
    getUserCartUseCase: GetUserCartUseCase,
) : BaseViewModel() {

    private val _detailProperty = MutableStateFlow<SellerDetailProperty?>(null)

    private val _toolbarCollapsed = MutableStateFlow(false)

    private val _sellerDetailWithCatalogs = MutableStateFlow<SellerDetailWithCatalogs?>(null)
    val sellerDetailWithCatalogs: LiveData<SellerDetailWithCatalogs?> = _sellerDetailWithCatalogs
        .asLiveData(viewModelScope.coroutineContext)

    private val _sectionBasic = MutableStateFlow<SellerSectionBasic?>(null)
    val sectionBasic: LiveData<SellerSectionBasic?> = _sectionBasic
        .asLiveData(viewModelScope.coroutineContext)

    private val _navigateToSellerMisc = SingleLiveEvent<Unit>()
    val navigateToSellerMisc: LiveData<Unit>
        get() = _navigateToSellerMisc

    private val _navigateToItemDetail = SingleLiveEvent<SellerItemDetailProperty>()
    val navigateToItemDetail: LiveData<SellerItemDetailProperty>
        get() = _navigateToItemDetail

    private val _detailActions = MutableLiveData<List<SellerDetailAction>>()
    val detailActions: LiveData<List<SellerDetailAction>>
        get() = _detailActions

    private val _snackBarMessage = SingleLiveEvent<String>()
    val snackBarMessage: LiveData<String>
        get() = _snackBarMessage

    val sellerInfoLine: LiveData<String> = _sellerDetailWithCatalogs
        .filterNotNull()
        .map { buildSellerInfoLine(it.sellerDetail) }
        .asLiveData(viewModelScope.coroutineContext)

    val toolbarTitle: LiveData<String?> = _toolbarCollapsed
        .combine(
            _sellerDetailWithCatalogs.filterNotNull()
                .map { it.sellerDetail.getNormalizedName() }
        ) { collapsed, sellerName ->
            if (collapsed) sellerName else null
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

    val typeInfo: LiveData<String> = _detailProperty
        .filterNotNull()
        .combine(_sectionBasic.asStateFlow()) { property, section ->
            if (property.isOrderSectionState()) {
                context.getString(
                    R.string.seller_item_detail_type_info_off_campus,
                    section?.getDeliveryTimeString(),
                    section?.getDeliveryDateString()
                )
            } else {
                context.getString(R.string.seller_item_detail_type_info_on_campus)
            }
        }
        .asLiveData(viewModelScope.coroutineContext)

    val noticeInfo: LiveData<String?> = _sellerDetailWithCatalogs
        .map {
            when (it?.sellerDetail?.online) {
                true -> it.sellerDetail.notice
                false -> context.getString(R.string.seller_detail_offline_message)
                else -> null
            }
        }
        .asLiveData(viewModelScope.coroutineContext)

    private var fetchSellerDetailJob: Job? = null

    fun onFetchSellerDetail(property: SellerDetailProperty) {
        _detailProperty.value = property
        fetchSellerDetailJob?.cancelIfActive()
        fetchSellerDetailJob = viewModelScope.launch {
            fetchSellerDetailWithCatalogs(sellerId = property.sellerId)
            // Fetch section for off-campus section
            if (property.isOrderSectionState()) {
                fetchSellerSectionBasic(
                    sellerId = property.sellerId,
                    sectionId = property.sectionId!!
                )
            }
        }
    }

    fun onToolbarScrollStateChanged(isCollapsed: Boolean) {
        _toolbarCollapsed.value = isCollapsed
    }

    fun onNavigateToSellerMisc() {
        _navigateToSellerMisc.value = Unit
    }

    fun onNavigateToSellerItemDetail(sellerId: String, itemId: String) {
        val sellerDetail = _sellerDetailWithCatalogs.value?.sellerDetail
        sellerDetail?.let {
            if (it.online) {
                _navigateToItemDetail.value = SellerItemDetailProperty(
                    sellerId = sellerId,
                    itemId = itemId,
                    sectionId = _detailProperty.value?.sectionId
                )
            } else {
                // Deny adding item when the seller is offline
                _snackBarMessage.value = context.getString(
                    R.string.seller_detail_offline_message
                )
            }
        }
    }

    private fun fetchSellerDetailWithCatalogs(sellerId: String) = viewModelScope.launch {
        getSellerDetailWithCatalogsUseCase(sellerId).collect {
            when (it) {
                is Resource.Success -> {
                    _sellerDetailWithCatalogs.value = it.data
                    buildActionList(sellerDetail = it.data.sellerDetail)
                    setUiState(UiState.Success)
                }
                is Resource.Error -> setUiState(UiState.Error(it.message))
                is Resource.Loading -> setUiState(UiState.Loading)
            }
        }
    }

    private fun fetchSellerSectionBasic(sellerId: String, sectionId: String) = viewModelScope.launch {
        val params = GetSellerSectionBasicParameters(sellerId, sectionId)
        getSellerSectionUseCase(params).collect {
            when (it) {
                is Resource.Success -> _sectionBasic.value = it.data
                is Resource.Error -> showToastMessage(it.message)
                is Resource.Loading -> Unit
            }
        }
    }

    private fun buildSellerInfoLine(sellerDetail: SellerDetail): String {
        return buildList {
            // Min spend
            add(context.getString(
                R.string.seller_detail_format_min_spend,
                sellerDetail.getNormalizedRatingString()
            ))
            // Delivery type
            if (sellerDetail.type == SellerType.ON_CAMPUS) {
                add(context.getString(R.string.seller_detail_deliver_type_pick_up))
            } else {
                /*
                add(getString(
                    R.string.seller_detail_format_delivery_cost,
                    sellerDetail.getNormalizedDeliveryCostString()
                ))

                 */
            }
        }
            .joinToString("  ·  ")
    }

    private fun buildActionList(sellerDetail: SellerDetail) {
        _detailActions.value = buildList {
            // Rating
            add(SellerDetailAction(
                id = SELLER_DETAIL_ACTION_RATING,
                title = sellerDetail.getNormalizedRatingString(),
                drawableRes = R.drawable.ic_star,
                colorRes = R.color.yellow
            ))

            // Tags
            addAll(sellerDetail.tags.map {
                SellerDetailAction(
                    id = "${SELLER_DETAIL_ACTION_RATING}_$it",
                    title = it
                )
            })
        }
    }
}

@Parcelize
data class SellerDetailProperty(
    val sellerId: String,
    val sectionId: String? = null
) : Parcelable {
    fun isOrderSectionState() = sectionId != null
}