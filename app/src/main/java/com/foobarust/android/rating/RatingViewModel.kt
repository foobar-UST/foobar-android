package com.foobarust.android.rating

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.domain.models.order.OrderDetail
import com.foobarust.domain.models.order.OrderRating
import com.foobarust.domain.models.order.OrderType
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.order.GetOrderDetailUseCase
import com.foobarust.domain.usecases.order.SubmitOrderRatingUseCase
import com.foobarust.domain.utils.cancelIfActive
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by kevin on 2/24/21
 */

@HiltViewModel
class RatingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getOrderDetailUseCase: GetOrderDetailUseCase,
    private val submitOrderRatingUseCase: SubmitOrderRatingUseCase
) : ViewModel() {

    private val _orderDetail = MutableStateFlow<OrderDetail?>(null)
    val orderDetail: Flow<OrderDetail> = _orderDetail.filterNotNull()

    private val _ratingUiLoadState = MutableStateFlow<RatingUiState>(RatingUiState.Loading)
    val ratingUiLoadState: StateFlow<RatingUiState> = _ratingUiLoadState.asStateFlow()

    private val _ratingUiSubmitState = MutableStateFlow<RatingUiState?>(null)
    val ratingUiSubmitState: SharedFlow<RatingUiState?> = _ratingUiSubmitState.asSharedFlow()

    private val _orderRating = MutableStateFlow<Int?>(null)

    private val _deliveryRating = MutableStateFlow<Boolean?>(null)

    private val _ratingCompleted = Channel<Unit>()
    val ratingCompleted: Flow<Unit> = _ratingCompleted.receiveAsFlow()

    private val _currentDestination = MutableStateFlow(-1)

    private var fetchOrderDetailJob: Job? = null

    val toolbarTitle: StateFlow<String?> = _orderDetail
        .filterNotNull()
        .map {
            context.getString(R.string.rating_toolbar_title, it.identifier)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

    fun onFetchOrderDetail(orderId: String) {
        fetchOrderDetailJob?.cancelIfActive()
        fetchOrderDetailJob = viewModelScope.launch {
            getOrderDetailUseCase(orderId).collect {
                when (it) {
                    is Resource.Success -> {
                        _orderDetail.value = it.data
                        _ratingUiLoadState.value = RatingUiState.Success
                    }
                    is Resource.Error -> {
                        _ratingUiLoadState.value = RatingUiState.Error(it.message)
                    }
                    is Resource.Loading -> {
                        _ratingUiLoadState.value = RatingUiState.Loading
                    }
                }
            }
        }
    }

    fun getOrderRating(): Int? = _orderRating.value

    fun shouldRateDelivery(): Boolean = _orderDetail.value?.type == OrderType.OFF_CAMPUS

    fun onUpdateOrderRating(rating: Int) {
        _orderRating.value = rating
    }

    fun onUpdateDeliveryRating(rating: Boolean) {
        _deliveryRating.value = rating
    }

    fun onSubmitRating() = viewModelScope.launch {
        val orderId = _orderDetail.value?.id ?: return@launch
        val orderRating = _orderRating.value ?: return@launch

        submitOrderRatingUseCase(
            OrderRating(
                orderId = orderId,
                orderRating = orderRating,
                deliveryRating = _deliveryRating.value
            )
        ).collect {
            _ratingUiSubmitState.value = when (it) {
                is Resource.Success -> RatingUiState.Success
                is Resource.Error -> RatingUiState.Error(it.message)
                is Resource.Loading -> RatingUiState.Loading
            }
        }
    }

    fun onUpdateCurrentDestination(destinationId: Int) {
        _currentDestination.value = destinationId
    }

    fun onCompleteRating() {
        _ratingCompleted.offer(Unit)
    }
}

sealed class RatingUiState {
    object Success : RatingUiState()
    data class Error(val message: String?) : RatingUiState()
    object Loading : RatingUiState()
}