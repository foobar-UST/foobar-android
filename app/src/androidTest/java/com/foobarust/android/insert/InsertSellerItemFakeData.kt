package com.foobarust.android.insert

import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.foobarust.android.InsertFakeDataActivity
import com.foobarust.data.common.Constants.SELLERS_COLLECTION
import com.foobarust.data.common.Constants.SELLER_ITEMS_SUB_COLLECTION
import com.foobarust.data.models.seller.SellerItemBasicDto
import com.foobarust.data.models.seller.SellerItemDetailDto
import com.google.firebase.firestore.FirebaseFirestore
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
class InsertSellerItemFakeData {

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
    fun insert_seller_items_fake_data() = runBlocking(Dispatchers.IO) {
        val serializedListSeller: List<SellerItemSerialized> = Json.decodeFromString(decodeJson())

        serializedListSeller.forEach {
            val sellerId = it.seller_id
            val sellerItemDetailEntity = it.toSellerItemDetailEntity()

            firestore.document(
                "$SELLERS_COLLECTION/$sellerId/$SELLER_ITEMS_SUB_COLLECTION/${it.id}"
            )
                .set(sellerItemDetailEntity)
                .await()
        }

        assertTrue(true)
    }

    /*
    @Test
    fun insert_seller_items_basic_fake_data() = runBlocking(Dispatchers.IO) {
        val serializedListSeller: List<SellerItemSerialized> = Json.decodeFromString(decodeJson())

        serializedListSeller.forEach {
            val sellerId = it.seller_id
            val sellerItemBasicEntity = it.toSellerItemBasicEntity()

            firestore.document(
                "$SELLERS_COLLECTION/$sellerId/$SELLER_ITEMS_BASIC_SUB_COLLECTION/${it.id}"
            )
                .set(sellerItemBasicEntity)
                .await()
        }

        assertTrue(true)
    }

     */

    private fun decodeJson(): String {
        val jsonInputStream = InstrumentationRegistry.getInstrumentation()
            .context.assets
            .open("seller_items_fake_data.json")

        return jsonInputStream.bufferedReader().use { it.readText() }
    }
}

@Serializable
private data class SellerItemSerialized(
    val id: String,
    val title: String,
    val title_zh: String? = null,
    val description: String? = null,
    val description_zh: String? = null,
    val catalog_id: String,
    val seller_id: String,
    val price: Double,
    val image_url: String? = null,
    val count: Int,
    val available: Boolean
) {
    fun toSellerItemDetailEntity(): SellerItemDetailDto {
        return SellerItemDetailDto(
            id = id,
            title = title,
            titleZh = title_zh,
            description = description,
            descriptionZh = description_zh,
            catalogId = catalog_id,
            sellerId = seller_id,
            price = price,
            imageUrl = image_url,
            count = count,
            available = available
        )
    }

    fun toSellerItemBasicEntity(): SellerItemBasicDto {
        return SellerItemBasicDto(
            id = id,
            title = title,
            titleZh = title_zh,
            catalogId = catalog_id,
            price = price,
            imageUrl = image_url,
            count = count,
            available = available
        )
    }
}