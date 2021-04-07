package com.foobarust.android.sellerrating

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.foobarust.android.sellerrating.SellerRatingDetailListModel.*
import com.foobarust.domain.models.seller.SellerRatingCount
import com.foobarust.domain.models.seller.SellerRatingSortOption
import com.foobarust.domain.usecases.seller.GetSellerRatingsPagingParameter
import com.foobarust.domain.usecases.seller.GetSellerRatingsPagingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import javax.inject.Inject

/**
 * Created by kevin on 3/3/21
 */

@HiltViewModel
class SellerRatingDetailViewModel @Inject constructor(
    private val getSellerRatingsPagingUseCase: GetSellerRatingsPagingUseCase
) : ViewModel() {

    private val _ratingDetailProperty = ConflatedBroadcastChannel<SellerRatingDetailProperty>()

    private val _ratingSortOption = MutableStateFlow(SellerRatingSortOption.LATEST)
    val ratingSortOption: StateFlow<SellerRatingSortOption> = _ratingSortOption.asStateFlow()

    val ratingDetailListModels: Flow<PagingData<SellerRatingDetailListModel>> = _ratingDetailProperty
        .asFlow()
        .combine(_ratingSortOption) { property, sortOption ->
            GetSellerRatingsPagingParameter(
                sellerId = property.sellerId,
                sortOption = sortOption
            )
        }
        .flatMapLatest {
            getSellerRatingsPagingUseCase(it)
        }
        .map { pagingData ->
            pagingData.map { sellerRatingBasic ->
                SellerRatingDetailRatingItem(
                    ratingId = sellerRatingBasic.id,
                    username = sellerRatingBasic.username,
                    userPhotoUrl = sellerRatingBasic.userPhotoUrl,
                    orderRating = sellerRatingBasic.orderRating,
                    createdAt = sellerRatingBasic.createdAt
                )
            }
        }
        .map { pagingData ->
            pagingData.insertSeparators { before, after ->
                insertSeparators(before, after)
            }
        }
        .cachedIn(viewModelScope)

    fun onFetchSellerRatings(property: SellerRatingDetailProperty) {
        _ratingDetailProperty.offer(property)
    }

    fun onUpdateSortOption(sortOption: SellerRatingSortOption) {
        _ratingSortOption.value = sortOption
    }

    private fun insertSeparators(
        before: SellerRatingDetailListModel?,
        after: SellerRatingDetailListModel?
    ): SellerRatingDetailListModel? {
        return when {
            before == null && after == null -> SellerRatingDetailEmptyItem
            before == null -> SellerRatingDetailInfoItem(
                orderRating = _ratingDetailProperty.value.orderRating,
                deliveryRating = _ratingDetailProperty.value.deliveryRating,
                ratingCount = _ratingDetailProperty.value.ratingCount,
                ratingSortOption = _ratingSortOption.value
            )
            else -> null
        }
    }
}

@Parcelize
data class SellerRatingDetailProperty(
    val sellerId: String,
    val sellerName: String,
    val orderRating: Double,
    val deliveryRating: Double?,
    val ratingCount: @RawValue SellerRatingCount
) : Parcelable