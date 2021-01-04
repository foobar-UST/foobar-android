package com.foobarust.android.settings

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.settings.SettingsListModel.SettingsProfileModel
import com.foobarust.android.settings.SettingsListModel.SettingsSectionModel
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.domain.models.user.UserDetail
import com.foobarust.domain.models.user.isSignedIn
import com.foobarust.domain.usecases.auth.SignOutUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch

const val SETTINGS_NOTIFICATIONS = "settings_notifications"
const val SETTINGS_CONTACT_US = "settings_contact_us"
const val SETTINGS_TERMS_CONDITIONS = "settings_terms_conditions"
const val SETTINGS_FEATURES = "settings_features"
const val SETTINGS_SIGN_OUT = "settings_sign_out"
const val SETTINGS_FAVORITE = "setting_favorite"
const val SETTINGS_ORDER_HISTORY = "settings_order_history"

class SettingsViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val signOutUseCase: SignOutUseCase
) : BaseViewModel() {

    private val _navigateToSignIn = SingleLiveEvent<Unit>()
    val navigateToSignIn: LiveData<Unit>
        get() = _navigateToSignIn

    private val _navigateToProfile = SingleLiveEvent<Unit>()
    val navigateToProfile: LiveData<Unit>
        get() = _navigateToProfile

    private val _settingsListModels = MutableLiveData<List<SettingsListModel>>()
    val settingsListModels: LiveData<List<SettingsListModel>>
        get() = _settingsListModels

    private var userDetail: UserDetail? = null

    fun onUserDetailUpdated(userDetail: UserDetail?) {
        this.userDetail = userDetail
        _settingsListModels.value = buildList {
            if (userDetail != null) {
                // Setup signed in sections
                add(SettingsProfileModel(
                    username = userDetail.username,
                    photoUrl = userDetail.photoUrl
                ))

                addAll(listOf(
                    SettingsSectionModel(
                        id = SETTINGS_FAVORITE,
                        icon = R.drawable.ic_loyalty,
                        title = context.getString(R.string.settings_section_favorite_title)
                    ),
                    SettingsSectionModel(
                        id = SETTINGS_ORDER_HISTORY,
                        icon = R.drawable.ic_fastfood,
                        title = context.getString(R.string.settings_section_orders_title)
                    )
                ))
            } else {
                // Setup signed out sections
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
            if (userDetail != null && userDetail.isSignedIn()) {
                add(SettingsSectionModel(
                    id = SETTINGS_SIGN_OUT,
                    icon = R.drawable.ic_exit_to_app,
                    title = context.getString(R.string.settings_section_sign_out_title)
                ))
            }
        }
    }

    fun onUserAccountCardClicked() = viewModelScope.launch {
        if (userDetail?.isSignedIn() == true) {
            _navigateToProfile.value = Unit
        } else {
            _navigateToSignIn.value = Unit
        }
    }

    fun signOut() = viewModelScope.launch {
        signOutUseCase(Unit)
    }
}

