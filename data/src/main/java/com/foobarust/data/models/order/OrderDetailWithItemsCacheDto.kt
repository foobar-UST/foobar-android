package com.foobarust.data.models.order

import androidx.room.Embedded
import androidx.room.Relation
import com.foobarust.data.constants.Constants.ORDER_ID_FIELD
import com.foobarust.data.constants.Constants.ORDER_ITEM_ORDER_ID_FIELD

/**
 * Created by kevin on 2/28/21
 */

data class OrderDetailWithItemsCacheDto(
    @Embedded
    val orderDetailCacheDto: OrderDetailCacheDto,

    @Relation(
        parentColumn = ORDER_ID_FIELD,
        entityColumn = ORDER_ITEM_ORDER_ID_FIELD
    )
    val orderDetailItemsCacheDtos: List<OrderItemCacheDto>
)