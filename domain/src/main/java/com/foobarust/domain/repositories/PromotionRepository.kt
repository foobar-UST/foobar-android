package com.foobarust.domain.repositories

import com.foobarust.domain.models.AdvertiseBasic
import com.foobarust.domain.models.SuggestBasic
import com.foobarust.domain.states.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Created by kevin on 10/3/20
 */

interface PromotionRepository {

    fun getAdvertiseBasicsObservable(): Flow<Resource<List<AdvertiseBasic>>>

    fun getSuggestBasicsObservable(userId: String): Flow<Resource<List<SuggestBasic>>>
}