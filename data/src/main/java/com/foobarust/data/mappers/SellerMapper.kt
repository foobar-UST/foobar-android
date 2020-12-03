package com.foobarust.data.mappers

import com.foobarust.data.models.seller.*
import com.foobarust.domain.models.common.Geolocation
import com.foobarust.domain.models.seller.*
import javax.inject.Inject

/**
 * Created by kevin on 9/27/20
 */

class SellerMapper @Inject constructor() {

    fun toSellerBasic(entity: SellerBasicEntity): SellerBasic {
        return SellerBasic(
            id = entity.id!!,
            name = entity.name ?: "",
            nameZh = entity.nameZh,
            imageUrl = entity.imageUrl,
            rating = entity.rating!!,
            type = SellerType.values()[entity.type!!],
            online = entity.online ?: false,
            minSpend = entity.minSpend ?: 0.toDouble(),
            tags = entity.tags ?: emptyList()
        )
    }

    fun toSellerDetail(entity: SellerDetailEntity): SellerDetail {
        // Map location
        val location = entity.location!!.let {
            SellerLocation(
                address = it.address!!,
                addressZh = it.addressZh!!,
                geolocation = Geolocation(
                    latitude = it.geoPoint?.latitude ?: 0.toDouble(),
                    longitude = it.geoPoint?.longitude ?: 0.toDouble()
                )
            )
        }

        return SellerDetail(
            id = entity.id!!,
            name = entity.name!!,
            nameZh = entity.nameZh,
            description = entity.description,
            descriptionZh = entity.descriptionZh,
            website = entity.website,
            phoneNum = entity.phone_num ?: "",
            location = location,
            imageUrl = entity.image_url,
            minSpend = entity.min_spend ?: 0.toDouble(),
            rating = entity.rating ?: 0.toDouble(),
            ratingCount = entity.ratingCount ?: 0,
            type = SellerType.values()[entity.type!!],
            online = entity.online ?: false,
            openingHours = entity.openingHours!!,
            notice = entity.notice,
            tags = entity.tags ?: emptyList()
        )
    }

    fun toSellerCatalog(entity: SellerCatalogEntity): SellerCatalog {
        return SellerCatalog(
            id = entity.id!!,
            title = entity.title!!,
            titleZh = entity.titleZh,
            available = entity.available ?: false,
            updatedAt = entity.updatedAt?.toDate()
        )
    }

    fun toSellerItemDetail(entity: SellerItemDetailEntity): SellerItemDetail {
        return SellerItemDetail(
            id = entity.id!!,
            title = entity.title!!,
            titleZh = entity.titleZh,
            description = entity.description,
            descriptionZh = entity.descriptionZh,
            catalogId = entity.catalogId!!,
            price = entity.price!!,
            imageUrl = entity.imageUrl,
            count = entity.count ?: 0,
            available = entity.available ?: false,
            updatedAt = entity.updatedAt?.toDate()
        )
    }

    fun toSellerItemBasic(entity: SellerItemBasicEntity): SellerItemBasic {
        return SellerItemBasic(
            id = entity.id!!,
            title = entity.title!!,
            titleZh = entity.titleZh,
            catalogId = entity.catalogId!!,
            price = entity.price!!,
            imageUrl = entity.imageUrl,
            count = entity.count ?: 0,
            available = entity.available ?: false,
            updatedAt = entity.updatedAt?.toDate()
        )
    }
}