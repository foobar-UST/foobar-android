package com.foobarust.data.mappers

import com.foobarust.data.models.promotion.AdvertiseBasicEntity
import com.foobarust.data.models.promotion.SuggestBasicEntity
import com.foobarust.domain.models.promotion.AdvertiseBasic
import com.foobarust.domain.models.promotion.SuggestBasic
import javax.inject.Inject

/**
 * Created by kevin on 10/3/20
 */

class PromotionMapper @Inject constructor() {

    fun toAdvertiseBasic(entity: AdvertiseBasicEntity): AdvertiseBasic {
        return AdvertiseBasic(
            id = entity.id!!,
            url = entity.url!!,
            imageUrl = entity.imageUrl
        )
    }

    fun toSuggestBasic(entity: SuggestBasicEntity): SuggestBasic {
        return SuggestBasic(
            id = entity.id!!,
            itemId = entity.itemId!!,
            itemTitle = entity.itemTitle!!,
            sellerName = entity.sellerName!!,
            imageUrl = entity.imageUrl
        )
    }
}