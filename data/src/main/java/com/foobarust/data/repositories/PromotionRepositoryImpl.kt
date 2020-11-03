package com.foobarust.data.repositories

import com.foobarust.data.common.Constants.ADVERTISES_BASIC_COLLECTION
import com.foobarust.data.common.Constants.SUGGESTS_BASIC_COLLECTION
import com.foobarust.data.common.Constants.USERS_COLLECTION
import com.foobarust.data.mappers.PromotionMapper
import com.foobarust.data.utils.snapshotFlow
import com.foobarust.domain.models.AdvertiseBasic
import com.foobarust.domain.models.SuggestBasic
import com.foobarust.domain.repositories.PromotionRepository
import com.foobarust.domain.states.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by kevin on 10/3/20
 */

class PromotionRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val promotionMapper: PromotionMapper
) : PromotionRepository {

    override fun getAdvertiseBasicsObservable(): Flow<Resource<List<AdvertiseBasic>>> {
       return firestore.collection(ADVERTISES_BASIC_COLLECTION)
           .snapshotFlow(promotionMapper::toAdvertiseItem)
    }

    override fun getSuggestBasicsObservable(userId: String): Flow<Resource<List<SuggestBasic>>> {
        return firestore.collection(USERS_COLLECTION).document(userId)
            .collection(SUGGESTS_BASIC_COLLECTION)
            .snapshotFlow(promotionMapper::toSuggestItem)
    }
}