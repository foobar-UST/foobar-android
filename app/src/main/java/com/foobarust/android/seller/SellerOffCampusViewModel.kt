package com.foobarust.android.seller

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.foobarust.android.R
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.domain.models.seller.isRecentSection
import com.foobarust.domain.usecases.seller.GetSellerSectionsUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Created by kevin on 12/21/20
 */

class SellerOffCampusViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val getSellerSectionsUseCase: GetSellerSectionsUseCase
) : ViewModel() {

    private val _loadState = SingleLiveEvent<LoadState>()
    val loadState: LiveData<LoadState>
        get() = _loadState

    val offCampusListModels: Flow<PagingData<SellerOffCampusListModel>> =
        getSellerSectionsUseCase(Unit).map { pagingData ->
            pagingData.map { SellerOffCampusListModel.SellerOffCampusSectionModel(it) }
        }.map { pagingData ->
            pagingData.insertSeparators { before, after ->
                return@insertSeparators if (before == null) {
                    SellerOffCampusListModel.SellerOffCampusSubtitleModel(
                        subtitle = context.getString(R.string.seller_section_subtitle_recent)
                    )
                } else if (after !== null &&
                    before.sellerSectionBasic.isRecentSection() &&
                    !after.sellerSectionBasic.isRecentSection()
                ) {
                    SellerOffCampusListModel.SellerOffCampusSubtitleModel(
                        subtitle = context.getString(R.string.seller_section_subtitle_upcoming)
                    )
                } else {
                    null
                }
            }
        }.cachedIn(viewModelScope)

    fun onLoadStateChanged(loadState: LoadState) {
        _loadState.value = loadState
    }
}