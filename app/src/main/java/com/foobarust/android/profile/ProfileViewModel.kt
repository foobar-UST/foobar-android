package com.foobarust.android.profile

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.common.TextInputProperty
import com.foobarust.android.common.TextInputType.NAME
import com.foobarust.android.common.TextInputType.PHONE_NUM
import com.foobarust.android.profile.ProfileListModel.*
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.domain.models.UserDetail
import com.foobarust.domain.models.isFieldsFulfilledForOrdering
import com.foobarust.domain.states.Resource.*
import com.foobarust.domain.usecases.user.GetUserDetailObservableUseCase
import com.foobarust.domain.usecases.user.UpdateUserDetailUseCase
import com.foobarust.domain.usecases.user.UpdateUserInfoParameter
import com.foobarust.domain.utils.PhoneUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

const val EDIT_PROFILE_NAME = "profile_name"
const val EDIT_PROFILE_PHONE_NUMBER = "profile_phone_number"

class ProfileViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    getUserDetailObservableUseCase: GetUserDetailObservableUseCase,
    private val updateUserDetailUseCase: UpdateUserDetailUseCase,
    private val phoneUtil: PhoneUtil,
) : BaseViewModel() {

    val profileItems = getUserDetailObservableUseCase(Unit)
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

    private fun buildProfileList(userDetail: UserDetail): List<ProfileListModel> {
        val infoItem = ProfileInfoModel(userDetail = userDetail)
        val orderingWarningItem = ProfileWarningModel(
            message = context.getString(R.string.profile_require_data_for_ordering)
        )
        val editItems = listOf(
            ProfileEditModel(
                id = EDIT_PROFILE_NAME,
                title = context.getString(R.string.profile_edit_field_name),
                value = userDetail.name,
                displayValue = userDetail.name.takeIf {
                    !it.isNullOrEmpty()
                } ?: context.getString(R.string.profile_edit_field_null)
            ),
            ProfileEditModel(
                id = EDIT_PROFILE_PHONE_NUMBER,
                title = context.getString(R.string.profile_edit_field_phone_number),
                value = userDetail.phoneNum,
                displayValue = userDetail.phoneNum?.let {
                    phoneUtil.getFormattedPhoneNumString(it)
                } ?: context.getString(R.string.profile_edit_field_null)
            )
        )

        return buildList {
            if (!userDetail.isFieldsFulfilledForOrdering()) add(orderingWarningItem)
            add(infoItem)
            addAll(editItems)
        }
    }

    fun updateUserName(name: String) = viewModelScope.launch {
        updateUserDetailUseCase(
            UpdateUserInfoParameter(name = name)
        ).let {
            if (it is Error) showMessage(it.message)
        }
    }

    fun updateUserPhoneNum(phoneNum: String) = viewModelScope.launch {
        updateUserDetailUseCase(
            UpdateUserInfoParameter(phoneNum = phoneNum)
        ).let {
            if (it is Error) showMessage(it.message)
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