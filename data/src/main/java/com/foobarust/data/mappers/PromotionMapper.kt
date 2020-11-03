package com.foobarust.data.mappers

import com.foobarust.data.models.AdvertiseBasicEntity
import com.foobarust.data.models.AdvertiseDetailEntity
import com.foobarust.data.models.SuggestBasicEntity
import com.foobarust.domain.models.AdvertiseBasic
import com.foobarust.domain.models.AdvertiseDetail
import com.foobarust.domain.models.SuggestBasic
import javax.inject.Inject

/**
 * Created by kevin on 10/3/20
 */

class PromotionMapper @Inject constructor() {

    fun toAdvertiseItem(advertiseBasicEntity: AdvertiseBasicEntity): AdvertiseBasic {
        return AdvertiseBasic(
            id = advertiseBasicEntity.id!!,
            imageUrl = advertiseBasicEntity.imageUrl
        )
    }

    fun toAdvertiseDetail(advertiseDetailEntity: AdvertiseDetailEntity): AdvertiseDetail {
        return AdvertiseDetail(
            id = advertiseDetailEntity.id!!,
            sellerId = advertiseDetailEntity.sellerId!!,
            sellerName = advertiseDetailEntity.sellerName!!,
            title = advertiseDetailEntity.title!!,
            content = advertiseDetailEntity.content!!,
            imageUrl = advertiseDetailEntity.imageUrl
        )
    }

    fun toSuggestItem(suggestBasicEntity: SuggestBasicEntity): SuggestBasic {
        return SuggestBasic(
            id = suggestBasicEntity.id!!,
            itemId = suggestBasicEntity.itemId!!,
            itemTitle = suggestBasicEntity.itemTitle!!,
            sellerName = suggestBasicEntity.sellerName!!,
            imageUrl = suggestBasicEntity.imageUrl
        )
    }
}