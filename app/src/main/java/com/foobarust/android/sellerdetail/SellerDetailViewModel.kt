package com.foobarust.android.sellerdetail

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.states.UiFetchState
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.domain.models.seller.SellerDetail
import com.foobarust.domain.models.seller.SellerDetailWithCatalogs
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.seller.GetSellerDetailWithCatalogsUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Created by kevin on 10/4/20
 */

const val SELLER_DETAIL_ACTION_RATING = "action_rating"
const val SELLER_DETAIL_ACTION_TAG = "action_tag"

class SellerDetailViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val getSellerDetailWithCatalogsUseCase: GetSellerDetailWithCatalogsUseCase
) : BaseViewModel() {

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

    fun onFetchSellerDetail(sellerId: String) = viewModelScope.launch {
        getSellerDetailWithCatalogsUseCase(sellerId).collect {
            when (it) {
                is Resource.Success -> {
                    _sellerDetailWithCatalogs.value = it.data
                    buildActionList(sellerDetail = it.data.sellerDetail)
                    setUiFetchState(UiFetchState.Success)
                }
                is Resource.Error -> setUiFetchState(UiFetchState.Error(it.message))
                is Resource.Loading -> setUiFetchState(UiFetchState.Loading)
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
                title = context.getString(R.string.seller_data_format_rating, sellerDetail.rating),
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