package com.foobarust.domain.repository

import com.foobarust.domain.models.promotion.AdvertiseBasic
import com.foobarust.domain.models.seller.SellerType
import com.foobarust.domain.repositories.PromotionRepository
import java.util.*

/**
 * Created by kevin on 4/9/21
 */

class FakePromotionRepositoryImpl : PromotionRepository {

    val advertiseBasicsList = listOf(
        AdvertiseBasic(
            id = UUID.randomUUID().toString(),
            imageUrl = "about:blank",
            url = "about:blank",
            createdAt = Date()
        ),
        AdvertiseBasic(
            id = UUID.randomUUID().toString(),
            imageUrl = "about:blank",
            url = "about:blank",
            createdAt = Date()
        ),
        AdvertiseBasic(
            id = UUID.randomUUID().toString(),
            imageUrl = "about:blank",
            url = "about:blank",
            createdAt = Date()
        ),
        AdvertiseBasic(
            id = UUID.randomUUID().toString(),
            imageUrl = "about:blank",
            url = "about:blank",
            createdAt = Date()
        ),
        AdvertiseBasic(
            id = UUID.randomUUID().toString(),
            imageUrl = "about:blank",
            url = "about:blank",
            createdAt = Date()
        ),
        AdvertiseBasic(
            id = UUID.randomUUID().toString(),
            imageUrl = "about:blank",
            url = "about:blank",
            createdAt = Date()
        )
    )

    private var shouldReturnNetworkError = false

    override suspend fun getAdvertiseBasics(
        sellerType: SellerType,
        numOfAdvertises: Int
    ): List<AdvertiseBasic> {
        if (shouldReturnNetworkError) throw Exception("Network error.")
        return advertiseBasicsList.take(numOfAdvertises)
    }

    fun setNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }
}