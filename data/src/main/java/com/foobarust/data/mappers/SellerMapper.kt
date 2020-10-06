package com.foobarust.data.mappers

import com.foobarust.data.common.Constants.SELLER_CATALOG_ID_FIELD
import com.foobarust.data.common.Constants.SELLER_CATALOG_NAME_FIELD
import com.foobarust.data.models.ItemBasicEntity
import com.foobarust.data.models.SellerBasicEntity
import com.foobarust.data.models.SellerDetailEntity
import com.foobarust.domain.models.*
import javax.inject.Inject

/**
 * Created by kevin on 9/27/20
 */

class SellerMapper @Inject constructor() {

    fun toSellerListItem(sellerBasicEntity: SellerBasicEntity): SellerBasic {
        return SellerBasic(
            id = sellerBasicEntity.id!!,
            name = sellerBasicEntity.name!!,
            description = sellerBasicEntity.description!!,
            imageUrl = sellerBasicEntity.imageUrl,
            openTime = sellerBasicEntity.openTime!!,
            closeTime = sellerBasicEntity.closeTime!!,
            minSpend = sellerBasicEntity.minSpend,
            rating = sellerBasicEntity.rating!!,
            type = SellerType.values()[sellerBasicEntity.type!!]
        )
    }

    fun toSellerDetail(sellerDetailEntity: SellerDetailEntity): SellerDetail {
        val location = GeoLocation(
            latitude = sellerDetailEntity.location!!.latitude,
            longitude = sellerDetailEntity.location.longitude
        )

        val catalogs = sellerDetailEntity.catalogs?.map {
            SellerCatalog(
                id = it.getValue(SELLER_CATALOG_ID_FIELD),
                name = it.getValue(SELLER_CATALOG_NAME_FIELD)
            )
        } ?: emptyList()

        return SellerDetail(
            id = sellerDetailEntity.id!!,
            name = sellerDetailEntity.name!!,
            description = sellerDetailEntity.description!!,
            email = sellerDetailEntity.email!!,
            phoneNum = sellerDetailEntity.phone_num!!,
            location = location,
            address = sellerDetailEntity.address!!,
            imageUrl = sellerDetailEntity.image_url,
            openTime = sellerDetailEntity.open_time!!,
            closeTime = sellerDetailEntity.close_time!!,
            minSpend = sellerDetailEntity.min_spend,
            rating = sellerDetailEntity.rating!!,
            catalogs = catalogs,
            type = SellerType.values()[sellerDetailEntity.type!!]
        )
    }

    fun toSellerItemInfo(itemBasicEntity: ItemBasicEntity): SellerItemBasic {
        return SellerItemBasic(
            id = itemBasicEntity.id!!,
            title = itemBasicEntity.title!!,
            description = itemBasicEntity.description!!,
            catalogId = itemBasicEntity.catalog_id!!,
            price = itemBasicEntity.price!!
        )
    }
}