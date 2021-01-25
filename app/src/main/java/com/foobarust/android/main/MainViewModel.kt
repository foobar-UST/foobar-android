package com.foobarust.android.main

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.foobarust.android.R
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.android.works.UploadUserPhotoWorker
import com.foobarust.domain.models.cart.UserCart
import com.foobarust.domain.states.Resource
import com.foobarust.domain.states.getSuccessDataOr
import com.foobarust.domain.usecases.cart.CheckCartTimeOutUseCase
import com.foobarust.domain.usecases.cart.ClearUserCartUseCase
import com.foobarust.domain.usecases.cart.GetUserCartUseCase
import com.foobarust.domain.usecases.onboarding.GetOnboardingCompletedUseCase
import com.foobarust.domain.usecases.onboarding.UpdateOnboardingCompletedUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Created by kevin on 9/20/20
 */

class MainViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val workManager: WorkManager,
    private val getUserCartUseCase: GetUserCartUseCase,
    private val checkCartTimeOutUseCase: CheckCartTimeOutUseCase,
    private val clearUserCartUseCase: ClearUserCartUseCase,
    private val getOnboardingCompletedUseCase: GetOnboardingCompletedUseCase,
    private val updateOnboardingCompletedUseCase: UpdateOnboardingCompletedUseCase,
) : BaseViewModel() {

    val userCartLiveData: LiveData<UserCart?> = getUserCartUseCase(Unit)
        .map { it.getSuccessDataOr(null) }
        .asLiveData(viewModelScope.coroutineContext)

    val topLevelDestinations = listOf(
        R.id.sellerFragment,
        R.id.orderFragment,
        R.id.exploreFragment,
        R.id.settingsFragment
    )

    private val _currentNavGraphId = MutableStateFlow<Int?>(null)

    val showCartBottomBar: LiveData<Boolean> = _currentNavGraphId
        .combine(
            getUserCartUseCase(Unit).map { it.getSuccessDataOr(null) }
        ) { currentGraphId, userCart ->
            // Show bottom bar only in seller tab
            currentGraphId == R.id.navigation_seller &&
                userCart != null &&
                userCart.itemsCount > 0
        }
        .distinctUntilChanged()
        .asLiveData(viewModelScope.coroutineContext)

    // Scroll-to-top trigger to be consumed by top-level destinations
    private val _scrollToTop = MutableSharedFlow<Int>()
    val scrollToTop: SharedFlow<Int> = _scrollToTop.asSharedFlow()

    private val _showSnackBarMessage = SingleLiveEvent<String>()
    val showSnackBarMessage: LiveData<String>
        get() = _showSnackBarMessage

    private val _navigateToTimeoutDialog = SingleLiveEvent<CartTimeoutProperty>()
    val navigateToTimeoutDialog: LiveData<CartTimeoutProperty>
        get() = _navigateToTimeoutDialog

    private val _navigateToOnboardingTutorial = SingleLiveEvent<Unit>()
    val navigateToOnboardingTutorial: LiveData<Unit>
        get() = _navigateToOnboardingTutorial

    private val _launchCustomTab = SingleLiveEvent<String>()
    val launchCustomTab: LiveData<String>
        get() = _launchCustomTab

    private var hasCheckedCartTimeout: Boolean = false
    private var currentDestinationId: Int = 0

    private var currentUserCart: UserCart? = null

    init {
        showOnboardingTutorial()
        fetchUserCart()
    }

    private fun fetchUserCart() = viewModelScope.launch {
        getUserCartUseCase(Unit).collect {
            when (it) {
                is Resource.Success -> {
                    val userCart = it.data
                    currentUserCart = userCart
                    if (!hasCheckedCartTimeout && userCart != null) {
                        checkUserCartTimeout(userCart = userCart)
                    }
                }
                is Resource.Error -> {
                    currentUserCart = null
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun onCurrentNavGraphChanged(graphId: Int) {
        _currentNavGraphId.value = graphId
    }

    fun onCurrentDestinationChanged(destinationId: Int) {
        currentDestinationId = destinationId
    }

    fun onScrollToTop() = viewModelScope.launch {
        _scrollToTop.emit(currentDestinationId)
    }

    fun onUploadUserPhoto(uri: String, extension: String) {
        val inputData = workDataOf(
            UploadUserPhotoWorker.USER_PHOTO_URL to uri,
            UploadUserPhotoWorker.USER_PHOTO_EXTENSION to extension
        )
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadRequest = OneTimeWorkRequestBuilder<UploadUserPhotoWorker>()
            .setInputData(inputData)
            .setConstraints(constraints)
            .build()

        workManager.beginUniqueWork(
            UploadUserPhotoWorker.WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            uploadRequest
        ).enqueue()
    }

    fun onClearUsersCart() = viewModelScope.launch {
        clearUserCartUseCase(Unit).collect {
            when (it) {
                is Resource.Success -> _showSnackBarMessage.value = context.getString(
                        R.string.cart_cleared_message
                    )
                is Resource.Error -> showToastMessage(it.message)
                is Resource.Loading -> Unit
            }
        }
    }

    fun onTutorialDismissed() = viewModelScope.launch {
        val onboardingCompleted = getOnboardingCompletedUseCase(Unit).getSuccessDataOr(false)
        if (!onboardingCompleted) {
            updateOnboardingCompletedUseCase(true)
        }
    }

    fun onLaunchCustomTab(url: String) {
        _launchCustomTab.value = url
    }

    fun getCurrentUserCart(): UserCart? = currentUserCart

    private fun showOnboardingTutorial() = viewModelScope.launch {
        val completed = getOnboardingCompletedUseCase(Unit).getSuccessDataOr(false)
        if (!completed) {
            _navigateToOnboardingTutorial.value = Unit
        }
    }

    private suspend fun checkUserCartTimeout(userCart: UserCart) {
        val isTimeout = checkCartTimeOutUseCase(userCart).getSuccessDataOr(false)
        if (isTimeout) {
            _navigateToTimeoutDialog.value = CartTimeoutProperty(
                cartItemsCount = userCart.itemsCount
            )
        }
        hasCheckedCartTimeout = true
    }
}