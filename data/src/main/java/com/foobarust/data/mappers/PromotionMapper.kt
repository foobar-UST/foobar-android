package com.foobarust.data.mappers

import com.foobarust.data.models.promotion.AdvertiseBasicDto
import com.foobarust.domain.models.promotion.AdvertiseBasic
import javax.inject.Inject

/**
 * Created by kevin on 10/3/20
 */

class PromotionMapper @Inject constructor() {

    fun toAdvertiseBasic(dto: AdvertiseBasicDto): AdvertiseBasic {
        return AdvertiseBasic(
            id = dto.id!!,
            url = dto.url!!,
            imageUrl = dto.imageUrl,
            createdAt = dto.createdAt!!.toDate()
        )
    }
}