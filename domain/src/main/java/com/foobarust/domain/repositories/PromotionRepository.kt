package com.foobarust.domain.repositories

import com.foobarust.domain.models.AdvertiseBasic
import com.foobarust.domain.models.AdvertiseDetail
import com.foobarust.domain.models.SuggestBasic
import com.foobarust.domain.states.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Created by kevin on 10/3/20
 */

interface PromotionRepository {

    fun getAdvertiseItems(): Flow<Resource<List<AdvertiseBasic>>>

    fun getAdvertiseDetail(advertiseId: String): Flow<Resource<AdvertiseDetail>>

    fun getSuggestItems(userId: String): Flow<Resource<List<SuggestBasic>>>
}