package com.foobarust.android.settings

import android.content.Context
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.settings.SettingsListModel.SettingsProfileModel
import com.foobarust.android.settings.SettingsListModel.SettingsSectionModel
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.domain.models.UserDetail
import com.foobarust.domain.models.asUserDetail
import com.foobarust.domain.states.Resource.*
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
                    is Success -> {
                        subscribeUserDetail()
                        _settingsItems.value = buildSettingList(it.data.asUserDetail())
                    }
                    is Error -> {
                        unsubscribeUserDetail()
                        _settingsItems.value = buildSettingList()
                    }
                    is Loading -> {
                        _settingsItems.value = emptyList()
                    }
                }
            }
        }
    }

    private fun subscribeUserDetail() {
        unsubscribeUserDetail()
        subscribeUserDetailJob = viewModelScope.launch {
            Log.d("SettingsViewModel", "subscribeUserDetail")
            getUserDetailObservableUseCase(Unit).collect {
                when (it) {
                    is Success -> {
                        _settingsItems.value = buildSettingList(it.data)
                    }
                    is Error -> showMessage(it.message)
                }
            }
        }
    }

    private fun unsubscribeUserDetail() {
        Log.d("SettingsViewModel", "unsubscribeUserDetail")
        subscribeUserDetailJob?.cancel()
        subscribeUserDetailJob = null
    }

    fun onUserAccountCardClicked() = viewModelScope.launch {
        when (val result = getIsUserSignedInUseCase(Unit)) {
            is Success -> {
                val isSignedIn = result.data
                if (isSignedIn) _navigateToProfile.value = Unit else _navigateToSignIn.value = Unit
            }
            is Error -> showMessage(result.message)
        }
    }

    fun signOut() = viewModelScope.launch {
        signOutUseCase(Unit)
    }

    private fun buildSettingList(userDetail: UserDetail? = null): List<SettingsListModel> {
        val authItem = SettingsProfileModel(userDetail = userDetail)
        val signOutItem = SettingsSectionModel(
            id = SETTINGS_SIGN_OUT,
            icon = R.drawable.ic_exit_to_app,
            title = context.getString(R.string.settings_section_sign_out_title)
        )
        val signedInItems = listOf(
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
        )
        val commonItems = listOf(
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
        )

        return buildList {
            add(authItem)
            userDetail?.let { addAll(signedInItems) }
            addAll(commonItems)
            userDetail?.let { add(signOutItem) }
        }
    }
}
