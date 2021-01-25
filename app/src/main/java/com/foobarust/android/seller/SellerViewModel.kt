package com.foobarust.android.seller

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.sellerdetail.SellerDetailProperty
import com.foobarust.android.sellerdetail.SellerItemDetailProperty
import com.foobarust.android.sellersection.SellerSectionProperty
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.domain.models.promotion.SuggestBasic
import com.foobarust.domain.models.seller.SellerBasic
import com.foobarust.domain.models.seller.SellerSectionBasic
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SellerViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
) : BaseViewModel() {

    val sellerPages: List<SellerPage> = listOf(
        SellerPage(
            title = context.getString(R.string.seller_type_on_campus),
            fragment = { SellerOnCampusFragment() }
        ),
        SellerPage(
            title = context.getString(R.string.seller_type_off_campus),
            fragment = { SellerOffCampusFragment() }
        )
    )

    private val _navigateToSellerDetail = SingleLiveEvent<SellerDetailProperty>()
    val navigateToSellerDetail: LiveData<SellerDetailProperty>
        get() = _navigateToSellerDetail

    private val _navigateToSellerAction = SingleLiveEvent<Unit>()
    val navigateToSellerAction: LiveData<Unit>
        get() = _navigateToSellerAction

    private val _navigateToSuggestItem = SingleLiveEvent<SellerItemDetailProperty>()
    val navigateToSuggestItem: LiveData<SellerItemDetailProperty>
        get() = _navigateToSuggestItem

    private val _navigateToSellerSection = SingleLiveEvent<SellerSectionProperty>()
    val navigateToSellerSection: LiveData<SellerSectionProperty>
        get() = _navigateToSellerSection

    // Handle scroll to top event, will be observed by SellerOnCampusFragment and
    // SellerOffCampusFragment.
    private val _scrollToTop = MutableSharedFlow<Int>()
    val scrollToTop: SharedFlow<Int> = _scrollToTop.asSharedFlow()

    var currentTabPage: Int = 0

    // From SellerOnCampusFragment
    fun onNavigateToSellerDetail(sellerBasic: SellerBasic) {
        _navigateToSellerDetail.value = SellerDetailProperty(
            sellerId = sellerBasic.id
        )
    }

    fun onNavigateToSellerAction() {
        _navigateToSellerAction.value = Unit
    }

    fun onNavigateToSuggestItem(suggestBasic: SuggestBasic) {
        // TODO: onNavigateToSuggestItem
    }

    fun onNavigateToSellerSection(sectionBasic: SellerSectionBasic) {
        _navigateToSellerSection.value = SellerSectionProperty(
            sectionId = sectionBasic.id,
            sellerId = sectionBasic.sellerId
        )
    }

    fun onScrollToTop() = viewModelScope.launch {
        _scrollToTop.emit(currentTabPage)
    }
}