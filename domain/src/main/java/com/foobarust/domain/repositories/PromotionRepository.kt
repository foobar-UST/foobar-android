package com.foobarust.domain.repositories

import com.foobarust.domain.models.promotion.AdvertiseBasic
import com.foobarust.domain.models.seller.SellerType

/**
 * Created by kevin on 10/3/20
 */

interface PromotionRepository {

    suspend fun getAdvertiseBasics(sellerType: SellerType, numOfAdvertises: Int): List<AdvertiseBasic>
}