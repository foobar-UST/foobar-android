package com.foobarust.android.settings

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.settings.ProfileListModel.*
import com.foobarust.android.settings.TextInputType.NAME
import com.foobarust.android.settings.TextInputType.PHONE_NUM
import com.foobarust.domain.models.user.UserDetail
import com.foobarust.domain.models.user.isDataCompleted
import com.foobarust.domain.states.Resource
import com.foobarust.domain.states.getSuccessDataOr
import com.foobarust.domain.usecases.AuthState
import com.foobarust.domain.usecases.common.GetFormattedPhoneNumUseCase
import com.foobarust.domain.usecases.user.GetUserAuthStateUseCase
import com.foobarust.domain.usecases.user.UpdateUserDetailParameters
import com.foobarust.domain.usecases.user.UpdateUserDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

const val EDIT_PROFILE_NAME = "profile_name"
const val EDIT_PROFILE_PHONE_NUMBER = "profile_phone_number"

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val updateUserDetailUseCase: UpdateUserDetailUseCase,
    private val getFormattedPhoneNumUseCase: GetFormattedPhoneNumUseCase,
    getUserAuthStateUseCase: GetUserAuthStateUseCase,
) : ViewModel() {

    private val _profileListModels = MutableStateFlow<List<ProfileListModel>>(emptyList())
    val profileListModels: LiveData<List<ProfileListModel>> = _profileListModels
        .asLiveData(viewModelScope.coroutineContext)

    private val _profileUiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val profileUiState: LiveData<ProfileUiState> = _profileUiState
        .asLiveData(viewModelScope.coroutineContext)

    private val _navigateToTextInput = Channel<TextInputProperty>()
    val navigateToTextInput: Flow<TextInputProperty> = _navigateToTextInput.receiveAsFlow()

    private val _snackBarMessage = Channel<String>()
    val snackBarMessage: Flow<String> = _snackBarMessage.receiveAsFlow()

    init {
        // Build profile list
        viewModelScope.launch {
            getUserAuthStateUseCase(Unit).collect {
                when (it) {
                    is AuthState.Authenticated -> {
                        _profileListModels.value = buildProfileListModels(userDetail = it.data)
                        _profileUiState.value = ProfileUiState.Success
                    }
                    AuthState.Unauthenticated -> {
                        _profileUiState.value = ProfileUiState.Error(
                            context.getString(R.string.auth_signed_out_message)
                        )
                    }
                    AuthState.Loading -> {
                        _profileUiState.value = ProfileUiState.Loading
                    }
                }
            }
        }
    }

    fun updateUserName(name: String) = viewModelScope.launch {
        val params = UpdateUserDetailParameters(name = name)
        updateUserDetailUseCase(params).collect {
            when (it) {
                is Resource.Success -> _snackBarMessage.offer(
                    context.getString(R.string.profile_user_detail_updated)
                )
                is Resource.Error -> _profileUiState.value = ProfileUiState.Error(it.message)
                is Resource.Loading -> _profileUiState.value = ProfileUiState.Loading
            }
        }
    }

    fun updateUserPhoneNum(phoneNum: String) = viewModelScope.launch {
        val params = UpdateUserDetailParameters(phoneNum = phoneNum)
        updateUserDetailUseCase(params).collect {
            when (it) {
                is Resource.Success ->_snackBarMessage.offer(
                    context.getString(R.string.profile_user_detail_updated)
                )
                is Resource.Error -> _profileUiState.value = ProfileUiState.Error(it.message)
                is Resource.Loading -> _profileUiState.value = ProfileUiState.Loading
            }
        }
    }

    fun onNavigateToTextInput(editItemModel: ProfileEditItemModel) {
        _navigateToTextInput.offer(
            when (editItemModel.id) {
                EDIT_PROFILE_NAME -> TextInputProperty(
                    id = editItemModel.id,
                    title = editItemModel.title,
                    value = editItemModel.value,
                    type = NAME
                )
                EDIT_PROFILE_PHONE_NUMBER -> TextInputProperty(
                    id = editItemModel.id,
                    title = editItemModel.title,
                    value = editItemModel.value,
                    type = PHONE_NUM
                )
                else -> throw IllegalStateException("Unknown edit model: ${editItemModel.id}")
            }
        )
    }

    private suspend fun buildProfileListModels(userDetail: UserDetail?): List<ProfileListModel> {
        if (userDetail == null) {
            return emptyList()
        }

        return buildList {
            // Add warning message section
            if (!userDetail.isDataCompleted()) {
                add(ProfileNoticeItemModel(
                    message = context.getString(R.string.profile_require_data_for_ordering)
                ))
            }

            // Add user avatar section
            add(ProfileInfoItemModel(userDetail = userDetail))

            // Add user name section
            add(ProfileEditItemModel(
                id = EDIT_PROFILE_NAME,
                title = context.getString(R.string.profile_edit_field_name),
                value = userDetail.name,
                displayValue = userDetail.name.takeIf { !it.isNullOrEmpty() } ?:
                context.getString(R.string.profile_edit_field_input_not_set)
            ))

            // Add user phone number section
            val formattedPhoneNum = userDetail.phoneNum?.let {
                getFormattedPhoneNumUseCase(it).getSuccessDataOr(null)
            }

            add(ProfileEditItemModel(
                id = EDIT_PROFILE_PHONE_NUMBER,
                title = context.getString(R.string.profile_edit_field_phone_number),
                value = userDetail.phoneNum,
                displayValue = formattedPhoneNum ?:
                context.getString(R.string.profile_edit_field_input_not_set)
            ))
        }
    }
}

sealed class ProfileUiState {
    object Success : ProfileUiState()
    data class Error(val message: String?) : ProfileUiState()
    object Loading : ProfileUiState()
}