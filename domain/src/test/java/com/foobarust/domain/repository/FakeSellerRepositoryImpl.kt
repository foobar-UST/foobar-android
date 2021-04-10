package com.foobarust.domain.repository


/*
class FakeSellerRepositoryImpl: SellerRepository {

    private data class SellerCatalogWrapper(
        val sellerId: String,
        val sellerCatalog: SellerCatalog
    )

    private data class SellerItemBasicWrapper(
        val sellerId: String,
        val catalogId: String,
        val sellerItemBasic: SellerItemBasic
    )

    private data class SellerItemDetailWrapper(
        val sellerId: String,
        val sellerItemDetail: SellerItemDetail
    )

    private data class SellerSectionBasicWrapper(
        val sellerId: String,
        val sellerSectionBasic: SellerSectionBasic
    )

    private data class SellerSectionDetailWrapper(
        val sellerId: String,
        val sellerSectionDetail: SellerSectionDetail
    )

    private val sellers = mutableListOf<SellerBasic>()
    private val sellerDetails = mutableListOf<SellerDetail>()
    private val sellerCatalogs = mutableListOf<SellerCatalogWrapper>()
    private val sellerItems = mutableListOf<SellerItemBasicWrapper>()
    private val sellerItemDetails = mutableListOf<SellerItemDetailWrapper>()
    private val sellerSections = mutableListOf<SellerSectionBasicWrapper>()
    private val sellerSectionDetails = mutableListOf<SellerSectionDetailWrapper>()

    private var shouldReturnNetworkError = false

    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    fun addSeller(sellerBasic: SellerBasic) {
        sellers.add(sellerBasic)
    }

    fun removeAllSellers() {
        sellers.clear()
    }

    fun addSellerDetail(sellerDetail: SellerDetail) {
        sellerDetails.add(sellerDetail)
    }

    fun removeAllSellerDetail() {
        sellerDetails.clear()
    }

    fun addSellerCatalogs(sellerId: String, sellerCatalog: SellerCatalog) {
        sellerCatalogs.add(SellerCatalogWrapper(sellerId, sellerCatalog))
    }

    fun addSellerItem(sellerId: String, catalogId: String, itemBasic: SellerItemBasic) {
        sellerItems.add(SellerItemBasicWrapper(sellerId, catalogId, itemBasic))
    }

    fun addSellerItemDetail(sellerId: String, itemDetail: SellerItemDetail) {
        sellerItemDetails.add(SellerItemDetailWrapper(sellerId, itemDetail))
    }

    fun addSellerSection(sellerId: String, sectionBasic: SellerSectionBasic) {
        sellerSections.add(SellerSectionBasicWrapper(sellerId, sectionBasic))
    }

    fun addSellerSectionDetail(sellerId: String, sectionDetail: SellerSectionDetail) {
        sellerSectionDetails.add(SellerSectionDetailWrapper(sellerId, sectionDetail))
    }

    override suspend fun getSeller(sellerId: String): SellerBasic {
        if (shouldReturnNetworkError) throw Exception("Network error.")
        return sellers.find { it.id == sellerId } ?: throw Exception("Not found.")
    }

    override suspend fun getSellerDetail(sellerId: String): SellerDetail {
        if (shouldReturnNetworkError) throw Exception("Network error.")
        return sellerDetails.find { it.id == sellerId } ?: throw Exception("Not found.")
    }

    override suspend fun getSellerCatalogs(sellerId: String): List<SellerCatalog> {
        if (shouldReturnNetworkError) throw Exception("Network error.")
        return sellerCatalogs.filter { it.sellerId == sellerId }
            .map { it.sellerCatalog }
    }

    override fun getSellerBasicsPagingData(sellerType: SellerType): Flow<PagingData<SellerBasic>> {
        if (shouldReturnNetworkError) throw Exception("Network error.")
        val result = sellers.filter { it.type == sellerType }
        return flowOf(PagingData.from(result))
    }

    override suspend fun searchSellers(searchQuery: String, numOfSellers: Int): List<SellerBasic> {
        TODO("Not yet implemented")
    }

    override suspend fun getSellerItemDetail(itemId: String): SellerItemDetail {
        if (shouldReturnNetworkError) throw Exception("Network error.")
        return sellerItemDetails.find {
            it.sellerId == sellerId && it.sellerItemDetail.id == itemId
        }
            ?.sellerItemDetail ?: throw Exception("Not found.")
    }

    override fun getSellerItemsPagingData(
        sellerId: String,
        catalogId: String
    ): Flow<PagingData<SellerItemBasic>> {
        if (shouldReturnNetworkError) throw Exception("Network error.")
        val result = sellerItems.filter { it.sellerId == sellerId && it.catalogId == catalogId }
            .map { it.sellerItemBasic }
        return flowOf(PagingData.from(result))
    }

    override suspend fun getRecentSellerItems(sellerId: String, limit: Int): List<SellerItemBasic> {
        if (shouldReturnNetworkError) throw Exception("Network error.")
        return sellerItems.filter { it.sellerId == sellerId }
            .take(limit)
            .map { it.sellerItemBasic }
    }

    override suspend fun getSellerSectionDetail(sectionId: String): SellerSectionDetail {
        TODO("Not yet implemented")
    }

    override suspend fun getSellerSection(sellerId: String, sectionId: String): SellerSectionBasic {
        if (shouldReturnNetworkError) throw Exception("Network error.")
        return sellerSections.find {
            it.sellerId == sellerId && it.sellerSectionBasic.id == sectionId
        }
            ?.sellerSectionBasic
            ?: throw Exception("Not found.")
    }

    override suspend fun getSellerSectionDetail(
        sellerId: String,
        sectionId: String
    ): SellerSectionDetail {
        if (shouldReturnNetworkError) throw Exception("Network error.")
        return sellerSectionDetails.find {
            it.sellerId == sellerId && it.sellerSectionDetail.id == sectionId
        }
            ?.sellerSectionDetail
            ?: throw Exception("Not found.")
    }

    override suspend fun getSellerSectionBasics(
        sellerId: String,
        numOfSections: Int
    ): List<SellerSectionBasic> {
        if (shouldReturnNetworkError) throw Exception("Network error.")
        return sellerSections.filter {
            it.sellerId == sellerId
        }
            .take(numOfSections)
            .map { it.sellerSectionBasic }
    }

    override fun getAllSellerSectionBasicsPagingData(): Flow<PagingData<SellerSectionBasic>> {
        if (shouldReturnNetworkError) throw Exception("Network error.")
        return flowOf(
            PagingData.from(sellerSections.map { it.sellerSectionBasic })
        )
    }

    override fun getSellerSectionBasicsPagingData(sellerId: String): Flow<PagingData<SellerSectionBasic>> {
        if (shouldReturnNetworkError) throw Exception("Network error.")
        val result = sellerSections.filter { it.sellerId == sellerId }
            .map { it.sellerSectionBasic }
        return flowOf(PagingData.from(result))
    }
}
 */