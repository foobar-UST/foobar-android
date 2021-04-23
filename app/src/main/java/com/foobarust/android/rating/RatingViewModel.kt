package com.foobarust.android.rating

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foobarust.domain.models.order.OrderDetail
import com.foobarust.domain.models.order.OrderRating
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.order.GetOrderDetailUseCase
import com.foobarust.domain.usecases.order.SubmitOrderRatingUseCase
import com.foobarust.domain.utils.cancelIfActive
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val getOrderDetailUseCase: GetOrderDetailUseCase,
    private val submitOrderRatingUseCase: SubmitOrderRatingUseCase
) : ViewModel() {

    private var _orderDetail = MutableStateFlow<OrderDetail?>(null)
    val orderDetail: StateFlow<OrderDetail?> = _orderDetail.asStateFlow()

    private val _ratingUiLoadState = MutableStateFlow<RatingUiState>(RatingUiState.Loading)
    val ratingUiLoadState: StateFlow<RatingUiState> = _ratingUiLoadState.asStateFlow()

    private val _ratingUiSubmitState = MutableStateFlow<RatingUiState?>(null)
    val ratingUiSubmitState: SharedFlow<RatingUiState?> = _ratingUiSubmitState.asSharedFlow()

    private val _ratingCompleted = Channel<Unit>()
    val ratingCompleted: Flow<Unit> = _ratingCompleted.receiveAsFlow()

    private val _currentDestination = MutableStateFlow(-1)

    private var fetchOrderDetailJob: Job? = null

    var orderRatingInput: Int? = null
    var deliveryRatingInput: Boolean? = null
    var commentInput: String? = null

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

    fun onUpdateOrderRating(rating: Int) {
        orderRatingInput = rating
    }

    fun onUpdateDeliveryRating(rating: Boolean) {
        deliveryRatingInput = rating
    }

    fun onUpdateComment(comment: String?) {
        commentInput = comment
    }

    fun onSubmitRating() = viewModelScope.launch {
        val orderDetail = _orderDetail.value ?: return@launch
        orderRatingInput?.let { rating ->
            val orderRating = OrderRating(
                orderId = orderDetail.id,
                orderRating = rating,
                deliveryRating = deliveryRatingInput,
                comment = commentInput
            )

            submitOrderRatingUseCase(orderRating).collect {
                _ratingUiSubmitState.value = when (it) {
                    is Resource.Success -> RatingUiState.Success
                    is Resource.Error -> RatingUiState.Error(it.message)
                    is Resource.Loading -> RatingUiState.Loading
                }
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