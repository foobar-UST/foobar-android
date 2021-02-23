package com.foobarust.android.main

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.foobarust.android.R
import com.foobarust.android.shared.BaseViewModel
import com.foobarust.android.utils.DynamicLinksUtils
import com.foobarust.android.works.UploadUserPhotoWork
import com.foobarust.domain.models.cart.UserCart
import com.foobarust.domain.models.cart.hasItems
import com.foobarust.domain.states.Resource
import com.foobarust.domain.states.getSuccessDataOr
import com.foobarust.domain.usecases.cart.CheckCartTimeOutUseCase
import com.foobarust.domain.usecases.cart.ClearUserCartUseCase
import com.foobarust.domain.usecases.cart.GetUserCartUseCase
import com.foobarust.domain.usecases.onboarding.GetUserCompleteTutorialUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by kevin on 9/20/20
 */

private const val TAG = "MainViewModel"

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val workManager: WorkManager,
    private val dynamicLinksUtils: DynamicLinksUtils,
    private val clearUserCartUseCase: ClearUserCartUseCase,
    private val checkCartTimeOutUseCase: CheckCartTimeOutUseCase,
    private val getUserCompleteTutorialUseCase: GetUserCompleteTutorialUseCase,
    getUserCartUseCase: GetUserCartUseCase,
) : BaseViewModel() {

    private val _currentNavGraphId = MutableStateFlow<Int?>(null)

    private val _userCart: StateFlow<UserCart?> = getUserCartUseCase(Unit)
        .map { it.getSuccessDataOr(null) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

    val userCart: LiveData<UserCart?> = _userCart.asLiveData(viewModelScope.coroutineContext)

    val showCartBottomBar: LiveData<Boolean> =
        _currentNavGraphId.combine(_userCart) { currentGraphId, userCart ->
            currentGraphId == R.id.navigation_seller && userCart?.hasItems() == true
        }
        .distinctUntilChanged()
        .asLiveData(viewModelScope.coroutineContext)

    // Scroll-to-top trigger to be consumed by top-level destinations
    private val _scrollToTop = MutableSharedFlow<Int>()
    val scrollToTop: SharedFlow<Int> = _scrollToTop.asSharedFlow()

    private val _snackBarMessage = Channel<String>()
    val snackBarMessage: Flow<String> = _snackBarMessage.receiveAsFlow()

    private val _deepLink = Channel<Uri>()
    val deepLink: Flow<Uri> = _deepLink.receiveAsFlow()

    private val _navigateToTutorial = Channel<Unit>()
    val navigateToTutorial: Flow<Unit> = _navigateToTutorial.receiveAsFlow()

    private val _navigateToCartTimeout = Channel<Int>()
    val navigateToCartTimeout: Flow<Int> = _navigateToCartTimeout.receiveAsFlow()

    private val _getUserPhoto = Channel<Unit>()
    val getUserPhoto: Flow<Unit> = _getUserPhoto.receiveAsFlow()

    private var checkedCartTimeout: Boolean = false
    private var currentDestinationId: Int = 0

    init {
        navigateToTutorial()
        navigateToCartTimeout()
    }

    fun getUserCart(): UserCart? = _userCart.value

    fun onCurrentDestinationChanged(graphId: Int, destinationId: Int) {
        _currentNavGraphId.value = graphId
        currentDestinationId = destinationId
    }

    fun onScrollToTop() = viewModelScope.launch {
        _scrollToTop.emit(currentDestinationId)
    }

    fun onClearUserCart() = viewModelScope.launch {
        clearUserCartUseCase(Unit).collect {
            when (it) {
                is Resource.Success -> {
                    onShowSnackBarMessage(
                        context.getString(R.string.cart_cleared_message)
                    )
                }
                is Resource.Error -> {
                    showToastMessage(it.message)
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun onShowSnackBarMessage(message: String) {
        _snackBarMessage.offer(message)
    }

    fun onDispatchDynamicLink(dynamicLink: Uri?) = viewModelScope.launch {
        dynamicLink?.let { link ->
            dynamicLinksUtils.extractDeepLink(link)?.let {
                _deepLink.offer(it)
            }
        }
    }

    fun onPickUserPhoto() {
        _getUserPhoto.offer(Unit)
    }

    fun onUploadUserPhoto(uri: String, extension: String) {
        val inputData = workDataOf(
            UploadUserPhotoWork.USER_PHOTO_URL to uri,
            UploadUserPhotoWork.USER_PHOTO_EXTENSION to extension
        )

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadRequest = OneTimeWorkRequestBuilder<UploadUserPhotoWork>()
            .setInputData(inputData)
            .setConstraints(constraints)
            .build()

        workManager.beginUniqueWork(
            UploadUserPhotoWork.WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            uploadRequest
        ).enqueue()
    }

    private fun navigateToTutorial() = viewModelScope.launch {
        val completed = getUserCompleteTutorialUseCase(Unit).getSuccessDataOr(false)
        if (!completed) {
            _navigateToTutorial.offer(Unit)
        }
    }

    private fun navigateToCartTimeout() = viewModelScope.launch {
        if (!checkedCartTimeout) {
            val userCart = _userCart.filterNotNull().first()
            if (checkCartTimeOutUseCase(userCart).getSuccessDataOr(false)) {
                _navigateToCartTimeout.offer(userCart.itemsCount)
            }
            checkedCartTimeout = true
        }
    }
}