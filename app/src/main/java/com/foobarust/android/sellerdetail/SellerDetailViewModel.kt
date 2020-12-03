package com.foobarust.android.sellerdetail

import android.content.Context
import android.os.Parcelable
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.sellermisc.SellerMiscProperty
import com.foobarust.android.states.UiFetchState
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.domain.models.seller.*
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.seller.GetSellerCatalogsUseCase
import com.foobarust.domain.usecases.seller.GetSellerDetailUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * Created by kevin on 10/4/20
 */

const val SELLER_DETAIL_ACTION_RATING = "action_rating"
const val SELLER_DETAIL_ACTION_TAG = "action_tag"

class SellerDetailViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val getSellerDetailUseCase: GetSellerDetailUseCase,
    private val getSellerCatalogsUseCase: GetSellerCatalogsUseCase
) : BaseViewModel() {

    private val _sellerDetail = MutableLiveData<SellerDetail>()
    val sellerDetail: LiveData<SellerDetail>
        get() = _sellerDetail

    private val _sellerCatalogs = MutableLiveData<List<SellerCatalog>>()
    val sellerCatalogs: LiveData<List<SellerCatalog>>
        get() = _sellerCatalogs

    private val _showToolbarTitle = MutableLiveData<Boolean>()
    val showToolbarTitle: LiveData<Boolean>
        get() = _showToolbarTitle

    private val _navigateToSellerMisc = SingleLiveEvent<SellerMiscProperty>()
    val navigateToSellerMisc: LiveData<SellerMiscProperty>
        get() = _navigateToSellerMisc

    private val _navigateToItemDetail = SingleLiveEvent<SellerItemDetailProperty>()
    val navigateToItemDetail: LiveData<SellerItemDetailProperty>
        get() = _navigateToItemDetail

    private val _detailActions = MutableLiveData<List<SellerDetailAction>>()
    val detailActions: LiveData<List<SellerDetailAction>>
        get() = _detailActions

    fun onFetchSellerDetail(sellerId: String) = viewModelScope.launch {
        // Fetch seller detail
        getSellerDetailUseCase(sellerId).onEach {
            when (it) {
                is Resource.Success -> {
                    _sellerDetail.value = it.data
                    buildActionList(it.data)
                    setUiFetchState(UiFetchState.Success)
                }
                is Resource.Loading -> setUiFetchState(UiFetchState.Loading)
                is Resource.Error -> setUiFetchState(UiFetchState.Error(it.message))
            }
        }.launchIn(this)

        // Fetch seller catalogs
        getSellerCatalogsUseCase(sellerId).onEach {
            when (it) {
                is Resource.Success -> {
                    _sellerCatalogs.value = it.data
                }
                is Resource.Loading -> setUiFetchState(UiFetchState.Loading)
                is Resource.Error -> setUiFetchState(UiFetchState.Error(it.message))
            }
        }.launchIn(this)
    }

    fun onShowToolbarTitleChanged(isShow: Boolean) {
        _showToolbarTitle.value = isShow
    }

    fun onShowSellerMisc() {
        _navigateToSellerMisc.value = _sellerDetail.value!!.let {
            SellerMiscProperty(
                name = it.getNormalizedName(),
                description = it.getNormalizedDescription(),
                address = it.getNormalizedAddress(),
                phoneNum = it.phoneNum,
                website = it.website,
                latitude = it.location.geolocation.latitude,
                longitude = it.location.geolocation.longitude,
                openingHours = it.openingHours
            )
        }
    }

    fun onShowItemDetailDialog(sellerId: String, itemId: String) {
        _navigateToItemDetail.value = SellerItemDetailProperty(
            sellerId = sellerId,
            itemId = itemId
        )
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

@Parcelize
data class SellerDetailProperty(
    val id: String,
    val name: String,
    val imageUrl: String?
) : Parcelable