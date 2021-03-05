package com.foobarust.data.models.order

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.foobarust.data.constants.Constants.GEO_LOCATION_ADDRESS_FIELD
import com.foobarust.data.constants.Constants.GEO_LOCATION_ADDRESS_ZH_FIELD
import com.foobarust.data.constants.Constants.ORDERS_ENTITY
import com.foobarust.data.constants.Constants.ORDER_CREATED_AT_FIELD
import com.foobarust.data.constants.Constants.ORDER_DELIVERER_ID_FIELD
import com.foobarust.data.constants.Constants.ORDER_DELIVERY_COST_FIELD
import com.foobarust.data.constants.Constants.ORDER_DELIVERY_LOCATION_LATITUDE_FIELD
import com.foobarust.data.constants.Constants.ORDER_DELIVERY_LOCATION_LONGITUDE_FIELD
import com.foobarust.data.constants.Constants.ORDER_IDENTIFIER_FIELD
import com.foobarust.data.constants.Constants.ORDER_ID_FIELD
import com.foobarust.data.constants.Constants.ORDER_IMAGE_URL_FIELD
import com.foobarust.data.constants.Constants.ORDER_IS_PAID_FIELD
import com.foobarust.data.constants.Constants.ORDER_MESSAGE_FIELD
import com.foobarust.data.constants.Constants.ORDER_ORDER_ITEMS_COUNT_FIELD
import com.foobarust.data.constants.Constants.ORDER_PAYMENT_METHOD_FIELD
import com.foobarust.data.constants.Constants.ORDER_SECTION_ID_FIELD
import com.foobarust.data.constants.Constants.ORDER_SECTION_TITLE_FIELD
import com.foobarust.data.constants.Constants.ORDER_SECTION_TITLE_ZH_FIELD
import com.foobarust.data.constants.Constants.ORDER_SELLER_ID_FIELD
import com.foobarust.data.constants.Constants.ORDER_SELLER_NAME_FIELD
import com.foobarust.data.constants.Constants.ORDER_SELLER_NAME_ZH_FIELD
import com.foobarust.data.constants.Constants.ORDER_STATE_FIELD
import com.foobarust.data.constants.Constants.ORDER_SUBTOTAL_COST_FIELD
import com.foobarust.data.constants.Constants.ORDER_TITLE_FIELD
import com.foobarust.data.constants.Constants.ORDER_TITLE_ZH_FIELD
import com.foobarust.data.constants.Constants.ORDER_TOTAL_COST_FIELD
import com.foobarust.data.constants.Constants.ORDER_TYPE_FIELD
import com.foobarust.data.constants.Constants.ORDER_UPDATED_AT_FIELD
import java.util.*

/**
 * Created by kevin on 1/28/21
 */

@Entity(tableName = ORDERS_ENTITY)
data class OrderDetailCacheDto(
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

    @ColumnInfo(name = ORDER_SECTION_TITLE_FIELD)
    val sectionTitle: String? = null,

    @ColumnInfo(name = ORDER_SECTION_TITLE_ZH_FIELD)
    val sectionTitleZh: String? = null,

    @ColumnInfo(name = ORDER_DELIVERER_ID_FIELD)
    val delivererId: String? = null,

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

    @ColumnInfo(name = ORDER_IS_PAID_FIELD)
    val isPaid: Boolean? = null,

    @ColumnInfo(name = ORDER_PAYMENT_METHOD_FIELD)
    val paymentMethod: String? = null,

    @ColumnInfo(name = ORDER_MESSAGE_FIELD)
    val message: String? = null,

    @ColumnInfo(name = GEO_LOCATION_ADDRESS_FIELD)
    val deliveryLocationAddress: String? = null,

    @ColumnInfo(name = GEO_LOCATION_ADDRESS_ZH_FIELD)
    val deliveryLocationAddressZh: String? = null,

    @ColumnInfo(name = ORDER_DELIVERY_LOCATION_LATITUDE_FIELD)
    val deliveryLocationGeoPointLat: Double? = null,

    @ColumnInfo(name = ORDER_DELIVERY_LOCATION_LONGITUDE_FIELD)
    val deliveryLocationGeoPointLong: Double? = null,

    @ColumnInfo(name = ORDER_SUBTOTAL_COST_FIELD)
    val subtotalCost: Double? = null,

    @ColumnInfo(name = ORDER_DELIVERY_COST_FIELD)
    val deliveryCost: Double? = null,

    @ColumnInfo(name = ORDER_TOTAL_COST_FIELD)
    val totalCost: Double? = null,

    @ColumnInfo(name = ORDER_CREATED_AT_FIELD)
    val createdAt: Date? = null,

    @ColumnInfo(name = ORDER_UPDATED_AT_FIELD)
    val updatedAt: Date? = null
)