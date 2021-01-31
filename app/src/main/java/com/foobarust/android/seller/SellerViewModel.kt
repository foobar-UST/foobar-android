package com.foobarust.android.seller

import android.content.Context
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
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellerViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : BaseViewModel() {

    val sellerPages: List<SellerPage> = listOf(
        SellerPage(
            tag = SellerOnCampusFragment.TAG,
            title = context.getString(R.string.seller_tab_on_campus),
            fragment = { SellerOnCampusFragment() }
        ),
        SellerPage(
            tag = SellerOffCampusFragment.TAG,
            title = context.getString(R.string.seller_tab_off_campus),
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

    // Emit the index of the page in ViewPager that needs to be scrolled to top, contains
    // the page tag.
    private val _pageScrollToTop = MutableSharedFlow<String>()
    val pageScrollToTop: SharedFlow<String> = _pageScrollToTop.asSharedFlow()

    // Emit the current scroll state of ViewPager, contains the page tag.
    var currentPageSelected: String? = null

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

    fun onPageScrollToTop() = viewModelScope.launch {
        currentPageSelected?.let {
            _pageScrollToTop.emit(it)
        }
    }

    fun onCurrentPageChanged(tag: String) {
        currentPageSelected = tag
    }
}