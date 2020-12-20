package com.foobarust.domain.models.seller

data class SellerDetailWithCatalogs(
    val sellerDetail: SellerDetail,
    val catalogs: List<SellerCatalog>
)