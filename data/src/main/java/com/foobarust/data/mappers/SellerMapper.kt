package com.foobarust.data.mappers

import com.foobarust.data.common.Constants.SELLER_SECTION_STATE_AVAILABLE
import com.foobarust.data.common.Constants.SELLER_SECTION_STATE_DELIVERED
import com.foobarust.data.common.Constants.SELLER_SECTION_STATE_PENDING
import com.foobarust.data.common.Constants.SELLER_SECTION_STATE_PREPARING
import com.foobarust.data.common.Constants.SELLER_SECTION_STATE_SHIPPED
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
            tags = entity.tags ?: emptyList(),
            deliveryCost = entity.deliveryCost
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
            price = entity.price!!,
            imageUrl = entity.imageUrl,
            count = entity.count ?: 0,
            available = entity.available ?: false,
            updatedAt = entity.updatedAt?.toDate()
        )
    }

    fun toSellerSectionDetail(entity: SellerSectionDetailEntity): SellerSectionDetail {
        return SellerSectionDetail(
            id = entity.id!!,
            title = entity.title!!,
            titleZh = entity.titleZh,
            groupId = entity.groupId!!,
            sellerId = entity.sellerId!!,
            sellerName = entity.sellerName!!,
            sellerNameZh = entity.sellerNameZh,
            deliveryTime = entity.deliveryTime!!.toDate(),
            deliveryLocation = entity.deliveryLocation!!,
            deliveryLocationZh = entity.deliveryLocationZh,
            description = entity.description!!,
            descriptionZh = entity.descriptionZh,
            cutoffTime = entity.cutoffTime!!.toDate(),
            maxUsers = entity.maxUsers!!,
            joinedUsersCount = entity.joinedUsersCount ?: 0,
            joinedUsersIds = entity.joinedUsersIds ?: emptyList(),
            imageUrl = entity.imageUrl,
            state = parseSectionState(entity.state!!),
            available = entity.available ?: false
        )
    }

    fun toSellerSectionBasic(entity: SellerSectionBasicEntity): SellerSectionBasic {
        return SellerSectionBasic(
            id = entity.id!!,
            title = entity.title!!,
            titleZh = entity.titleZh,
            sellerId = entity.sellerId!!,
            sellerName = entity.sellerName!!,
            sellerNameZh = entity.sellerNameZh,
            deliveryTime = entity.deliveryTime!!.toDate(),
            cutoffTime = entity.cutoffTime!!.toDate(),
            maxUsers = entity.maxUsers!!,
            joinedUsersCount = entity.joinedUsersCount ?: 0,
            imageUrl = entity.imageUrl,
            state = parseSectionState(entity.state!!),
            available = entity.available ?: false
        )
    }

    private fun parseSectionState(sectionState: String): SellerSectionState {
        return when (sectionState) {
            SELLER_SECTION_STATE_AVAILABLE -> SellerSectionState.AVAILABLE
            SELLER_SECTION_STATE_PENDING -> SellerSectionState.PENDING
            SELLER_SECTION_STATE_PREPARING -> SellerSectionState.PREPARING
            SELLER_SECTION_STATE_SHIPPED -> SellerSectionState.SHIPPED
            SELLER_SECTION_STATE_DELIVERED -> SellerSectionState.DELIVERED
            else -> throw IllegalArgumentException("Invalid SellerSectionState.")
        }
    }
}