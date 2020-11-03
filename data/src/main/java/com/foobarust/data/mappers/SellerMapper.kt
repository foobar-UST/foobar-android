package com.foobarust.data.mappers

import com.foobarust.data.models.SellerBasicEntity
import com.foobarust.data.models.SellerDetailEntity
import com.foobarust.data.models.SellerItemBasicEntity
import com.foobarust.data.models.SellerItemDetailEntity
import com.foobarust.domain.models.*
import javax.inject.Inject

/**
 * Created by kevin on 9/27/20
 */

class SellerMapper @Inject constructor() {

    fun toSellerBasic(sellerBasicEntity: SellerBasicEntity): SellerBasic {
        return SellerBasic(
            id = sellerBasicEntity.id!!,
            name = sellerBasicEntity.name!!,
            imageUrl = sellerBasicEntity.imageUrl,
            rating = sellerBasicEntity.rating!!,
            type = SellerType.values()[sellerBasicEntity.type!!],
            online = sellerBasicEntity.online ?: false,
            minSpend = sellerBasicEntity.minSpend ?: 0.0
        )
    }

    fun toSellerDetail(sellerDetailEntity: SellerDetailEntity): SellerDetail {
        // Map location
        val location = sellerDetailEntity.location!!.let {
            SellerLocation(
                address = it.address!!,
                geoLocation = GeoLocation(
                    latitude = it.geoPoint!!.latitude,
                    longitude = it.geoPoint.longitude
                )
            )
        }

        // Map catalogs list
        val catalogs = sellerDetailEntity.catalogs?.map {
            SellerCatalog(
                id = it.id!!,
                name = it.name!!,
                available = it.available ?: false,
                startTime = it.startTime,
                endTime = it.endTime
            )
        } ?: emptyList()

        return SellerDetail(
            id = sellerDetailEntity.id!!,
            name = sellerDetailEntity.name!!,
            description = sellerDetailEntity.description!!,
            email = sellerDetailEntity.email!!,
            phoneNum = sellerDetailEntity.phone_num!!,
            location = location,
            imageUrl = sellerDetailEntity.image_url,
            minSpend = sellerDetailEntity.min_spend,
            rating = sellerDetailEntity.rating!!,
            catalogs = catalogs,
            type = SellerType.values()[sellerDetailEntity.type!!],
            online = sellerDetailEntity.online ?: false,
            openingHours = sellerDetailEntity.openingHours!!,
            notice = sellerDetailEntity.notice
        )
    }

    fun toSellerItemBasic(sellerItemBasicEntity: SellerItemBasicEntity): SellerItemBasic {
        return SellerItemBasic(
            id = sellerItemBasicEntity.id!!,
            title = sellerItemBasicEntity.title!!,
            description = sellerItemBasicEntity.description!!,
            catalogId = sellerItemBasicEntity.catalogId!!,
            price = sellerItemBasicEntity.price!!,
            available = sellerItemBasicEntity.available ?: false
        )
    }

    fun toSellerItemDetail(sellerItemDetailEntity: SellerItemDetailEntity): SellerItemDetail {
        val choiceList = sellerItemDetailEntity.choices?.map {
            SellerItemChoice(
                id = it.id!!,
                title = it.title!!,
                extraPrice = it.extraPrice!!
            )
        } ?: emptyList()

        val extraItemList = sellerItemDetailEntity.extraItems?.map {
            SellerItemExtraItem(
                id = it.id!!,
                title = it.title!!,
                price = it.price!!
            )
        } ?: emptyList()

        return SellerItemDetail(
            id = sellerItemDetailEntity.id!!,
            title = sellerItemDetailEntity.title!!,
            description = sellerItemDetailEntity.description!!,
            sellerId = sellerItemDetailEntity.sellerId!!,
            catalogId = sellerItemDetailEntity.catalogId!!,
            price = sellerItemDetailEntity.price!!,
            imageUrl = sellerItemDetailEntity.imageUrl,
            choices = choiceList,
            extraItems = extraItemList,
            available = sellerItemDetailEntity.available ?: false
        )
    }
}