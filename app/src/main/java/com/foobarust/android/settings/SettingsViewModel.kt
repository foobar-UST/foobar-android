package com.foobarust.android.settings

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.foobarust.android.R
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.settings.SettingsListModel.SettingsProfileModel
import com.foobarust.android.settings.SettingsListModel.SettingsSectionModel
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.android.works.UploadUserPhotoWork
import com.foobarust.domain.models.user.UserDetail
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.AuthState
import com.foobarust.domain.usecases.auth.SignOutUseCase
import com.foobarust.domain.usecases.user.DoOnSignOutUseCase
import com.foobarust.domain.usecases.user.GetUserAuthStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

const val SETTINGS_NOTIFICATIONS = "settings_notifications"
const val SETTINGS_CONTACT_US = "settings_contact_us"
const val SETTINGS_TERMS_CONDITIONS = "settings_terms_conditions"
const val SETTINGS_FEATURES = "settings_features"
const val SETTINGS_SIGN_OUT = "settings_sign_out"
const val SETTINGS_FAVORITE = "setting_favorite"

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val workManager: WorkManager,
    getUserAuthStateUseCase: GetUserAuthStateUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val doOnSignOutUseCase: DoOnSignOutUseCase
) : BaseViewModel() {

    private val _navigateToSignIn = SingleLiveEvent<Unit>()
    val navigateToSignIn: LiveData<Unit>
        get() = _navigateToSignIn

    private val _navigateToProfile = SingleLiveEvent<Unit>()
    val navigateToProfile: LiveData<Unit>
        get() = _navigateToProfile

    private val _userSignedOut = SingleLiveEvent<Unit>()
    val userSignedOut: LiveData<Unit>
        get() = _userSignedOut

    val settingsListModels: LiveData<List<SettingsListModel>> = getUserAuthStateUseCase(Unit)
        .map {
            when (it) {
                is AuthState.Authenticated -> buildSettingsListModels(it.data)
                AuthState.Unauthenticated -> buildSettingsListModels()
                AuthState.Loading -> emptyList()
            }
        }
        .asLiveData(viewModelScope.coroutineContext)

    fun onUserAccountClicked(isSignedIn: Boolean) = viewModelScope.launch {
        if (isSignedIn) {
            _navigateToProfile.value = Unit
        } else {
            _navigateToSignIn.value = Unit
        }
    }

    fun onUserSignOut() = viewModelScope.launch {
        signOutUseCase(Unit).collect {
            when (it) {
                is Resource.Success -> {
                    doOnSignOutUseCase(Unit)
                    cancelWorkManagerWorks()
                    _userSignedOut.value = Unit
                }
                is Resource.Error -> {
                    showToastMessage(it.message)
                }
                is Resource.Loading -> Unit
            }
        }
    }

    private fun cancelWorkManagerWorks() {
        workManager.cancelUniqueWork(UploadUserPhotoWork.WORK_NAME)
    }

    private fun buildSettingsListModels(userDetail: UserDetail? = null): List<SettingsListModel> {
        return buildList {
            if (userDetail != null) {
                // User is signed in.
                add(SettingsProfileModel(
                    signedIn = true,
                    username = userDetail.username,
                    photoUrl = userDetail.photoUrl
                ))

                add(SettingsSectionModel(
                    id = SETTINGS_FAVORITE,
                    icon = R.drawable.ic_loyalty,
                    title = context.getString(R.string.settings_section_favorite_title)
                ))
            } else {
                // User is signed out.
                add(SettingsProfileModel(signedIn = false))
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
            if (userDetail != null) {
                add(SettingsSectionModel(
                    id = SETTINGS_SIGN_OUT,
                    icon = R.drawable.ic_exit_to_app,
                    title = context.getString(R.string.settings_section_sign_out_title)
                ))
            }
        }
    }
 }

