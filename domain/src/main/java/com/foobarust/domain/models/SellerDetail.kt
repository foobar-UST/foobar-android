package com.foobarust.domain.models

/**
 * Created by kevin on 9/27/20
 */

data class SellerDetail(
    val id: String,
    val name: String,
    val description: String,
    val email: String,
    val phoneNum: String,
    val location: SellerLocation,
    val imageUrl: String? = null,
    val minSpend: Double? = null,
    val rating: Double,
    val catalogs: List<SellerCatalog>,
    val type: SellerType,
    val online: Boolean,
    val openingHours: String,
    val notice: String? = null
)

fun SellerDetail.removeNonPurchasableCatalogs(): SellerDetail {
    return copy(
        catalogs = catalogs.filter { it.purchasable() }
    )
}
