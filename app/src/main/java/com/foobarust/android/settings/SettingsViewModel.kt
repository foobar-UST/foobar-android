package com.foobarust.android.settings

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.foobarust.android.R
import com.foobarust.android.settings.SettingsListModel.SettingsAccountItemModel
import com.foobarust.android.settings.SettingsListModel.SettingsSectionItemModel
import com.foobarust.android.works.UploadUserPhotoWork
import com.foobarust.domain.models.user.UserDetail
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.AuthState
import com.foobarust.domain.usecases.auth.SignOutUseCase
import com.foobarust.domain.usecases.user.DoOnSignOutUseCase
import com.foobarust.domain.usecases.user.GetUserAuthStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
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
    private val signOutUseCase: SignOutUseCase,
    private val doOnSignOutUseCase: DoOnSignOutUseCase,
    getUserAuthStateUseCase: GetUserAuthStateUseCase,
) : ViewModel() {

    private val _settingsListModels = MutableStateFlow<List<SettingsListModel>>(emptyList())
    val settingsListModels: LiveData<List<SettingsListModel>> = _settingsListModels
        .asLiveData(viewModelScope.coroutineContext)

    private val _settingsUiState = MutableStateFlow(SettingsUiState.LOADING)
    val settingsUiState: LiveData<SettingsUiState> = _settingsUiState
        .asLiveData(viewModelScope.coroutineContext)

    private val _navigateToSignIn = Channel<Unit>()
    val navigateToSignIn: Flow<Unit> = _navigateToSignIn.receiveAsFlow()

    private val _navigateToProfile = Channel<Unit>()
    val navigateToProfile: Flow<Unit> = _navigateToProfile.receiveAsFlow()

    private val _isUserSignedOut = Channel<Unit>()
    val isUserSignedOut: Flow<Unit> = _isUserSignedOut.receiveAsFlow()

    private val _toastMessage = Channel<String>()
    val toastMessage: Flow<String> = _toastMessage.receiveAsFlow()

    init {
        // Build settings list
        viewModelScope.launch {
            getUserAuthStateUseCase(Unit).collect {
                when (it) {
                    is AuthState.Authenticated -> {
                        _settingsListModels.value = buildAuthenticatedListModels(userDetail = it.data)
                        _settingsUiState.value = SettingsUiState.COMPLETED
                    }
                    AuthState.Unauthenticated -> {
                        _settingsListModels.value =  buildUnauthenticatedListModels()
                        _settingsUiState.value = SettingsUiState.COMPLETED
                    }
                    AuthState.Loading -> {
                        _settingsUiState.value = SettingsUiState.LOADING
                    }
                }
            }
        }
    }

    fun onNavigateToProfileOrSignIn(isSignedIn: Boolean) = viewModelScope.launch {
        if (isSignedIn) {
            _navigateToProfile.offer(Unit)
        } else {
            _navigateToSignIn.offer(Unit)
        }
    }

    fun onUserSignOut() = viewModelScope.launch {
        signOutUseCase(Unit).collect {
            when (it) {
                is Resource.Success -> {
                    doOnSignOutUseCase(Unit)
                    cancelWorkManagerWorks()
                    _isUserSignedOut.offer(Unit)
                }
                is Resource.Error -> {
                    it.message?.let {
                        _toastMessage.offer(it)
                    }
                }
                is Resource.Loading -> Unit
            }
        }
    }

    private fun cancelWorkManagerWorks() {
        workManager.cancelUniqueWork(UploadUserPhotoWork.WORK_NAME)
    }

    private fun buildAuthenticatedListModels(
        userDetail: UserDetail
    ): List<SettingsListModel> = buildList {
        add(SettingsAccountItemModel(
            signedIn = true,
            username = userDetail.username,
            photoUrl = userDetail.photoUrl
        ))

        add(SettingsSectionItemModel(
            id = SETTINGS_FAVORITE,
            drawableRes = R.drawable.ic_loyalty,
            title = context.getString(R.string.settings_section_favorite_title)
        ))

        addAll(buildCommonListModels())

        add(SettingsSectionItemModel(
            id = SETTINGS_SIGN_OUT,
            drawableRes = R.drawable.ic_exit_to_app,
            title = context.getString(R.string.settings_section_sign_out_title)
        ))
    }

    private fun buildUnauthenticatedListModels(): List<SettingsListModel> = buildList {
        add(SettingsAccountItemModel(signedIn = false))
        addAll(buildCommonListModels())
    }

    private fun buildCommonListModels(): List<SettingsListModel> = buildList {
        addAll(listOf(
            SettingsSectionItemModel(
                id = SETTINGS_NOTIFICATIONS,
                drawableRes = R.drawable.ic_notification_important,
                title = context.getString(R.string.settings_section_notifications_title)
            ),
            SettingsSectionItemModel(
                id = SETTINGS_FEATURES,
                drawableRes = R.drawable.ic_whatshot,
                title = context.getString(R.string.settings_section_features_title)
            ),
            SettingsSectionItemModel(
                id = SETTINGS_CONTACT_US,
                drawableRes = R.drawable.ic_live_help,
                title = context.getString(R.string.settings_section_contact_us_title)
            ),
            SettingsSectionItemModel(
                id = SETTINGS_TERMS_CONDITIONS,
                drawableRes = R.drawable.ic_copyright,
                title = context.getString(R.string.settings_section_license_title)
            )
        ))
    }
}

enum class SettingsUiState {
    COMPLETED,
    LOADING
}

