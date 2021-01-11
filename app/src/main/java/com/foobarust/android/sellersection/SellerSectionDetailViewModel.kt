package com.foobarust.android.sellersection

import android.content.Context
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.sellersection.SectionDetailMoreSectionsListModel.SectionDetailMoreSectionsSectionItem
import com.foobarust.android.sellersection.SellerSectionDetailListModel.*
import com.foobarust.android.states.UiState
import com.foobarust.domain.models.seller.*
import com.foobarust.domain.models.user.UserPublic
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.seller.*
import com.foobarust.domain.utils.cancelIfActive
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Created by kevin on 12/27/20
 */

class SellerSectionDetailViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val getSellerDetailUseCase: GetSellerDetailUseCase,
    private val getMoreSellerSectionsUseCase: GetMoreSellerSectionsUseCase,
    private val getSectionParticipantsUseCase: GetSectionParticipantsUseCase
) : BaseViewModel() {

    private val _participantsInfo = MutableStateFlow<List<UserPublic>>(emptyList())
    private val _sellerDetail = MutableStateFlow<SellerDetail?>(null)
    private val _moreSections = MutableStateFlow<List<SellerSectionBasic>>(emptyList())

    private val _sectionDetailListModels = MutableLiveData<List<SellerSectionDetailListModel>>()
    val sectionDetailListModels: LiveData<List<SellerSectionDetailListModel>>
        get() = _sectionDetailListModels

    private val _showAddItemsButton = MutableLiveData(false)
    val showAddItemsButton: LiveData<Boolean>
        get() = _showAddItemsButton

    private var fetchSectionDataJob: Job? = null

    fun onReceiveSellerDetail(sectionDetail: SellerSectionDetail) {
        _showAddItemsButton.value = true
        fetchSectionDataJob?.cancelIfActive()
        fetchSectionDataJob = viewModelScope.launch {
            fetchSellerDetail(sectionDetail)
            fetchMoreSections(sectionDetail)
            fetchParticipantsInfo(sectionDetail)
            buildSectionDetailList(sectionDetail)
        }
    }

    private fun fetchSellerDetail(sectionDetail: SellerSectionDetail) = viewModelScope.launch {
        when (val result = getSellerDetailUseCase(sectionDetail.sellerId)) {
            is Resource.Success -> _sellerDetail.value = result.data
            is Resource.Loading -> setUiState(UiState.Loading)
            is Resource.Error -> {
                _sellerDetail.value = null
                showToastMessage(result.message)
            }
        }
    }

    private fun fetchMoreSections(sectionDetail: SellerSectionDetail) = viewModelScope.launch {
        when (val result = getMoreSellerSectionsUseCase(
            GetMoreSellerSectionsParameters(
                sellerId = sectionDetail.sellerId,
                numOfSections = 5,
                currentSectionId = sectionDetail.id
            )
        )) {
            is Resource.Success -> _moreSections.value = result.data
            is Resource.Loading -> Unit
            is Resource.Error -> {
                _moreSections.value = emptyList()
                showToastMessage(result.message)
            }
        }
    }

    private fun fetchParticipantsInfo(sectionDetail: SellerSectionDetail) = viewModelScope.launch {
        when (val result = getSectionParticipantsUseCase(
            GetSectionParticipantsParameters(
                userIds = sectionDetail.joinedUsersIds,
                numOfUsers = 10
            )
        )) {
            is Resource.Success -> _participantsInfo.value = result.data
            is Resource.Loading -> Unit
            is Resource.Error -> {
                _participantsInfo.value = emptyList()
                showToastMessage(result.message)
            }
        }
    }

    private fun buildSectionDetailList(sectionDetail: SellerSectionDetail) = viewModelScope.launch {
        combine(
            flowOf(sectionDetail),
            _participantsInfo.asStateFlow(),
            _sellerDetail.asStateFlow(),
            _moreSections.asStateFlow()
        ) { sectionDetail, participantsInfo, sellerDetail, moreSections ->
            buildList {
                // Add participants row
                if (participantsInfo.isNotEmpty()) {
                    add(SellerSectionDetailUsersItemModel(
                        sectionId = sectionDetail.id,
                        usersCount = sectionDetail.joinedUsersCount,
                        maxUsers = sectionDetail.maxUsers,
                        usersPublics = participantsInfo
                    ))
                }

                // Add counter
                Log.d("SellerSectionDetail", "${sectionDetail.isRecentSection()}")
                add(SellerSectionDetailCounterItemModel(
                    cutoffTime = sectionDetail.cutoffTime,
                    isRecentSection = sectionDetail.isRecentSection()
                ))

                // Add order info
                add(SellerSectionDetailSectionInfoItemModel(
                    description = sectionDetail.getNormalizedDescription(),
                    cutoffTime = sectionDetail.getCutoffTimeString(),
                    deliveryDate = sectionDetail.getDeliveryDateString(),
                    deliveryTime = sectionDetail.getDeliveryTimeString(),
                    deliveryLocation = sectionDetail.getNormalizedDeliveryLocation()
                ))

                if (sellerDetail != null) {
                    // Add seller info
                    add(SellerSectionDetailSubtitleItemModel(subtitle = context.getString(R.string.seller_section_detail_seller_info_subtitle)))
                    add(SellerSectionDetailSellerInfoItemModel(
                        sellerId = sellerDetail.id,
                        sellerName = sellerDetail.getNormalizedName(),
                        sellerRating = sellerDetail.getNormalizedRatingString(),
                        sellerAddress = sellerDetail.getNormalizedAddress(),
                        sellerImageUrl = sellerDetail.imageUrl,
                        sellerOnline = sellerDetail.online
                    ))

                    // Add more sections
                    if (moreSections.isNotEmpty()) {
                        val sectionItems = moreSections.map {
                            SectionDetailMoreSectionsSectionItem(
                                sectionId = it.id,
                                sectionTitle = it.getNormalizedTitleForMoreSections(),
                                sectionDeliveryTime = context.getString(R.string.more_sections_section_item_format_delivery_time, it.getDeliveryTimeString()),
                                sectionImageUrl = it.imageUrl
                            )
                        }

                        add(SellerSectionDetailSubtitleItemModel(
                            subtitle = context.getString(R.string.more_sections_subtitle)
                        ))
                        add(SellerSectionDetailMoreSectionsItemModel(
                            sellerId = sellerDetail.id,
                            sectionItems = sectionItems
                        ))
                    }
                }
            }
        }.collectLatest { _sectionDetailListModels.value = it }
    }
}