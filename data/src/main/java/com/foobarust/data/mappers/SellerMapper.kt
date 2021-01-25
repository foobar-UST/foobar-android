package com.foobarust.data.mappers

import com.foobarust.data.common.Constants.SELLER_SECTION_STATE_AVAILABLE
import com.foobarust.data.common.Constants.SELLER_SECTION_STATE_DELIVERED
import com.foobarust.data.common.Constants.SELLER_SECTION_STATE_PENDING
import com.foobarust.data.common.Constants.SELLER_SECTION_STATE_PREPARING
import com.foobarust.data.common.Constants.SELLER_SECTION_STATE_SHIPPED
import com.foobarust.data.models.seller.*
import com.foobarust.domain.models.seller.*
import javax.inject.Inject

/**
 * Created by kevin on 9/27/20
 */

class SellerMapper @Inject constructor() {

    fun toSellerBasic(dto: SellerBasicDto): SellerBasic {
        return SellerBasic(
            id = dto.id!!,
            name = dto.name ?: "",
            nameZh = dto.nameZh,
            imageUrl = dto.imageUrl,
            rating = dto.rating!!,
            type = SellerType.values()[dto.type!!],
            online = dto.online ?: false,
            minSpend = dto.minSpend ?: 0.toDouble(),
            tags = dto.tags ?: emptyList()
        )
    }

    fun toSellerDetail(dto: SellerDetailDto): SellerDetail {
        return SellerDetail(
            id = dto.id!!,
            name = dto.name!!,
            nameZh = dto.nameZh,
            description = dto.description,
            descriptionZh = dto.descriptionZh,
            website = dto.website,
            phoneNum = dto.phone_num ?: "",
            location = dto.location!!.toGeolocation(),
            imageUrl = dto.image_url,
            minSpend = dto.min_spend!!,
            rating = dto.rating ?: 0.0,
            ratingCount = dto.ratingCount ?: 0,
            type = SellerType.values()[dto.type!!],
            online = dto.online ?: false,
            openingHours = dto.openingHours!!,
            notice = dto.notice,
            tags = dto.tags ?: emptyList()
        )
    }

    fun toSellerCatalog(dto: SellerCatalogDto): SellerCatalog {
        return SellerCatalog(
            id = dto.id!!,
            title = dto.title!!,
            titleZh = dto.titleZh,
            available = dto.available ?: false,
            updatedAt = dto.updatedAt?.toDate()
        )
    }

    fun toSellerItemDetail(dto: SellerItemDetailDto): SellerItemDetail {
        return SellerItemDetail(
            id = dto.id!!,
            title = dto.title!!,
            titleZh = dto.titleZh,
            description = dto.description,
            descriptionZh = dto.descriptionZh,
            price = dto.price!!,
            imageUrl = dto.imageUrl,
            count = dto.count ?: 0,
            available = dto.available ?: false,
            updatedAt = dto.updatedAt?.toDate()
        )
    }

    fun toSellerItemBasic(dto: SellerItemBasicDto): SellerItemBasic {
        return SellerItemBasic(
            id = dto.id!!,
            title = dto.title!!,
            titleZh = dto.titleZh,
            price = dto.price!!,
            imageUrl = dto.imageUrl,
            count = dto.count ?: 0,
            available = dto.available ?: false
        )
    }

    fun toSellerSectionDetail(dto: SellerSectionDetailDto): SellerSectionDetail {
        return SellerSectionDetail(
            id = dto.id!!,
            title = dto.title!!,
            titleZh = dto.titleZh,
            groupId = dto.groupId!!,
            sellerId = dto.sellerId!!,
            sellerName = dto.sellerName!!,
            sellerNameZh = dto.sellerNameZh,
            deliveryCost = dto.deliveryCost!!,
            deliveryTime = dto.deliveryTime!!.toDate(),
            deliveryLocation = dto.deliveryLocation!!.toGeolocation(),
            description = dto.description!!,
            descriptionZh = dto.descriptionZh,
            cutoffTime = dto.cutoffTime!!.toDate(),
            maxUsers = dto.maxUsers!!,
            joinedUsersCount = dto.joinedUsersCount ?: 0,
            joinedUsersIds = dto.joinedUsersIds ?: emptyList(),
            imageUrl = dto.imageUrl,
            state = parseSectionState(dto.state!!),
            available = dto.available ?: false
        )
    }

    fun toSellerSectionBasic(dto: SellerSectionBasicDto): SellerSectionBasic {
        return SellerSectionBasic(
            id = dto.id!!,
            title = dto.title!!,
            titleZh = dto.titleZh,
            sellerId = dto.sellerId!!,
            sellerName = dto.sellerName!!,
            sellerNameZh = dto.sellerNameZh,
            deliveryTime = dto.deliveryTime!!.toDate(),
            cutoffTime = dto.cutoffTime!!.toDate(),
            maxUsers = dto.maxUsers!!,
            joinedUsersCount = dto.joinedUsersCount ?: 0,
            imageUrl = dto.imageUrl,
            state = parseSectionState(dto.state!!),
            available = dto.available ?: false
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