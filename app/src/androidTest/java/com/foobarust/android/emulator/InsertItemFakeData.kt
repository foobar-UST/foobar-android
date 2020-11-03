package com.foobarust.android.emulator

import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.foobarust.android.InsertFakeDataActivity
import com.foobarust.data.common.Constants.SELLER_ITEMS_BASIC_COLLECTION
import com.foobarust.data.common.Constants.SELLER_ITEMS_COLLECTION
import com.foobarust.data.models.ExtraItemEntity
import com.foobarust.data.models.ItemChoiceEntity
import com.foobarust.data.models.SellerItemBasicEntity
import com.foobarust.data.models.SellerItemDetailEntity
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
class InsertItemFakeData {

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
    fun insert_items_basic_fake_data() = runBlocking(Dispatchers.IO) {
        val jsonInputStream = InstrumentationRegistry.getInstrumentation()
            .context.assets
            .open("items_fake_data.json")
        val jsonString = jsonInputStream.bufferedReader().use { it.readText() }
        val itemSerializedList: List<ItemSerialized> = Json.decodeFromString(jsonString)

        val chunks = itemSerializedList.chunked(size = 50)
        val asyncJobs = chunks.map { chunk ->
            async {
                chunk.map { it.toItemBasicEntity() }
                    .forEach { item ->
                        firestore.collection(SELLER_ITEMS_BASIC_COLLECTION).document(item.id!!)
                            .set(item)
                            .await()
                    }
            }
        }

        asyncJobs.awaitAll()

        assertTrue(true)
    }

    @Test
    fun insert_items_fake_data() = runBlocking(Dispatchers.IO) {
        val jsonInputStream = InstrumentationRegistry.getInstrumentation()
            .context.assets
            .open("items_fake_data.json")
        val jsonString = jsonInputStream.bufferedReader().use { it.readText() }
        val itemSerializedList: List<ItemSerialized> = Json.decodeFromString(jsonString)

        val chunks = itemSerializedList.chunked(size = 50)
        val asyncJobs = chunks.map { chunk ->
            async {
                chunk.map { it.toItemDetailEntity() }
                    .forEach { item ->
                        firestore.collection(SELLER_ITEMS_COLLECTION).document(item.id!!)
                            .set(item)
                            .await()
                    }
            }
        }

        asyncJobs.awaitAll()

        assertTrue(true)
    }
}

@Serializable
private data class ItemChoiceSerialized(
    val id: String,
    val title: String,
    val extra_price: Double
)

@Serializable
private data class ExtraItemSerialized(
    val id: String,
    val title: String,
    val price: Double
)

@Serializable
private data class ItemSerialized(
    val id: String,
    val title: String,
    val description: String,
    val seller_id: String,
    val catalog_id: String,
    val price: Double,
    val count: Int,
    val image_url: String,
    val choices: List<ItemChoiceSerialized>,
    val extra_items: List<ExtraItemSerialized>,
    val available: Boolean
) {
    fun toItemDetailEntity(): SellerItemDetailEntity {
        return SellerItemDetailEntity(
            id = id, title = title, description = description,
            sellerId = seller_id, catalogId = catalog_id, price = price,
            count = count, imageUrl = image_url,
            choices = choices.map {
                ItemChoiceEntity(id = it.id, title = it.title, extraPrice = it.extra_price)
            },
            extraItems = extra_items.map {
                ExtraItemEntity(id = it.id, title = it.title, price = it.price)
            },
            available = available
        )
    }

    fun toItemBasicEntity(): SellerItemBasicEntity {
        return SellerItemBasicEntity(id, title, description, catalog_id, price, available)
    }
}