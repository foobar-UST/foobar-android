package com.foobarust.android.settings

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.common.TextInputProperty
import com.foobarust.android.common.TextInputType.NAME
import com.foobarust.android.common.TextInputType.PHONE_NUM
import com.foobarust.android.common.UiState
import com.foobarust.android.settings.ProfileListModel.*
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.domain.models.user.UserDetail
import com.foobarust.domain.models.user.isDataCompleted
import com.foobarust.domain.states.Resource
import com.foobarust.domain.states.getSuccessDataOr
import com.foobarust.domain.usecases.common.GetFormattedPhoneNumUseCase
import com.foobarust.domain.usecases.user.GetUserDetailUseCase
import com.foobarust.domain.usecases.user.UpdateUserDetailParameters
import com.foobarust.domain.usecases.user.UpdateUserDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

const val EDIT_PROFILE_NAME = "profile_name"
const val EDIT_PROFILE_PHONE_NUMBER = "profile_phone_number"

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    getUserDetailUseCase: GetUserDetailUseCase,
    private val updateUserDetailUseCase: UpdateUserDetailUseCase,
    private val getFormattedPhoneNumUseCase: GetFormattedPhoneNumUseCase
) : BaseViewModel() {

    val profileListModels: LiveData<List<ProfileListModel>> = getUserDetailUseCase(Unit)
        .map {
            when (it) {
                is Resource.Success -> {
                    setUiState(UiState.Success)
                    buildProfileListModels(userDetail = it.data)
                }
                is Resource.Error -> {
                    setUiState(UiState.Error(it.message))
                    emptyList()
                }
                is Resource.Loading -> {
                    setUiState(UiState.Loading)
                    emptyList()
                }
            }
        }
        .asLiveData(viewModelScope.coroutineContext)

    private val _navigateToTextInput = SingleLiveEvent<TextInputProperty>()
    val navigateToTextInput: LiveData<TextInputProperty>
        get() = _navigateToTextInput

    private val _snackBarMessage = SingleLiveEvent<String>()
    val snackBarMessage: LiveData<String>
        get() = _snackBarMessage

    fun updateUserName(name: String) = viewModelScope.launch {
        val params = UpdateUserDetailParameters(name = name)
        updateUserDetailUseCase(params).collect {
            when (it) {
                is Resource.Success -> {
                    setUiState(UiState.Success)
                    _snackBarMessage.value = context.getString(R.string.profile_user_detail_updated)
                }
                is Resource.Error -> {
                    setUiState(UiState.Error(it.message))
                }
                is Resource.Loading -> {
                    setUiState(UiState.Loading)
                }
            }
        }
    }

    fun updateUserPhoneNum(phoneNum: String) = viewModelScope.launch {
        val params = UpdateUserDetailParameters(phoneNum = phoneNum)
        updateUserDetailUseCase(params).collect {
            when (it) {
                is Resource.Success -> {
                    setUiState(UiState.Success)
                    _snackBarMessage.value = context.getString(R.string.profile_user_detail_updated)
                }
                is Resource.Error -> {
                    setUiState(UiState.Error(it.message))
                }
                is Resource.Loading -> {
                    setUiState(UiState.Loading)
                }
            }
        }
    }

    fun onNavigateToTextInput(editModel: ProfileEditModel) {
        _navigateToTextInput.value = when (editModel.id) {
            EDIT_PROFILE_NAME -> TextInputProperty(
                id = editModel.id,
                title = editModel.title,
                value = editModel.value,
                type = NAME
            )
            EDIT_PROFILE_PHONE_NUMBER -> TextInputProperty(
                id = editModel.id,
                title = editModel.title,
                value = editModel.value,
                type = PHONE_NUM
            )
            else -> throw IllegalStateException("Unknown edit model: ${editModel.id}")
        }
    }

    private suspend fun buildProfileListModels(userDetail: UserDetail?): List<ProfileListModel> {
        if (userDetail == null) {
            return emptyList()
        }

        return buildList {
            // Add warning message section
            if (!userDetail.isDataCompleted()) {
                add(ProfileWarningModel(
                    message = context.getString(R.string.profile_require_data_for_ordering)
                ))
            }

            // Add user avatar section
            add(ProfileInfoModel(userDetail = userDetail))

            // Add user name section
            add(ProfileEditModel(
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

            add(ProfileEditModel(
                id = EDIT_PROFILE_PHONE_NUMBER,
                title = context.getString(R.string.profile_edit_field_phone_number),
                value = userDetail.phoneNum,
                displayValue = formattedPhoneNum ?:
                context.getString(R.string.profile_edit_field_input_not_set)
            ))
        }
    }
}