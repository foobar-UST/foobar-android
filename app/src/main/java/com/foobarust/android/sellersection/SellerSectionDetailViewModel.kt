package com.foobarust.android.sellersection

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.sellersection.SectionDetailMoreSectionsListModel.SectionDetailMoreSectionsSectionItem
import com.foobarust.android.sellersection.SellerSectionDetailListModel.*
import com.foobarust.domain.models.seller.*
import com.foobarust.domain.models.user.UserPublic
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.seller.GetMoreSellerSectionsParameters
import com.foobarust.domain.usecases.seller.GetMoreSellerSectionsUseCase
import com.foobarust.domain.usecases.seller.GetSectionParticipantsParameters
import com.foobarust.domain.usecases.seller.GetSectionParticipantsUseCase
import com.foobarust.domain.utils.cancelIfActive
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by kevin on 12/27/20
 */

@HiltViewModel
class SellerSectionDetailViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getMoreSellerSectionsUseCase: GetMoreSellerSectionsUseCase,
    private val getSectionParticipantsUseCase: GetSectionParticipantsUseCase
) : BaseViewModel() {

    private val _sectionDetail = MutableStateFlow<SellerSectionDetail?>(null)
    private val _participantsInfo = MutableStateFlow<List<UserPublic>>(emptyList())
    private val _moreSections = MutableStateFlow<List<SellerSectionBasic>>(emptyList())

    val sectionDetailListModels: LiveData<List<SellerSectionDetailListModel>> = combine(
        _sectionDetail.filterNotNull(),
        _participantsInfo,
        _moreSections
    ) { sectionDetail, participantsInfo, moreSections ->
        buildSectionDetailList(sectionDetail, participantsInfo, moreSections)
    }
        .asLiveData(viewModelScope.coroutineContext)

    val isRecentSection: LiveData<Boolean> = _sectionDetail
        .map { it?.isRecentSection() ?: false }
        .asLiveData(viewModelScope.coroutineContext)

    private var fetchSectionDataJob: Job? = null

    fun onFetchSectionDetail(sectionDetail: SellerSectionDetail) {
        _sectionDetail.value = sectionDetail
        fetchSectionDataJob?.cancelIfActive()
        fetchSectionDataJob = viewModelScope.launch {
            fetchMoreSections(sectionDetail)
            fetchParticipantsInfo(sectionDetail)
        }
    }

    private fun fetchMoreSections(sectionDetail: SellerSectionDetail) = viewModelScope.launch {
        val params =  GetMoreSellerSectionsParameters(
            sellerId = sectionDetail.sellerId,
            numOfSections = 5,
            currentSectionId = sectionDetail.id
        )
        getMoreSellerSectionsUseCase(params).collect {
            when (it) {
                is Resource.Success -> {
                    _moreSections.value = it.data
                }
                is Resource.Error -> {
                    _moreSections.value = emptyList()
                    showToastMessage(it.message)
                }
                is Resource.Loading -> Unit
            }
        }
    }

    private fun fetchParticipantsInfo(sectionDetail: SellerSectionDetail) = viewModelScope.launch {
        val params = GetSectionParticipantsParameters(
            userIds = sectionDetail.joinedUsersIds,
            numOfUsers = 10
        )
        getSectionParticipantsUseCase(params).collect {
            when (it) {
                is Resource.Success -> {
                    _participantsInfo.value = it.data
                }
                is Resource.Error -> {
                    _participantsInfo.value = emptyList()
                    showToastMessage(it.message)
                }
                is Resource.Loading -> Unit
            }
        }
    }

    private fun buildSectionDetailList(
        sectionDetail: SellerSectionDetail,
        participantsInfo: List<UserPublic>,
        moreSections: List<SellerSectionBasic>
    ): List<SellerSectionDetailListModel> {
        return buildList {
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
                deliveryLocation = sectionDetail.getNormalizedDeliveryAddress()
            ))

            // Add more sections
            if (moreSections.isNotEmpty()) {
                val sectionItems = moreSections.map {
                    SectionDetailMoreSectionsSectionItem(
                        sectionId = it.id,
                        sectionTitle = it.getNormalizedTitleForMoreSections(),
                        sectionDeliveryTime = context.getString(
                            R.string.more_sections_section_item_format_delivery_time,
                            it.getDeliveryTimeString()
                        ),
                        sectionImageUrl = it.imageUrl
                    )
                }

                add(SellerSectionDetailSubtitleItemModel(
                    subtitle = context.getString(R.string.more_sections_subtitle)
                ))
                add(SellerSectionDetailMoreSectionsItemModel(
                    sellerId = sectionDetail.sellerId,
                    sectionItems = sectionItems
                ))
            }
        }
    }
}