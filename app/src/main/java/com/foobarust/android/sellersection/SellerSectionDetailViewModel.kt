package com.foobarust.android.sellersection

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.sellerdetail.SellerDetailProperty
import com.foobarust.android.sellersection.RelatedSectionsListModel.RelatedSectionsItemModel
import com.foobarust.android.sellersection.SellerSectionDetailListModel.*
import com.foobarust.android.shared.BaseViewModel
import com.foobarust.domain.models.seller.*
import com.foobarust.domain.models.user.UserPublic
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.seller.*
import com.foobarust.domain.utils.cancelIfActive
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by kevin on 12/27/20
 */

private const val NUM_OF_RELATED_SECTIONS = 5
private const val NUM_OF_PARTICIPANTS = 10

@HiltViewModel
class SellerSectionDetailViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getSellerSectionDetailUseCase: GetSellerSectionDetailUseCase,
    private val getRelatedSellerSectionsUseCase: GetRelatedSellerSectionsUseCase,
    private val getSectionParticipantsUseCase: GetSectionParticipantsUseCase
) : BaseViewModel() {

    private val _sellerSectionDetailListModels = MutableStateFlow<List<SellerSectionDetailListModel>>(
        emptyList()
    )
    val sellerSectionDetailListModels: StateFlow<List<SellerSectionDetailListModel>> =
        _sellerSectionDetailListModels.asStateFlow()

    private val _sectionDetailUiState = MutableStateFlow<SellerSectionDetailUiState>(
        SellerSectionDetailUiState.Loading
    )
    val sectionDetailUiState: StateFlow<SellerSectionDetailUiState> = _sectionDetailUiState
        .asStateFlow()

    private val _navigateToSellerDetail = Channel<SellerDetailProperty>()
    val navigateToSellerDetail: Flow<SellerDetailProperty> = _navigateToSellerDetail
        .receiveAsFlow()

    private val _navigateToSectionRelated = Channel<SellerSectionRelatedProperty>()
    val navigateToSectionRelated: Flow<SellerSectionRelatedProperty> = _navigateToSectionRelated
        .receiveAsFlow()

    private val _finishSwipeRefresh = Channel<Unit>()
    val finishSwipeRefresh: Flow<Unit> = _finishSwipeRefresh.receiveAsFlow()

    private val _sectionDetail = MutableStateFlow<SellerSectionDetail?>(null)
    private val _participants = MutableStateFlow<List<UserPublic>>(emptyList())
    private val _relatedSections = MutableStateFlow<List<SellerSectionBasic>>(emptyList())

    val showOpenMenuButton: Flow<Boolean> = _sectionDetail
        .combine(_sectionDetailUiState) { sectionDetail, uiState ->
            sectionDetail?.isRecentSection() == true &&
                uiState is SellerSectionDetailUiState.Success
        }

    val toolbarTitle: LiveData<String> = _sectionDetail
        .filterNotNull()
        .map { it.getNormalizedTitle() }
        .asLiveData(viewModelScope.coroutineContext)

    private var fetchSectionDetailJob: Job? = null

    fun onFetchSectionDetail(sectionId: String, isSwipeRefresh: Boolean = false) {
        fetchSectionDetailJob?.cancelIfActive()
        fetchSectionDetailJob = viewModelScope.launch {
            // Get section detail
            viewModelScope.launch {
                getSellerSectionDetailUseCase(sectionId).collect {
                    when (it) {
                        is Resource.Success -> {
                            _sectionDetail.value = it.data
                            _sectionDetailUiState.value = SellerSectionDetailUiState.Success

                            if (isSwipeRefresh) {
                                _finishSwipeRefresh.offer(Unit) 
                            }
                        }
                        is Resource.Error -> {
                            _sectionDetailUiState.value = SellerSectionDetailUiState.Error(it.message)

                            if (isSwipeRefresh) {
                                _finishSwipeRefresh.offer(Unit)
                            }
                        }
                        is Resource.Loading -> if (!isSwipeRefresh) {
                            _sectionDetailUiState.value = SellerSectionDetailUiState.Loading
                        }
                    }
                }
            }

            // Get participants
            viewModelScope.launch {
                _sectionDetail
                    .filterNotNull()
                    .flatMapLatest {
                        val params = GetSectionParticipantsParameters(
                            userIds = it.joinedUsersIds,
                            numOfUsers = NUM_OF_PARTICIPANTS
                        )
                        getSectionParticipantsUseCase(params)
                    }
                    .collect {
                        when (it) {
                            is Resource.Success -> _participants.value = it.data
                            is Resource.Error -> showToastMessage(it.message)
                            is Resource.Loading -> Unit
                        }
                    }
            }

            // Get related sections
            viewModelScope.launch {
                _sectionDetail
                    .filterNotNull()
                    .flatMapLatest {
                        val params = GetRelatedSellerSectionsParameters(
                            sellerId = it.sellerId,
                            numOfSections = NUM_OF_RELATED_SECTIONS,
                            currentSectionId = it.id
                        )
                        getRelatedSellerSectionsUseCase(params)
                    }
                    .collect {
                        when (it) {
                            is Resource.Success -> _relatedSections.value = it.data
                            is Resource.Error -> showToastMessage(it.message)
                            is Resource.Loading -> Unit
                        }
                    }
            }

            // Build section detail list
            viewModelScope.launch {
                combine(
                    _sectionDetail.filterNotNull(),
                    _participants,
                    _relatedSections
                ) { sectionDetail, participantsInfo, relatedSections ->
                    buildSectionDetailList(sectionDetail, participantsInfo, relatedSections)
                }.collect {
                    _sellerSectionDetailListModels.value = it
                }
            }
        }
    }

    fun onNavigateToSellerDetail() = viewModelScope.launch {
        val sectionDetail = _sectionDetail.firstOrNull()
        sectionDetail?.let {
            _navigateToSellerDetail.offer(
                SellerDetailProperty(
                    sellerId = it.sellerId,
                    sectionId = it.id
                )
            )
        }
    }

    fun onNavigateToSectionRelated() = viewModelScope.launch {
        val sectionDetail = _sectionDetail.firstOrNull()
        sectionDetail?.let {
            _navigateToSectionRelated.offer(
                SellerSectionRelatedProperty(
                    sellerId = it.sellerId,
                    ignoreSectionId = it.id
                )
            )
        }
    }

    private fun buildSectionDetailList(
        sectionDetail: SellerSectionDetail,
        participantsInfo: List<UserPublic>,
        relatedSections: List<SellerSectionBasic>
    ): List<SellerSectionDetailListModel> = buildList {
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

        // Add participants row
        if (sectionDetail.isRecentSection()) {
            add(SellerSectionDetailParticipantsItemModel(
                sectionId = sectionDetail.id,
                usersCount = sectionDetail.joinedUsersCount,
                maxUsers = sectionDetail.maxUsers,
                usersPublics = participantsInfo
            ))
        }

        // Add more sections
        if (relatedSections.isNotEmpty()) {
            val sectionItems = relatedSections.map {
                RelatedSectionsItemModel(
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
            add(SellerSectionDetailRelatedItemModel(
                sellerId = sectionDetail.sellerId,
                itemModels = sectionItems
            ))
        }
    }
}

sealed class SellerSectionDetailUiState {
    object Success : SellerSectionDetailUiState()
    data class Error(val message: String?) : SellerSectionDetailUiState()
    object Loading: SellerSectionDetailUiState()
}