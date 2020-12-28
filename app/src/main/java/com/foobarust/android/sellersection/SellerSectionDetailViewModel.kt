package com.foobarust.android.sellersection

import android.content.Context
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.sellersection.MoreSectionsListModel.MoreSectionsSectionItem
import com.foobarust.android.sellersection.SellerSectionDetailListModel.*
import com.foobarust.android.states.UiFetchState
import com.foobarust.domain.models.seller.*
import com.foobarust.domain.models.user.UserPublic
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.seller.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Created by kevin on 12/27/20
 */

class SellerSectionDetailViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val getSellerSectionDetailUseCase: GetSellerSectionDetailUseCase,
    private val getSellerDetailUseCase: GetSellerDetailUseCase,
    private val getMoreSellerSectionsUseCase: GetMoreSellerSectionsUseCase,
    private val getSectionParticipantsUseCase: GetSectionParticipantsUseCase
) : BaseViewModel() {

    private val _sectionDetail = MutableStateFlow<SellerSectionDetail?>(null)
    private val _participantsInfo = MutableStateFlow<List<UserPublic>>(emptyList())
    private val _sellerDetail = MutableStateFlow<SellerDetail?>(null)
    private val _moreSections = MutableStateFlow<List<SellerSectionBasic>>(emptyList())

    private val _sectionDetailListModels = MutableLiveData<List<SellerSectionDetailListModel>>()
    val sectionDetailListModels: LiveData<List<SellerSectionDetailListModel>>
        get() = _sectionDetailListModels

    private var fetchSectionDetailJob: Job? = null

    fun onFetchSectionDetail(sellerId: String, sectionId: String) {
        setUiFetchState(UiFetchState.Loading)

        fetchSectionDetailJob?.cancel()
        fetchSectionDetailJob = viewModelScope.launch {
            fetchSectionDetail(sellerId, sectionId)
            fetchSellerDetail()
            //fetchMoreSections()
            fetchParticipantsInfo()
            buildSectionDetailList()
        }
    }

    private fun fetchSectionDetail(sellerId: String, sectionId: String) = viewModelScope.launch {
        when (val result = getSellerSectionDetailUseCase(
            GetSellerSectionDetailParameters(sellerId, sectionId)
        )) {
            is Resource.Success -> {
                _sectionDetail.value = result.data
                setUiFetchState(UiFetchState.Success)
            }
            is Resource.Loading -> setUiFetchState(UiFetchState.Loading)
            is Resource.Error -> {
                _sectionDetail.value = null
                setUiFetchState(UiFetchState.Error(result.message))
            }
        }
    }

    private fun fetchSellerDetail() = viewModelScope.launch {
        _sectionDetail.asStateFlow()
            .filterNotNull()
            .collect { sectionDetail ->
                when (val result = getSellerDetailUseCase(sectionDetail.sellerId)) {
                    is Resource.Success -> _sellerDetail.value = result.data
                    is Resource.Loading -> setUiFetchState(UiFetchState.Loading)
                    is Resource.Error -> {
                        _sellerDetail.value = null
                        setUiFetchState(UiFetchState.Error(result.message))
                    }
                }
            }
    }

    private fun fetchMoreSections() = viewModelScope.launch {
        _sectionDetail.asStateFlow()
            .filterNotNull()
            .collect { sectionDetail ->
                when (val result = getMoreSellerSectionsUseCase(
                    GetMoreSellerSectionsParameters(
                        sellerId = sectionDetail.sellerId,
                        numOfSections = 5
                    )
                )) {
                    is Resource.Success -> _moreSections.value = result.data
                    is Resource.Loading -> Unit
                    is Resource.Error -> {
                        _moreSections.value = emptyList()
                        setUiFetchState(UiFetchState.Error(result.message))
                    }
                }
            }

    }

    private fun fetchParticipantsInfo() = viewModelScope.launch {
        _sectionDetail.asStateFlow()
            .filterNotNull()
            .flatMapLatest { getSectionParticipantsUseCase(it.joinedUsersIds) }
            .collect {
                when (it) {
                    is Resource.Success -> _participantsInfo.value = it.data
                    is Resource.Loading -> Unit
                    is Resource.Error -> {
                        _participantsInfo.value = emptyList()
                        setUiFetchState(UiFetchState.Error(it.message))
                    }
                }
            }
    }

    private fun buildSectionDetailList() = viewModelScope.launch {
        combine(
            _sectionDetail.asStateFlow().filterNotNull(),
            _participantsInfo.asStateFlow(),
            _sellerDetail.asStateFlow(),
            _moreSections.asStateFlow()
        ) { sectionDetail, participantsInfo, sellerDetail, moreSections ->
            Log.d("DetailViewModel", "participantsInfo: $participantsInfo\nsectionDetail: $sectionDetail\nsellerDetail: $sellerDetail\nmoreSections: $moreSections")

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
                add(SellerSectionDetailCounterItemModel(
                    cutoffTime = sectionDetail.cutoffTime,
                    isRecentSection = sectionDetail.isRecentSection()
                ))

                // Add order info
                add(SellerSectionDetailSectionInfoItemModel(
                    description = sectionDetail.getNormalizedDescription(),
                    cutoffTime = sectionDetail.getCutoffTimeString(),
                    deliveryDate = sectionDetail.getDeliveryDateString(),
                    deliveryTime = sectionDetail.getDeliveryTimeString()
                ))

                if (sellerDetail != null) {
                    // Add shipping info
                    add(SellerSectionDetailSubtitleItemModel(
                        subtitle = context.getString(R.string.seller_section_detail_shipping_info_subtitle)
                    ))
                    add(SellerSectionDetailShippingInfoItemModel(
                        address = sellerDetail.getNormalizedAddress(),
                        geolocation = sellerDetail.location.geolocation
                    ))

                    // Add seller info
                    add(SellerSectionDetailSubtitleItemModel(
                        subtitle = context.getString(R.string.seller_section_detail_seller_info_subtitle)
                    ))
                    add(SellerSectionDetailSellerInfoItemModel(
                        sellerId = sellerDetail.id,
                        sellerName = sellerDetail.getNormalizedName(),
                        sellerRating = sellerDetail.rating,
                        sellerImageUrl = sellerDetail.imageUrl
                    ))

                    // Add more sections
                    if (moreSections.isNotEmpty()) {
                        val sectionItems = moreSections.map {
                            MoreSectionsSectionItem(
                                sectionId = it.id,
                                sectionTitle = it.getNormalizedTitleForMoreSections(),
                                sectionDeliveryTime = it.getDeliveryTimeString(),
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