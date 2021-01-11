package com.foobarust.android.settings

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.settings.SettingsListModel.SettingsProfileModel
import com.foobarust.android.settings.SettingsListModel.SettingsSectionModel
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.domain.models.user.UserDetail
import com.foobarust.domain.models.user.isSignedIn
import com.foobarust.domain.states.Resource
import com.foobarust.domain.states.getSuccessDataOr
import com.foobarust.domain.usecases.auth.SignOutUseCase
import com.foobarust.domain.usecases.user.GetUserDetailUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

const val SETTINGS_NOTIFICATIONS = "settings_notifications"
const val SETTINGS_CONTACT_US = "settings_contact_us"
const val SETTINGS_TERMS_CONDITIONS = "settings_terms_conditions"
const val SETTINGS_FEATURES = "settings_features"
const val SETTINGS_SIGN_OUT = "settings_sign_out"
const val SETTINGS_FAVORITE = "setting_favorite"

class SettingsViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    getUserDetailUseCase: GetUserDetailUseCase,
    private val signOutUseCase: SignOutUseCase
) : BaseViewModel() {

    private val _navigateToSignIn = SingleLiveEvent<Unit>()
    val navigateToSignIn: LiveData<Unit>
        get() = _navigateToSignIn

    private val _navigateToProfile = SingleLiveEvent<Unit>()
    val navigateToProfile: LiveData<Unit>
        get() = _navigateToProfile

    private val userDetail: Flow<Resource<UserDetail>> = getUserDetailUseCase(Unit)

    val settingsListModels: LiveData<List<SettingsListModel>> = userDetail
        .map { it.getSuccessDataOr(null) }
        .map { buildSettingsListModels(userDetail = it) }
        .asLiveData(viewModelScope.coroutineContext)

    fun onUserAccountClicked() = viewModelScope.launch {
        // Get user detail from replay cache
        val currentUserDetail = (userDetail
            .first { it is Resource.Success } as Resource.Success)
            .data

        if (currentUserDetail.isSignedIn()) {
            _navigateToProfile.value = Unit
        } else {
            _navigateToSignIn.value = Unit
        }
    }

    fun signOut() = viewModelScope.launch {
        signOutUseCase(Unit)
    }

    private fun buildSettingsListModels(userDetail: UserDetail?): List<SettingsListModel> {
        if (userDetail == null) return emptyList()

        return buildList {
            // Load user section when UserDetail is offered
            if (userDetail.isSignedIn()) {
                // For signed in
                add(SettingsProfileModel(
                    username = userDetail.username,
                    photoUrl = userDetail.photoUrl
                ))

                add(SettingsSectionModel(
                    id = SETTINGS_FAVORITE,
                    icon = R.drawable.ic_loyalty,
                    title = context.getString(R.string.settings_section_favorite_title)
                ))
            } else {
                // For signed out
                add(SettingsProfileModel())
            }

            // Setup common sections
            addAll(listOf(
                SettingsSectionModel(
                    id = SETTINGS_NOTIFICATIONS,
                    icon = R.drawable.ic_notification_important,
                    title = context.getString(R.string.settings_section_notifications_title)
                ),
                SettingsSectionModel(
                    id = SETTINGS_FEATURES,
                    icon = R.drawable.ic_whatshot,
                    title = context.getString(R.string.settings_section_features_title)
                ),
                SettingsSectionModel(
                    id = SETTINGS_CONTACT_US,
                    icon = R.drawable.ic_live_help,
                    title = context.getString(R.string.settings_section_contact_us_title)
                ),
                SettingsSectionModel(
                    id = SETTINGS_TERMS_CONDITIONS,
                    icon = R.drawable.ic_copyright,
                    title = context.getString(R.string.settings_section_license_title)
                )
            ))

            // Add sign out button
            if (userDetail.isSignedIn()) {
                add(SettingsSectionModel(
                    id = SETTINGS_SIGN_OUT,
                    icon = R.drawable.ic_exit_to_app,
                    title = context.getString(R.string.settings_section_sign_out_title)
                ))
            }
        }
    }
 }

