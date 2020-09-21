package com.foobarust.android.profile

import android.content.Context
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.common.PhoneFormatter
import com.foobarust.android.input.TextInputProperty
import com.foobarust.android.input.TextInputType.NAME
import com.foobarust.android.input.TextInputType.PHONE_NUM
import com.foobarust.android.profile.ProfileListModel.*
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.domain.models.UserDetailInfo
import com.foobarust.domain.models.allowOrdering
import com.foobarust.domain.states.Resource.*
import com.foobarust.domain.usecases.user.GetUserDetailInfoUseCase
import com.foobarust.domain.usecases.user.UpdateUserInfoParameter
import com.foobarust.domain.usecases.user.UpdateUserInfoUseCase
import com.foobarust.domain.usecases.user.UpdateUserPhotoUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

const val EDIT_PROFILE_NAME = "profile_name"
const val EDIT_PROFILE_PHONE_NUMBER = "profile_phone_number"

class ProfileViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    getUserDetailInfoUseCase: GetUserDetailInfoUseCase,
    private val updateUserInfoUseCase: UpdateUserInfoUseCase,
    private val updateUserPhotoUseCase: UpdateUserPhotoUseCase,
    private val phoneFormatter: PhoneFormatter,
) : BaseViewModel() {

    val profileItems = getUserDetailInfoUseCase(Unit)
        .map {
            controlLoadingProgress(isShow = it is Loading)
            when (it) {
                is Success -> buildProfileList(it.data)
                is Loading -> emptyList()
                is Error -> {
                    showNetworkError()
                    showMessage(it.message)
                    emptyList()
                }
            }
        }
        .asLiveData(viewModelScope.coroutineContext)

    private val _navigateToTextInput = SingleLiveEvent<TextInputProperty>()
    val navigateToTextInput: LiveData<TextInputProperty>
        get() = _navigateToTextInput

    private fun buildProfileList(userDetailInfo: UserDetailInfo): List<ProfileListModel> {
        val infoItem = ProfileInfoModel(userDetailInfo = userDetailInfo)
        val orderingWarningItem = ProfileWarningModel(
            message = context.getString(R.string.profile_require_data_for_ordering)
        )
        val editItems = listOf(
            ProfileEditModel(
                id = EDIT_PROFILE_NAME,
                title = context.getString(R.string.profile_edit_field_name),
                value = userDetailInfo.name,
                displayValue = userDetailInfo.name.takeIf {
                    !it.isNullOrEmpty()
                } ?: context.getString(R.string.profile_edit_field_null)
            ),
            ProfileEditModel(
                id = EDIT_PROFILE_PHONE_NUMBER,
                title = context.getString(R.string.profile_edit_field_phone_number),
                value = userDetailInfo.phoneNum,
                displayValue = userDetailInfo.phoneNum?.let {
                    phoneFormatter.getFormattedString(it)
                } ?: context.getString(R.string.profile_edit_field_null)
            )
        )

        return buildList {
            if (!userDetailInfo.allowOrdering()) add(orderingWarningItem)
            add(infoItem)
            addAll(editItems)
        }
    }

    fun updateUserName(name: String) = viewModelScope.launch {
        updateUserInfoUseCase(
            UpdateUserInfoParameter(name = name)
        ).let {
            if (it is Error) showMessage(it.message)
        }
    }

    fun updateUserPhoneNum(phoneNum: String) = viewModelScope.launch {
        updateUserInfoUseCase(
            UpdateUserInfoParameter(phoneNum = phoneNum)
        ).let {
            if (it is Error) showMessage(it.message)
        }
    }

    fun updateUserPhoto(photoUriString: String) = viewModelScope.launch {
        updateUserPhotoUseCase(photoUriString).collect {
            when (it) {
                is Success -> showMessage("Photo uploaded.")
                is Error -> showMessage(it.message)
                is Loading -> Log.d("ProfileViewModel", "progress: ${it.progress}")
            }
        }
    }

    fun onNavigateToTextInput(editModel: ProfileEditModel) {
        _navigateToTextInput.value = when (editModel.id) {
            EDIT_PROFILE_NAME -> TextInputProperty(
                editId = editModel.id,
                title = editModel.title,
                value = editModel.value,
                type = NAME
            )

            EDIT_PROFILE_PHONE_NUMBER -> TextInputProperty(
                editId = editModel.id,
                title = editModel.title,
                value = editModel.value,
                type = PHONE_NUM
            )

            else -> throw IllegalStateException("Unknown edit model: ${editModel.id}")
        }
    }
}