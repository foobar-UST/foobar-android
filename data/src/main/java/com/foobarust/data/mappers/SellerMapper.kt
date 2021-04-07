package com.foobarust.data.mappers

import com.foobarust.data.constants.Constants.SELLER_SECTION_STATE_AVAILABLE
import com.foobarust.data.constants.Constants.SELLER_SECTION_STATE_DELIVERED
import com.foobarust.data.constants.Constants.SELLER_SECTION_STATE_PREPARING
import com.foobarust.data.constants.Constants.SELLER_SECTION_STATE_PROCESSING
import com.foobarust.data.constants.Constants.SELLER_SECTION_STATE_READY_FOR_PICK_UP
import com.foobarust.data.constants.Constants.SELLER_SECTION_STATE_SHIPPED
import com.foobarust.data.models.explore.ItemCategoryDto
import com.foobarust.data.models.seller.*
import com.foobarust.domain.models.common.GeolocationPoint
import com.foobarust.domain.models.explore.SellerItemCategory
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
            orderRating = dto.orderRating!!,
            type = SellerType.values()[dto.type!!],
            online = dto.online ?: false,
            minSpend = dto.minSpend ?: 0.toDouble(),
            tags = dto.tags ?: emptyList()
        )
    }

    fun toSellerBasic(response: SearchSellerResponse): SellerBasic {
        return SellerBasic(
            id = response.id,
            name = response.name,
            nameZh = response.nameZh,
            imageUrl = response.imageUrl,
            orderRating = response.rating,
            type = SellerType.values()[response.type],
            online = response.online,
            minSpend = response.minSpend,
            tags = response.tags
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
            orderRating = dto.orderRating ?: 0.0,
            deliveryRating = dto.deliveryRating,
            ratingCount = mapSellerRatingCount(dto.ratingCount!!),
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
            state = mapSectionState(dto.state!!),
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
            state = mapSectionState(dto.state!!),
            available = dto.available ?: false
        )
    }

    fun toSellerItemCategory(dto: ItemCategoryDto): SellerItemCategory {
        return SellerItemCategory(
            id = dto.id!!,
            tag = dto.tag!!,
            title = dto.title!!,
            titleZh = dto.titleZh,
            imageUrl = dto.imageUrl
        )
    }

    fun toSellerRatingBasic(dto: SellerRatingBasicDto): SellerRatingBasic {
        return SellerRatingBasic(
            id = dto.id!!,
            username = dto.username!!,
            userPhotoUrl = dto.userPhotoUrl,
            orderRating = dto.orderRating!!,
            deliveryRating = dto.deliveryRating,
            createdAt = dto.createdAt!!.toDate()
        )
    }

    private fun mapSectionState(sectionState: String): SellerSectionState {
        return when (sectionState) {
            SELLER_SECTION_STATE_AVAILABLE -> SellerSectionState.AVAILABLE
            SELLER_SECTION_STATE_PROCESSING -> SellerSectionState.PROCESSING
            SELLER_SECTION_STATE_PREPARING -> SellerSectionState.PREPARING
            SELLER_SECTION_STATE_SHIPPED -> SellerSectionState.SHIPPED
            SELLER_SECTION_STATE_READY_FOR_PICK_UP -> SellerSectionState.READY_FOR_PICK_UP
            SELLER_SECTION_STATE_DELIVERED -> SellerSectionState.DELIVERED
            else -> throw IllegalArgumentException("Invalid SellerSectionState.")
        }
    }

    private fun mapSellerRatingCount(dto: SellerRatingCountDto): SellerRatingCount {
        return SellerRatingCount(
            excellent = dto.excellent ?: 0,
            veryGood = dto.veryGood ?: 0,
            good = dto.good ?: 0,
            fair = dto.fair ?: 0,
            poor = dto.poor ?: 0
        )
    }

    private fun fromGeoLocationPoint(latitude: Double, longitude: Double): String {
        return "$latitude,$longitude"
    }

    private fun toGeoLocationPoint(location: String): GeolocationPoint {
        val output = location.split(',')
        return GeolocationPoint(
            latitude = output[0].toDouble(),
            longitude = output[1].toDouble()
        )
    }
}