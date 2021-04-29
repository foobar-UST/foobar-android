package com.foobarust.domain.repository

import androidx.paging.PagingData
import com.foobarust.domain.models.explore.ItemCategory
import com.foobarust.domain.models.seller.*
import com.foobarust.domain.repositories.SellerRepository
import com.foobarust.domain.serialize.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class FakeSellerRepositoryImpl : SellerRepository {

    internal val sellerList: List<SellerSerialized> by lazy {
        deserializeJsonList("sellers_fake_data.json")
    }

    internal val sellerCatalogList: List<SellerCatalogSerialized> by lazy {
        deserializeJsonList("seller_catalogs_fake_data.json")
    }

    internal val sellerItemList: List<SellerItemSerialized> by lazy {
        deserializeJsonList("seller_items_fake_data.json")
    }

    internal val sellerSectionList: List<SellerSectionSerialized> by lazy {
        deserializeJsonList("seller_sections_fake_data.json")
    }

    internal val itemCategoryList: List<ItemCategorySerialized> by lazy {
        deserializeJsonList("item_categories_fake_data.json")
    }

    internal val sellerRatingList: List<SellerRatingSerialized> by lazy {
        deserializeJsonList("seller_ratings_fake_data.json")
    }

    private var shouldReturnNetworkError = false

    override suspend fun getSellerDetail(sellerId: String): SellerDetail {
        if (shouldReturnNetworkError) throw Exception("Network error.")
        return sellerList.first { it.id == sellerId }.toSellerDetail()
    }

    override suspend fun getSellerCatalogs(sellerId: String): List<SellerCatalog> {
        if (shouldReturnNetworkError) throw Exception("Network error.")
        return sellerCatalogList.filter { it.seller_id == sellerId }
            .map { it.toSellerCatalog() }
    }

    override fun getSellerBasicsPagingData(
        sellerBasicsFilter: SellerBasicsFilter
    ): Flow<PagingData<SellerBasic>> {
        return sellerList.filter {
            it.type == sellerBasicsFilter.sellerType?.ordinal &&
            it.tags.contains(sellerBasicsFilter.categoryTag)
        }.map {
            it.toSellerBasic()
        }.let {
            flowOf(PagingData.from(it))
        }
    }

    override suspend fun searchSellers(searchQuery: String, numOfSellers: Int): List<SellerBasic> {
        if (shouldReturnNetworkError) throw Exception("Network error.")
        return sellerList.filter {
            "${it.name}${it.name_zh}".contains(searchQuery)
        }.take(numOfSellers).map {
            it.toSellerBasic()
        }
    }

    override suspend fun getSellerItemDetail(itemId: String): SellerItemDetail {
        if (shouldReturnNetworkError) throw Exception("Network error.")
        return sellerItemList.first { it.id == itemId }.toSellerItemDetail()
    }

    override fun getSellerItemsPagingData(
        sellerId: String,
        catalogId: String
    ): Flow<PagingData<SellerItemBasic>> {
        return sellerItemList.filter {
            it.seller_id == sellerId &&
            it.catalog_id == catalogId
        }.map {
            it.toSellerItemBasic()
        }.let {
            flowOf(PagingData.from(it))
        }
    }

    override suspend fun getRecentSellerItems(sellerId: String, limit: Int): List<SellerItemBasic> {
        if (shouldReturnNetworkError) throw Exception("Network error.")
        return sellerItemList.filter { it.seller_id == sellerId }
            .take(limit)
            .map { it.toSellerItemBasic() }
    }

    override suspend fun getSellerSectionDetail(sectionId: String): SellerSectionDetail {
        if (shouldReturnNetworkError) throw Exception("Network error.")
        return sellerSectionList.first { it.id == sectionId }.toSellerSectionDetail()
    }

    override suspend fun getSellerSectionBasics(
        sellerId: String,
        numOfSections: Int
    ): List<SellerSectionBasic> {
        if (shouldReturnNetworkError) throw Exception("Network error.")
        return sellerSectionList.filter { it.seller_id == sellerId }
            .take(numOfSections)
            .map { it.toSellerSectionBasic() }
    }

    override fun getAllSellerSectionBasicsPagingData(): Flow<PagingData<SellerSectionBasic>> {
        return flowOf(PagingData.from(sellerSectionList.map { it.toSellerSectionBasic() }))
    }

    override fun getSellerSectionBasicsPagingData(sellerId: String): Flow<PagingData<SellerSectionBasic>> {
        return sellerSectionList.filter { it.seller_id == sellerId }
            .map { it.toSellerSectionBasic() }
            .let { flowOf(PagingData.from(it)) }
    }

    override suspend fun getItemCategories(): List<ItemCategory> {
        if (shouldReturnNetworkError) throw Exception("Network error.")
        return itemCategoryList.map { it.toItemCategory() }
    }

    override suspend fun getItemCategory(categoryTag: String): ItemCategory {
        if (shouldReturnNetworkError) throw Exception("Network error.")
        return itemCategoryList.first { it.tag == categoryTag }.toItemCategory()
    }

    override fun getSellerRatingsPagingData(
        sellerId: String,
        sortOption: SellerRatingSortOption
    ): Flow<PagingData<SellerRatingBasic>> {
        return sellerRatingList.filter { it.seller_id == sellerId }
            .map { it.toSellerRatingBasic() }
            .let { flowOf(PagingData.from(it)) }
    }

    fun setNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    private inline fun<reified T> deserializeJsonList(file: String): List<T> {
        val inputStream = javaClass.classLoader.getResourceAsStream(file)!!
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        return Json.decodeFromString(jsonString)
    }
}