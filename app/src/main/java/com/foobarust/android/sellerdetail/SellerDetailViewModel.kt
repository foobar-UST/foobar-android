package com.foobarust.android.sellerdetail

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.states.UiState
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.domain.models.cart.UserCart
import com.foobarust.domain.models.seller.SellerDetail
import com.foobarust.domain.models.seller.SellerDetailWithCatalogs
import com.foobarust.domain.models.seller.getNormalizedRatingString
import com.foobarust.domain.states.Resource
import com.foobarust.domain.states.getSuccessDataOr
import com.foobarust.domain.usecases.cart.GetUserCartUseCase
import com.foobarust.domain.usecases.seller.GetSellerDetailWithCatalogsUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Created by kevin on 10/4/20
 */

const val SELLER_DETAIL_ACTION_RATING = "action_rating"
const val SELLER_DETAIL_ACTION_TAG = "action_tag"

class SellerDetailViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val getSellerDetailWithCatalogsUseCase: GetSellerDetailWithCatalogsUseCase,
    getUserCartUseCase: GetUserCartUseCase,
) : BaseViewModel() {

    private val userCart: Flow<Resource<UserCart?>> = getUserCartUseCase(Unit)

    private val _sellerDetailWithCatalogs = MutableLiveData<SellerDetailWithCatalogs?>()
    val sellerDetailWithCatalogs: LiveData<SellerDetailWithCatalogs?>
        get() = _sellerDetailWithCatalogs

    private val _showToolbarTitle = MutableLiveData<Boolean>()
    val showToolbarTitle: LiveData<Boolean>
        get() = _showToolbarTitle

    private val _navigateToSellerMisc = SingleLiveEvent<Unit>()
    val navigateToSellerMisc: LiveData<Unit>
        get() = _navigateToSellerMisc

    private val _navigateToItemDetail = SingleLiveEvent<SellerItemDetailProperty>()
    val navigateToItemDetail: LiveData<SellerItemDetailProperty>
        get() = _navigateToItemDetail

    private val _detailActions = MutableLiveData<List<SellerDetailAction>>()
    val detailActions: LiveData<List<SellerDetailAction>>
        get() = _detailActions

    private val _showSnackBarMessage = SingleLiveEvent<String>()
    val showSnackBarMessage: LiveData<String>
        get() = _showSnackBarMessage

    val userCartLiveData: LiveData<UserCart?> = userCart
        .map { it.getSuccessDataOr(null) }
        .asLiveData(viewModelScope.coroutineContext)

    val showCartBottomBar: LiveData<Boolean> = userCart
        .map { it.getSuccessDataOr(null) }
        .map { userCart -> userCart != null && userCart.itemsCount > 0 }
        .distinctUntilChanged()
        .asLiveData(viewModelScope.coroutineContext)

    fun onFetchSellerDetailWithCatalogs(sellerId: String) = viewModelScope.launch {
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

    fun onShowToolbarTitleChanged(isShow: Boolean) {
        _showToolbarTitle.value = isShow
    }

    fun onShowSellerMisc() {
        _navigateToSellerMisc.value = Unit
    }

    fun onShowItemDetailDialog(sellerId: String, itemId: String) {
        val sellerDetail = _sellerDetailWithCatalogs.value?.sellerDetail
        sellerDetail?.let {
            if (it.online) {
                _navigateToItemDetail.value = SellerItemDetailProperty(
                    sellerId = sellerId,
                    itemId = itemId
                )
            } else {
                _showSnackBarMessage.value = context.getString(
                    R.string.seller_status_offline_message
                )
            }
        }
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