package com.foobarust.data.repositories

import com.foobarust.data.common.Constants.ADVERTISES_BASIC_COLLECTION
import com.foobarust.data.mappers.PromotionMapper
import com.foobarust.data.utils.getAwaitResult
import com.foobarust.domain.models.promotion.AdvertiseBasic
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

    override suspend fun getAdvertiseBasics(): List<AdvertiseBasic> {
       return firestore.collection(ADVERTISES_BASIC_COLLECTION)
           .getAwaitResult(promotionMapper::toAdvertiseBasic)
    }

    /*
    override fun getSuggestsObservable(userId: String): Flow<Resource<List<SuggestBasic>>> {
        return firestore.collection(USERS_COLLECTION).document(userId)
            .collection(SUGGESTS_BASIC_COLLECTION)
            .snapshotFlow(promotionMapper::toSuggestBasic)
    }
     */
}