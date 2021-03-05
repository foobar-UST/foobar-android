package com.foobarust.data.models.order

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.foobarust.data.constants.Constants.ORDERS_BASIC_ENTITY
import com.foobarust.data.constants.Constants.ORDER_BASIC_DELIVERY_ADDRESS_FIELD
import com.foobarust.data.constants.Constants.ORDER_BASIC_DELIVERY_ADDRESS_ZH_FIELD
import com.foobarust.data.constants.Constants.ORDER_CREATED_AT_FIELD
import com.foobarust.data.constants.Constants.ORDER_IDENTIFIER_FIELD
import com.foobarust.data.constants.Constants.ORDER_ID_FIELD
import com.foobarust.data.constants.Constants.ORDER_IMAGE_URL_FIELD
import com.foobarust.data.constants.Constants.ORDER_ORDER_ITEMS_COUNT_FIELD
import com.foobarust.data.constants.Constants.ORDER_SECTION_ID_FIELD
import com.foobarust.data.constants.Constants.ORDER_SELLER_ID_FIELD
import com.foobarust.data.constants.Constants.ORDER_SELLER_NAME_FIELD
import com.foobarust.data.constants.Constants.ORDER_SELLER_NAME_ZH_FIELD
import com.foobarust.data.constants.Constants.ORDER_STATE_FIELD
import com.foobarust.data.constants.Constants.ORDER_TITLE_FIELD
import com.foobarust.data.constants.Constants.ORDER_TITLE_ZH_FIELD
import com.foobarust.data.constants.Constants.ORDER_TOTAL_COST_FIELD
import com.foobarust.data.constants.Constants.ORDER_TYPE_FIELD
import com.foobarust.data.constants.Constants.ORDER_UPDATED_AT_FIELD
import java.util.*

/**
 * Created by kevin on 2/28/21
 */

@Entity(tableName = ORDERS_BASIC_ENTITY)
data class OrderBasicCacheDto(
    @PrimaryKey
    @ColumnInfo(name = ORDER_ID_FIELD)
    val id: String,

    @ColumnInfo(name = ORDER_TITLE_FIELD)
    val title: String? = null,

    @ColumnInfo(name = ORDER_TITLE_ZH_FIELD)
    val titleZh: String? = null,

    @ColumnInfo(name = ORDER_SELLER_ID_FIELD)
    val sellerId: String? = null,

    @ColumnInfo(name = ORDER_SELLER_NAME_FIELD)
    val sellerName: String? = null,

    @ColumnInfo(name = ORDER_SELLER_NAME_ZH_FIELD)
    val sellerNameZh: String? = null,

    @ColumnInfo(name = ORDER_SECTION_ID_FIELD)
    val sectionId: String? = null,

    @ColumnInfo(name = ORDER_IDENTIFIER_FIELD)
    val identifier: String? = null,

    @ColumnInfo(name = ORDER_IMAGE_URL_FIELD)
    val imageUrl: String? = null,

    @ColumnInfo(name = ORDER_TYPE_FIELD)
    val type: Int? = null,

    @ColumnInfo(name = ORDER_ORDER_ITEMS_COUNT_FIELD)
    val orderItemsCount: Int? = null,

    @ColumnInfo(name = ORDER_STATE_FIELD)
    val state: String? = null,

    @ColumnInfo(name = ORDER_BASIC_DELIVERY_ADDRESS_FIELD)
    val deliveryAddress: String? = null,

    @ColumnInfo(name = ORDER_BASIC_DELIVERY_ADDRESS_ZH_FIELD)
    val deliveryAddressZh: String? = null,

    @ColumnInfo(name = ORDER_TOTAL_COST_FIELD)
    val totalCost: Double? = null,

    @ColumnInfo(name = ORDER_CREATED_AT_FIELD)
    val createdAt: Date? = null,

    @ColumnInfo(name = ORDER_UPDATED_AT_FIELD)
    val updatedAt: Date? = null
)