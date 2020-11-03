package com.foobarust.android.sellerdetail

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.sellermisc.SellerMiscProperty
import com.foobarust.android.states.UiFetchState
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.domain.models.SellerDetail
import com.foobarust.domain.models.SellerItemBasic
import com.foobarust.domain.models.SellerType
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.seller.GetSellerDetailUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Created by kevin on 10/4/20
 */

const val SELLER_DETAIL_ACTION_RATING = "action_rating"
const val SELLER_DETAIL_ACTION_TYPE = "action_type"
const val SELLER_DETAIL_ACTION_DELIVERY = "action_delivery"
const val SELLER_DETAIL_ACTION_MIN_SPEND = "action_min_spend"

class SellerDetailViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val getSellerDetailUseCase: GetSellerDetailUseCase
) : BaseViewModel() {

    private val _sellerDetail = MutableLiveData<SellerDetail>()
    val sellerDetail: LiveData<SellerDetail>
        get() = _sellerDetail

    private val _showToolbarTitle = MutableLiveData<Boolean>()
    val showToolbarTitle: LiveData<Boolean>
        get() = _showToolbarTitle

    private val _navigateToSellerMisc = SingleLiveEvent<SellerMiscProperty>()
    val navigateToSellerMisc: LiveData<SellerMiscProperty>
        get() = _navigateToSellerMisc

    private val _navigateToItemDetail = SingleLiveEvent<SellerItemBasic>()
    val navigateToItemDetail: LiveData<SellerItemBasic>
        get() = _navigateToItemDetail

    private val _detailActions = MutableLiveData<List<SellerDetailAction>>()
    val detailActions: LiveData<List<SellerDetailAction>>
        get() = _detailActions

    fun onFetchSellerDetail(sellerId: String) = viewModelScope.launch {
        getSellerDetailUseCase(sellerId).collect {
            when (it) {
                is Resource.Success -> {
                    setUiFetchState(UiFetchState.Success)
                    _sellerDetail.value = it.data
                    buildActionList(it.data)
                }
                is Resource.Loading -> setUiFetchState(UiFetchState.Loading)
                is Resource.Error -> setUiFetchState(UiFetchState.Error(it.message))
            }
        }
    }

    fun onShowToolbarTitleChanged(isShow: Boolean) {
        _showToolbarTitle.value = isShow
    }

    fun onShowSellerMisc() {
        _navigateToSellerMisc.value = _sellerDetail.value!!.let {
            SellerMiscProperty(
                sellerName = it.name,
                email = it.email,
                description = it.description,
                phoneNum = it.phoneNum,
                address = it.location.address,
                latitude = it.location.geoLocation.latitude,
                longitude = it.location.geoLocation.longitude,
                openingHours = it.openingHours
            )
        }
    }

    fun onShowItemDetailDialog(sellerItemBasic: SellerItemBasic) {
        _navigateToItemDetail.value = sellerItemBasic
    }

    private fun buildActionList(sellerDetail: SellerDetail) {
        _detailActions.value = buildList {
            // Rating
            add(SellerDetailAction(
                id = SELLER_DETAIL_ACTION_RATING,
                title = context.getString(R.string.seller_rating_format, sellerDetail.rating),
                drawableRes = R.drawable.ic_star,
                colorRes = R.color.yellow
            ))

            // Min spend
            sellerDetail.minSpend?.let {
                add(SellerDetailAction(
                    id = SELLER_DETAIL_ACTION_MIN_SPEND,
                    title = context.getString(R.string.seller_min_spend_format, it),
                    drawableRes = R.drawable.ic_attach_money
                ))
            }

            // On-campus or off-campus
            add(SellerDetailAction(
                id = SELLER_DETAIL_ACTION_TYPE,
                title = context.getString(
                    if (sellerDetail.type == SellerType.ON_CAMPUS)
                        R.string.seller_on_campus_title
                    else
                        R.string.seller_off_campus_title
                ),
                drawableRes = if (sellerDetail.type == SellerType.ON_CAMPUS) R.drawable.ic_school
                    else R.drawable.ic_local_dining
            ))

            // Delivery type
            add(SellerDetailAction(
                id = SELLER_DETAIL_ACTION_DELIVERY,
                title = context.getString(
                    if (sellerDetail.type == SellerType.ON_CAMPUS)
                        R.string.seller_pick_up
                    else
                        R.string.seller_delivery
                ),
                drawableRes = if (sellerDetail.type == SellerType.ON_CAMPUS) R.drawable.ic_directions_run
                    else R.drawable.ic_local_shipping
            ))
        }
    }
}