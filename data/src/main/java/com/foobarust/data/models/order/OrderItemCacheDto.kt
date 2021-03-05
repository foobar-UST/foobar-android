package com.foobarust.data.models.order

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.foobarust.data.constants.Constants.ORDER_ITEMS_ENTITY
import com.foobarust.data.constants.Constants.ORDER_ITEM_AMOUNTS_FIELD
import com.foobarust.data.constants.Constants.ORDER_ITEM_ID_FIELD
import com.foobarust.data.constants.Constants.ORDER_ITEM_ITEM_ID_FIELD
import com.foobarust.data.constants.Constants.ORDER_ITEM_ITEM_IMAGE_URL_FIELD
import com.foobarust.data.constants.Constants.ORDER_ITEM_ITEM_PRICE_FIELD
import com.foobarust.data.constants.Constants.ORDER_ITEM_ITEM_SELLER_ID_FIELD
import com.foobarust.data.constants.Constants.ORDER_ITEM_ITEM_TITLE_FIELD
import com.foobarust.data.constants.Constants.ORDER_ITEM_ITEM_TITLE_ZH_FIELD
import com.foobarust.data.constants.Constants.ORDER_ITEM_ORDER_ID_FIELD
import com.foobarust.data.constants.Constants.ORDER_ITEM_TOTAL_PRICE_FIELD

/**
 * Created by kevin on 1/28/21
 */

@Entity(tableName = ORDER_ITEMS_ENTITY)
data class OrderItemCacheDto(
    @PrimaryKey
    @ColumnInfo(name = ORDER_ITEM_ID_FIELD)
    val id: String,

    @ColumnInfo(name = ORDER_ITEM_ORDER_ID_FIELD)
    val orderId: String? = null,

    @ColumnInfo(name = ORDER_ITEM_ITEM_ID_FIELD)
    val itemId: String? = null,

    @ColumnInfo(name = ORDER_ITEM_ITEM_SELLER_ID_FIELD)
    val itemSellerId: String? = null,

    @ColumnInfo(name = ORDER_ITEM_ITEM_TITLE_FIELD)
    val itemTitle: String? = null,

    @ColumnInfo(name = ORDER_ITEM_ITEM_TITLE_ZH_FIELD)
    val itemTitleZh: String? = null,

    @ColumnInfo(name = ORDER_ITEM_ITEM_PRICE_FIELD)
    val itemPrice: Double? = null,

    @ColumnInfo(name = ORDER_ITEM_ITEM_IMAGE_URL_FIELD)
    val itemImageUrl: String? = null,

    @ColumnInfo(name = ORDER_ITEM_AMOUNTS_FIELD)
    val amounts: Int? = null,

    @ColumnInfo(name = ORDER_ITEM_TOTAL_PRICE_FIELD)
    val totalPrice: Double? = null
)