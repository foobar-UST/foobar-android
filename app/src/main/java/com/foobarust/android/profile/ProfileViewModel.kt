package com.foobarust.android.profile

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.common.TextInputProperty
import com.foobarust.android.common.TextInputType.NAME
import com.foobarust.android.common.TextInputType.PHONE_NUM
import com.foobarust.android.profile.ProfileListModel.*
import com.foobarust.android.states.UiFetchState
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.domain.models.user.UserDetail
import com.foobarust.domain.models.user.isOrderingAllowed
import com.foobarust.domain.states.Resource
import com.foobarust.domain.states.getSuccessDataOr
import com.foobarust.domain.usecases.common.GetFormattedPhoneNumUseCase
import com.foobarust.domain.usecases.user.GetUserDetailUseCase
import com.foobarust.domain.usecases.user.UpdateUserDetailUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

const val EDIT_PROFILE_NAME = "profile_name"
const val EDIT_PROFILE_PHONE_NUMBER = "profile_phone_number"

class ProfileViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val getUserDetailUseCase: GetUserDetailUseCase,
    private val updateUserDetailUseCase: UpdateUserDetailUseCase,
    private val getFormattedPhoneNumUseCase: GetFormattedPhoneNumUseCase
) : BaseViewModel() {

    private var userDetailCache: UserDetail? = null

    private val _profileListModels = MutableLiveData<List<ProfileListModel>>()
    val profileListModels: LiveData<List<ProfileListModel>>
        get() = _profileListModels

    private val _navigateToTextInput = SingleLiveEvent<TextInputProperty>()
    val navigateToTextInput: LiveData<TextInputProperty>
        get() = _navigateToTextInput

    private var fetchUserDetailJob: Job? = null

    init {
        onFetchUserDetail()
    }

    fun onFetchUserDetail() {
        fetchUserDetailJob?.cancel()
        fetchUserDetailJob = viewModelScope.launch {
            getUserDetailUseCase(Unit).collect {
                when (it) {
                    is Resource.Success -> {
                        setUiFetchState(UiFetchState.Success)
                        buildProfileList(userDetail = it.data)
                        userDetailCache = it.data
                    }
                    is Resource.Error -> setUiFetchState(UiFetchState.Error(it.message))
                    is Resource.Loading -> setUiFetchState(UiFetchState.Loading)
                }
            }
        }
    }

    private fun buildProfileList(userDetail: UserDetail) = viewModelScope.launch {
        _profileListModels.value = buildList {
            // Add warning message section
            if (!userDetail.isOrderingAllowed()) {
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
}