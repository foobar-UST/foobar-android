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
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.auth.GetIsUserSignedInUseCase
import com.foobarust.domain.usecases.auth.SignOutUseCase
import com.foobarust.domain.usecases.user.GetAuthProfileObservableUseCase
import com.foobarust.domain.usecases.user.GetUserDetailObservableUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
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
    private val getIsUserSignedInUseCase: GetIsUserSignedInUseCase,
    private val getAuthProfileObservableUseCase: GetAuthProfileObservableUseCase,
    private val getUserDetailObservableUseCase: GetUserDetailObservableUseCase,
    private val signOutUseCase: SignOutUseCase
) : BaseViewModel() {

    private var subscribeUserDetailJob: Job? = null

    private val _navigateToSignIn = SingleLiveEvent<Unit>()
    val navigateToSignIn: LiveData<Unit>
        get() = _navigateToSignIn

    private val _navigateToProfile = SingleLiveEvent<Unit>()
    val navigateToProfile: LiveData<Unit>
        get() = _navigateToProfile

    private val _settingsItems = MutableLiveData<List<SettingsListModel>>()
    val settingsItems: LiveData<List<SettingsListModel>>
        get() = _settingsItems

    init {
        // Populate settings list
        viewModelScope.launch {
            getAuthProfileObservableUseCase(Unit).collect {
                when (it) {
                    is Resource.Success -> {
                        subscribeUserDetail()
                        buildSettingList(
                            SettingsProfile(username = it.data.username)
                        )
                    }
                    is Resource.Error -> {
                        unsubscribeUserDetail()
                        buildSettingList(SettingsProfile())
                    }
                    is Resource.Loading -> {
                        _settingsItems.value = emptyList()
                    }
                }
            }
        }
    }

    private fun subscribeUserDetail() {
        unsubscribeUserDetail()

        subscribeUserDetailJob = viewModelScope.launch {
            getUserDetailObservableUseCase(Unit).collect {
                when (it) {
                    is Resource.Success -> buildSettingList(
                        SettingsProfile(
                            username = it.data.username,
                            photoUrl = it.data.photoUrl
                        )
                    )
                    is Resource.Error -> showToastMessage(it.message)
                    is Resource.Loading -> Unit
                }
            }
        }
    }

    private fun unsubscribeUserDetail() {
        subscribeUserDetailJob?.cancel()
        subscribeUserDetailJob = null
    }

    fun onUserAccountCardClicked() = viewModelScope.launch {
        when (val result = getIsUserSignedInUseCase(Unit)) {
            is Resource.Success -> {
                val isSignedIn = result.data
                if (isSignedIn) _navigateToProfile.value = Unit else _navigateToSignIn.value = Unit
            }
            is Resource.Error -> showToastMessage(result.message)
            is Resource.Loading -> Unit
        }
    }

    fun signOut() = viewModelScope.launch {
        signOutUseCase(Unit)
    }

    private fun buildSettingList(settingsProfile: SettingsProfile) {
        _settingsItems.value = buildList {
            // Add auth item
            add(SettingsProfileModel(settingsProfile))

            // Add signed in items
            if (settingsProfile.isSignedIn()) {
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
            }

            // Add common items
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
            if (settingsProfile.isSignedIn()) {
                add(SettingsSectionModel(
                    id = SETTINGS_SIGN_OUT,
                    icon = R.drawable.ic_exit_to_app,
                    title = context.getString(R.string.settings_section_sign_out_title)
                ))
            }
        }
    }
}

data class SettingsProfile(
    val username: String? = null,
    val photoUrl: String? = null
) {
    fun isSignedIn(): Boolean = username != null

    fun hasPhoto(): Boolean = photoUrl != null
}
