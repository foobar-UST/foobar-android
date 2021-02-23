package com.foobarust.data.repositories

import com.foobarust.data.constants.Constants.ADVERTISES_BASIC_COLLECTION
import com.foobarust.data.constants.Constants.ADVERTISE_RANDOM_FIELD
import com.foobarust.data.constants.Constants.ADVERTISE_SELLER_TYPE_FIELD
import com.foobarust.data.mappers.PromotionMapper
import com.foobarust.data.utils.getAwaitResult
import com.foobarust.domain.models.promotion.AdvertiseBasic
import com.foobarust.domain.models.seller.SellerType
import com.foobarust.domain.repositories.PromotionRepository
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

/**
 * Created by kevin on 10/3/20
 */

class PromotionRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val promotionMapper: PromotionMapper
) : PromotionRepository {

    override suspend fun getAdvertiseBasics(sellerType: SellerType, numOfAdvertises: Int): List<AdvertiseBasic> {
        return firestore.collection(ADVERTISES_BASIC_COLLECTION)
            .whereEqualTo(ADVERTISE_SELLER_TYPE_FIELD, sellerType.ordinal)
            .orderBy(ADVERTISE_RANDOM_FIELD)
            .limit(numOfAdvertises.toLong())
            .getAwaitResult(promotionMapper::toAdvertiseBasic)
    }
}