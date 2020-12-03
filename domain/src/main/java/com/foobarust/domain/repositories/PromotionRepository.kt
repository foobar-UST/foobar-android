package com.foobarust.domain.repositories

import com.foobarust.domain.models.promotion.AdvertiseBasic
import com.foobarust.domain.models.promotion.SuggestBasic
import com.foobarust.domain.states.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Created by kevin on 10/3/20
 */

interface PromotionRepository {

    fun getAdvertisesObservable(): Flow<Resource<List<AdvertiseBasic>>>

    fun getSuggestsObservable(userId: String): Flow<Resource<List<SuggestBasic>>>
}