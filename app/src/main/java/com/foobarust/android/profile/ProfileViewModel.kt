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
import com.foobarust.android.states.UiFetchState
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.domain.models.UserDetail
import com.foobarust.domain.models.isFieldsFulfilledForOrdering
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.user.GetUserDetailObservableUseCase
import com.foobarust.domain.usecases.user.UpdateUserDetailUseCase
import com.foobarust.domain.utils.PhoneUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

const val EDIT_PROFILE_NAME = "profile_name"
const val EDIT_PROFILE_PHONE_NUMBER = "profile_phone_number"

class ProfileViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    getUserDetailObservableUseCase: GetUserDetailObservableUseCase,
    private val updateUserDetailUseCase: UpdateUserDetailUseCase,
    private val phoneUtil: PhoneUtil,
) : BaseViewModel() {

    private val fetchUserDetailChannel = ConflatedBroadcastChannel<Unit>()
    private var userDetailCache: UserDetail? = null

    val profileItems = fetchUserDetailChannel
        .asFlow()
        .flatMapLatest { getUserDetailObservableUseCase(Unit) }
        .mapLatest {
            when (it) {
                is Resource.Success -> {
                    setUiFetchState(UiFetchState.Success)
                    userDetailCache = it.data
                    buildProfileList(it.data)
                }
                is Resource.Loading -> {
                    setUiFetchState(UiFetchState.Loading)
                    emptyList()
                }
                is Resource.Error -> {
                    setUiFetchState(UiFetchState.Error(it.message))
                    emptyList()
                }
            }
        }
        .asLiveData(viewModelScope.coroutineContext)

    private val _navigateToTextInput = SingleLiveEvent<TextInputProperty>()
    val navigateToTextInput: LiveData<TextInputProperty>
        get() = _navigateToTextInput

    init {
        fetchUserDetail()
    }

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

    fun fetchUserDetail() {
        fetchUserDetailChannel.offer(Unit)
    }

    fun updateUserName(name: String) = viewModelScope.launch {
        userDetailCache?.let {
            val updatedUserDetail = it.copy(name = name, updatedAt = null)
            val resource = updateUserDetailUseCase(updatedUserDetail)

            if (resource is Resource.Error) {
                showToastMessage(resource.message)
            }
        }
    }

    fun updateUserPhoneNum(phoneNum: String) = viewModelScope.launch {
        userDetailCache?.let {
            val updatedUserDetail = it.copy(phoneNum = phoneNum, updatedAt = null)
            val resource = updateUserDetailUseCase(updatedUserDetail)

            if (resource is Resource.Error) {
                showToastMessage(resource.message)
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