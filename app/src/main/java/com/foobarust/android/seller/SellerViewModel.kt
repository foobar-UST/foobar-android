package com.foobarust.android.seller

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.foobarust.android.R
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.domain.models.SellerBasic
import com.foobarust.domain.models.SuggestBasic
import dagger.hilt.android.qualifiers.ApplicationContext

class SellerViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
) : BaseViewModel() {

    val sellerPages: LiveData<List<SellerPage>> = liveData {
        emit(listOf(
            SellerPage(
                title = context.getString(R.string.seller_on_campus_title),
                fragment = { SellerOnCampusFragment() }
            ),
            SellerPage(
                title = context.getString(R.string.seller_off_campus_title),
                fragment = { SellerOffCampusFragment() }
            )
        ))
    }

    private val _navigateToSellerDetail = SingleLiveEvent<SellerBasic>()
    val navigateToSellerDetail: LiveData<SellerBasic>
        get() = _navigateToSellerDetail

    private val _navigateToSellerAction = SingleLiveEvent<Unit>()
    val navigateToSellerAction: LiveData<Unit>
        get() = _navigateToSellerAction

    private val _navigateToSuggestItem = SingleLiveEvent<SuggestBasic>()
    val navigateToSuggestItem: LiveData<SuggestBasic>
        get() = _navigateToSuggestItem

    fun onNavigateToSellerDetail(sellerBasic: SellerBasic) {
        _navigateToSellerDetail.value = sellerBasic
    }

    fun onNavigateToSellerAction() {
        _navigateToSellerAction.value = Unit
    }

    fun onNavigateToSuggestItem(suggestBasic: SuggestBasic) {
        _navigateToSuggestItem.value = suggestBasic
    }
}