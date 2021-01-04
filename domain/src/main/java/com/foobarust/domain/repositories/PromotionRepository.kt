package com.foobarust.domain.repositories

import com.foobarust.domain.models.promotion.AdvertiseBasic

/**
 * Created by kevin on 10/3/20
 */

interface PromotionRepository {

    suspend fun getAdvertiseBasics(): List<AdvertiseBasic>

    //fun getSuggestsObservable(userId: String): Flow<Resource<List<SuggestBasic>>>
}