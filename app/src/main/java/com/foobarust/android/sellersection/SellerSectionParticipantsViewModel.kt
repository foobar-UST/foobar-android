package com.foobarust.android.sellersection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foobarust.android.sellersection.ParticipantsListModel.ParticipantsListItemModel
import com.foobarust.domain.models.user.UserPublic
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.seller.GetSectionParticipantsParameters
import com.foobarust.domain.usecases.seller.GetSectionParticipantsUseCase
import com.foobarust.domain.utils.cancelIfActive
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by kevin on 1/3/21
 */

@HiltViewModel
class SellerSectionParticipantsViewModel @Inject constructor(
    private val getSectionParticipantsUseCase: GetSectionParticipantsUseCase
) : ViewModel() {

    private val _participantsUiState = MutableStateFlow<SellerSectionParticipantsUiState>(
        SellerSectionParticipantsUiState.Loading
    )
    val participantsUiState: StateFlow<SellerSectionParticipantsUiState> = _participantsUiState
        .asStateFlow()

    private val _participantsListModels = MutableStateFlow<List<ParticipantsListModel>>(emptyList())
    val participantsListModels: StateFlow<List<ParticipantsListModel>> = _participantsListModels
        .asStateFlow()

    private var fetchParticipantsJob: Job? = null

    fun onFetchParticipants(userIds: List<String>) {
        fetchParticipantsJob?.cancelIfActive()
        fetchParticipantsJob = viewModelScope.launch {
            val params = GetSectionParticipantsParameters(userIds)
            getSectionParticipantsUseCase(params).collect {
                when (it) {
                    is Resource.Success -> {
                        _participantsUiState.value = SellerSectionParticipantsUiState.Success
                        _participantsListModels.value = buildParticipantsListModels(it.data)
                    }
                    is Resource.Error -> {
                        _participantsUiState.value = SellerSectionParticipantsUiState.Error(it.message)
                    }
                    is Resource.Loading -> {
                        _participantsUiState.value = SellerSectionParticipantsUiState.Loading
                    }
                }
            }
        }
    }

    private fun buildParticipantsListModels(userPublics: List<UserPublic>): List<ParticipantsListModel> {
        return userPublics.map { ParticipantsListItemModel(it) }
    }
}

sealed class SellerSectionParticipantsUiState {
    object Success : SellerSectionParticipantsUiState()
    data class Error(val message: String?) : SellerSectionParticipantsUiState()
    object Loading : SellerSectionParticipantsUiState()
}