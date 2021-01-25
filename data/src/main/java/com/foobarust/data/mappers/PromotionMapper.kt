package com.foobarust.data.mappers

import com.foobarust.data.models.promotion.AdvertiseBasicDto
import com.foobarust.data.models.promotion.SuggestBasicDto
import com.foobarust.domain.models.promotion.AdvertiseBasic
import com.foobarust.domain.models.promotion.SuggestBasic
import javax.inject.Inject

/**
 * Created by kevin on 10/3/20
 */

class PromotionMapper @Inject constructor() {

    fun toAdvertiseBasic(dto: AdvertiseBasicDto): AdvertiseBasic {
        return AdvertiseBasic(
            id = dto.id!!,
            url = dto.url!!,
            imageUrl = dto.imageUrl
        )
    }

    fun toSuggestBasic(dto: SuggestBasicDto): SuggestBasic {
        return SuggestBasic(
            id = dto.id!!,
            itemId = dto.itemId!!,
            itemTitle = dto.itemTitle!!,
            sellerName = dto.sellerName!!,
            imageUrl = dto.imageUrl
        )
    }
}