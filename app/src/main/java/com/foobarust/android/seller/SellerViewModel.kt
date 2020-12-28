package com.foobarust.android.seller

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.foobarust.android.R
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.sellerdetail.SellerItemDetailProperty
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.domain.models.promotion.SuggestBasic
import com.foobarust.domain.models.seller.SellerBasic
import com.foobarust.domain.models.seller.SellerSectionBasic
import dagger.hilt.android.qualifiers.ApplicationContext

class SellerViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
) : BaseViewModel() {

    val sellerPages: LiveData<List<SellerPage>> = liveData {
        emit(listOf(
            SellerPage(
                title = context.getString(R.string.seller_tab_on_campus),
                fragment = { SellerOnCampusFragment() }
            ),
            SellerPage(
                title = context.getString(R.string.seller_tab_off_campus),
                fragment = { SellerOffCampusFragment() }
            )
        ))
    }

    private val _navigateToSellerDetail = SingleLiveEvent<String>()
    val navigateToSellerDetail: LiveData<String>
        get() = _navigateToSellerDetail

    private val _navigateToSellerAction = SingleLiveEvent<Unit>()
    val navigateToSellerAction: LiveData<Unit>
        get() = _navigateToSellerAction

    private val _navigateToSuggestItem = SingleLiveEvent<SellerItemDetailProperty>()
    val navigateToSuggestItem: LiveData<SellerItemDetailProperty>
        get() = _navigateToSuggestItem

    private val _navigateToSellerSection = SingleLiveEvent<SellerSectionBasic>()
    val navigateToSellerSection: LiveData<SellerSectionBasic>
        get() = _navigateToSellerSection

    fun onNavigateToSellerDetail(sellerBasic: SellerBasic) {
        _navigateToSellerDetail.value = sellerBasic.id
    }

    fun onNavigateToSellerAction() {
        _navigateToSellerAction.value = Unit
    }

    fun onNavigateToSuggestItem(suggestBasic: SuggestBasic) {
        // TODO: onNavigateToSuggestItem
    }

    fun onNavigateToSellerSection(sectionBasic: SellerSectionBasic) {
        _navigateToSellerSection.value = sectionBasic
    }
}