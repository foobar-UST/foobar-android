package com.foobarust.android.seller

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.sellerdetail.SellerDetailProperty
import com.foobarust.domain.models.seller.SellerBasic
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellerViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

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

    private val _navigateToSellerDetail = Channel<SellerDetailProperty>()
    val navigateToSellerDetail: Flow<SellerDetailProperty> = _navigateToSellerDetail.receiveAsFlow()

    private val _navigateToSellerAction = Channel<Unit>()
    val navigateToSellerAction: Flow<Unit> = _navigateToSellerAction.receiveAsFlow()

    // Argument: section id
    private val _navigateToSellerSection = Channel<String>()
    val navigateToSellerSection: Flow<String> = _navigateToSellerSection.receiveAsFlow()


    private val _navigateToPromotionDetail = Channel<String>()
    val navigateToPromotionDetail: Flow<String> = _navigateToPromotionDetail.receiveAsFlow()

    // Emit the index of the page in ViewPager that needs to be scrolled to top,
    // contains the page tag.
    private val _pageScrollToTop = MutableSharedFlow<String>()
    val pageScrollToTop: SharedFlow<String> = _pageScrollToTop.asSharedFlow()

    // Emit the current scroll state of ViewPager, contains the page tag.
    private var currentPageSelected: String? = null

    fun onNavigateToSellerDetail(sellerBasic: SellerBasic) {
        _navigateToSellerDetail.offer(
            SellerDetailProperty(sellerId = sellerBasic.id)
        )
    }

    fun onNavigateToSellerAction() {
        _navigateToSellerAction.offer(Unit)
    }

    fun onNavigateToSellerSection(sectionId: String) {
        _navigateToSellerSection.offer(sectionId)
    }

    fun onNavigateToPromotionDetail(url: String) {
        _navigateToPromotionDetail.offer(url)
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