package com.foobarust.android.emulator

import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.foobarust.android.InsertFakeDataActivity
import com.foobarust.data.common.Constants.SELLERS_BASIC_COLLECTION
import com.foobarust.data.common.Constants.SELLERS_CATALOGS_SUB_COLLECTION
import com.foobarust.data.common.Constants.SELLERS_COLLECTION
import com.foobarust.data.models.seller.SellerBasicEntity
import com.foobarust.data.models.seller.SellerCatalogEntity
import com.foobarust.data.models.seller.SellerDetailEntity
import com.foobarust.data.models.seller.SellerLocationEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

/**
 * Created by kevin on 10/12/20
 */

@HiltAndroidTest
class InsertSellerFakeData {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    @get:Rule
    val activityScenarioRule = activityScenarioRule<InsertFakeDataActivity>()

    @Inject
    lateinit var firestore: FirebaseFirestore

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun insert_sellers_fake_data() = runBlocking(Dispatchers.IO) {
        val serializedList: List<SellerSerialized> = Json.decodeFromString(decodeSellersJson())

        serializedList.map { it.toSellerDetailEntity() }
            .forEach {
                firestore.collection(SELLERS_COLLECTION)
                    .document(it.id!!)
                    .set(it)
                    .await()
            }

        assertTrue(true)
    }

    @Test
    fun insert_sellers_basic_fake_data() = runBlocking(Dispatchers.IO) {
        val serializedList: List<SellerSerialized> = Json.decodeFromString(decodeSellersJson())

        serializedList.map { it.toSellerBasicEntity() }
            .forEach {
                firestore.collection(SELLERS_BASIC_COLLECTION)
                    .document(it.id!!)
                    .set(it)
                    .await()
            }

        assertTrue(true)
    }

    @Test
    fun insert_seller_catalogs_fake_data() = runBlocking(Dispatchers.IO) {
        val serializedList: List<SellerCatalogSerialized> = Json.decodeFromString(decodeCatalogsJson())

        serializedList.forEach {
            val sellerId = it.seller_id
            val sellerCatalogEntity = it.toSellerCatalogEntity()

            firestore.collection(SELLERS_COLLECTION)
                .document(sellerId)
                .collection(SELLERS_CATALOGS_SUB_COLLECTION)
                .document(it.id)
                .set(sellerCatalogEntity)
                .await()
        }
    }

    private fun decodeSellersJson(): String {
        val jsonInputStream = InstrumentationRegistry.getInstrumentation()
            .context.assets
            .open("sellers_fake_data.json")

        return jsonInputStream.bufferedReader().use { it.readText() }
    }

    private fun decodeCatalogsJson(): String {
        val jsonInputStream = InstrumentationRegistry.getInstrumentation()
            .context.assets
            .open("seller_catalogs_fake_data.json")

        return jsonInputStream.bufferedReader().use { it.readText() }
    }
}

@Serializable
private data class SellerSerialized(
    val id: String,
    val name: String,
    val name_zh: String? = null,
    val description: String? = null,
    val description_zh: String? = null,
    val website: String? = null,
    val phone_num: String,
    val location: LocationSerialized,
    val image_url: String? = null,
    val min_spend: Double,
    val rating: Double,
    val rating_count: Int,
    val type: Int,
    val online: Boolean,
    val notice: String? = null,
    val opening_hours: String,
    val tags: List<String>
) {
    fun toSellerDetailEntity(): SellerDetailEntity {
        val location = SellerLocationEntity(
            address = this.location.address,
            addressZh = this.location.address_zh,
            geoPoint = GeoPoint(this.location.geopoint.lat, this.location.geopoint.long)
        )

        return SellerDetailEntity(
            id = id,
            name = name,
            nameZh = name_zh,
            description = description,
            descriptionZh = description_zh,
            website = website,
            phone_num = phone_num,
            location = location,
            image_url = image_url,
            min_spend = min_spend,
            rating = rating,
            ratingCount = rating_count,
            type = type,
            online = online,
            notice = notice,
            openingHours = opening_hours,
            tags = tags
        )
    }

    fun toSellerBasicEntity(): SellerBasicEntity {
        return SellerBasicEntity(
            id = id,
            name = name,
            nameZh = name_zh,
            imageUrl = image_url,
            minSpend = min_spend,
            rating = rating,
            ratingCount = rating_count,
            type = type,
            online = online,
            tags = tags
        )
    }
}

@Serializable
private data class LocationSerialized(
    val address: String,
    val address_zh: String,
    val geopoint: GeoPointSerialized
)

@Serializable
private data class GeoPointSerialized(
    val lat: Double,
    val long: Double
)

@Serializable
private data class SellerCatalogSerialized(
    val id: String,
    val seller_id: String,
    val title: String,
    val title_zh: String? = null,
    val available: Boolean
) {
    fun toSellerCatalogEntity(): SellerCatalogEntity {
        return SellerCatalogEntity(
            id = id,
            title = title,
            titleZh = title_zh,
            available = available
        )
    }
}