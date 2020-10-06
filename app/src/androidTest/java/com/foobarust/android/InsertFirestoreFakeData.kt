package com.foobarust.android

import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.foobarust.data.models.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*
import javax.inject.Inject

/**
 * Created by kevin on 10/3/20
 */

@HiltAndroidTest
class InsertFirestoreFakeData {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val activityScenarioRule = activityScenarioRule<DemoActivity>()

    @Inject
    lateinit var firestore: FirebaseFirestore

    /*
    val settings = firestoreSettings {
        host = "192.168.128.106:8080"
        isSslEnabled = false
        isPersistenceEnabled = false
    }

    val firestore = Firebase.firestore.apply {
        firestoreSettings = settings
    }

     */

    val myUserUid = "fFFdrdmz9zeyw7rWNjhrJaXnVOh2"

    @Before
    fun init() {
        hiltRule.inject()
    }

    /*
    @Test
    fun insert_users_fake_data() = runBlocking {
        firestore.collection("users").document(myUserUid)
            .set(mapOf(
                "username" to "kthon",
                "email" to "kthon@connect.ust.hk",
                "phone_num" to "67681436",
                "allow_order" to true,
                "photo_url" to "https://firebasestorage.googleapis.com/v0/b/foobar-group-delivery-app.appspot.com/o/user_photos%2FfFFdrdmz9zeyw7rWNjhrJaXnVOh2?alt=media&token=3dad277a-bd1a-402a-87e0-d557bc5dee07"
            ))
            .await()

        assertTrue(true)
    }

     */

    @Test
    fun insert_sellers_basic_fake_data() = runBlocking {
        val jsonInputStream = InstrumentationRegistry.getInstrumentation()
            .context.assets
            .open("sellers_fake_data.json")
        val jsonString = jsonInputStream.bufferedReader().use { it.readText() }

        val sellerObjs: List<SellerObj> = Json.decodeFromString(jsonString)

        sellerObjs.map { it.toSellerBasicDoc() }
            .forEach {
                firestore.collection("sellers_basic").document(it.id!!)
                    .set(it)
                    .await()
            }

        assertTrue(true)
    }

    @Test
    fun insert_sellers_fake_data() = runBlocking {
        val jsonInputStream = InstrumentationRegistry.getInstrumentation()
            .context.assets
            .open("sellers_fake_data.json")
        val jsonString = jsonInputStream.bufferedReader().use { it.readText() }

        val sellerObjs: List<SellerObj> = Json.decodeFromString(jsonString)

        sellerObjs.map { it.toSellerDoc() }
            .forEach {
                firestore.collection("sellers").document(it.id!!)
                    .set(it)
                    .await()
            }

        assertTrue(true)
    }

    @Test
    fun insert_advertises_basic_fake_data() = runBlocking {
        val advertisesBasicDocs = listOf(
            AdvertiseBasicEntity(
                id = "KbcdIweT3uCMI8sGh6Do",
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/foobar-group-delivery-app.appspot.com/o/test_images%2Fsample_image_3.jpg?alt=media&token=4c5e8ace-aa3e-40e5-81c1-46597e128b9b"
            ),
            AdvertiseBasicEntity(
                id = "TO7faqKnyI1g6BLl5EOR",
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/foobar-group-delivery-app.appspot.com/o/test_images%2Fsample_image_1.jpg?alt=media&token=4453e3c8-de2e-4863-8e3f-e9347f73c2a0"
            ),
            AdvertiseBasicEntity(
                id = "VJtZxRBIwL1Tz79lrAPk",
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/foobar-group-delivery-app.appspot.com/o/test_images%2Fsample_image_4.jpg?alt=media&token=ccc9f16a-f2f1-4c8e-865a-89daeb49121d"
            ),
            AdvertiseBasicEntity(
                id = "XPYI0SjEQTJ03Pc7w27p",
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/foobar-group-delivery-app.appspot.com/o/test_images%2Fsample_image_2.jpg?alt=media&token=3c16ab78-aa1b-4c59-b918-efac5e69f17b"
            ),
            AdvertiseBasicEntity(
                id = "tCaTUuEEiIkXUbOIRCEh",
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/foobar-group-delivery-app.appspot.com/o/test_images%2Fsample_image_3.jpg?alt=media&token=4c5e8ace-aa3e-40e5-81c1-46597e128b9b"
            )
        )

        advertisesBasicDocs.forEach {
            firestore.collection("advertises_basic").document(it.id!!)
                .set(it)
                .await()
        }

        assertTrue(true)
    }

    @Test
    fun insert_suggests_basic_fake_data() = runBlocking {
        val suggestBasicDocs = listOf(
            SuggestBasicEntity(
                id = UUID.randomUUID().toString(),
                item_title = "Broccoli and buckwheat crepes",
                seller_name = "Happy Restaurant",
                image_url = "https://firebasestorage.googleapis.com/v0/b/foobar-group-delivery-app.appspot.com/o/test_images%2Fsample_image_3.jpg?alt=media&token=4c5e8ace-aa3e-40e5-81c1-46597e128b9b"
            ),
            SuggestBasicEntity(
                id = UUID.randomUUID().toString(),
                item_title = "Amchoor and pork vindaloo",
                seller_name = "Happy Restaurant",
                image_url = "https://firebasestorage.googleapis.com/v0/b/foobar-group-delivery-app.appspot.com/o/test_images%2Fsample_image_3.jpg?alt=media&token=4c5e8ace-aa3e-40e5-81c1-46597e128b9b"
            ),
            SuggestBasicEntity(
                id = UUID.randomUUID().toString(),
                item_title = "Raisin and sultana buns",
                seller_name = "Happy Restaurant",
                image_url = "https://firebasestorage.googleapis.com/v0/b/foobar-group-delivery-app.appspot.com/o/test_images%2Fsample_image_4.jpg?alt=media&token=ccc9f16a-f2f1-4c8e-865a-89daeb49121d"
            ),
            SuggestBasicEntity(
                id = UUID.randomUUID().toString(),
                item_title = "Bacon and buffalo skewers",
                seller_name = "Happy Restaurant",
                image_url = "https://firebasestorage.googleapis.com/v0/b/foobar-group-delivery-app.appspot.com/o/test_images%2Fsample_image_1.jpg?alt=media&token=4453e3c8-de2e-4863-8e3f-e9347f73c2a0"
            ),
            SuggestBasicEntity(
                id = UUID.randomUUID().toString(),
                item_title = "Sesame and samphire salad",
                seller_name = "Happy Restaurant",
                image_url = "https://firebasestorage.googleapis.com/v0/b/foobar-group-delivery-app.appspot.com/o/test_images%2Fsample_image_4.jpg?alt=media&token=ccc9f16a-f2f1-4c8e-865a-89daeb49121d"
            ),
            SuggestBasicEntity(
                id = UUID.randomUUID().toString(),
                item_title = "Delicata and chandeau salad",
                seller_name = "Happy Restaurant",
                image_url = "https://firebasestorage.googleapis.com/v0/b/foobar-group-delivery-app.appspot.com/o/test_images%2Fsample_image_2.jpg?alt=media&token=3c16ab78-aa1b-4c59-b918-efac5e69f17b"
            ),
            SuggestBasicEntity(
                id = UUID.randomUUID().toString(),
                item_title = "Broccoli and buckwheat crepes",
                seller_name = "Happy Restaurant",
                image_url = "https://firebasestorage.googleapis.com/v0/b/foobar-group-delivery-app.appspot.com/o/test_images%2Fsample_image_1.jpg?alt=media&token=4453e3c8-de2e-4863-8e3f-e9347f73c2a0"
            )
        )

        suggestBasicDocs.forEach {
            firestore.collection("users").document(myUserUid)
                .collection("suggests_basic").document(it.id!!)
                .set(it)
                .await()
        }

        assertTrue(true)
    }

    @Test
    fun insert_items_fake_data() = runBlocking {
        val jsonInputStream = InstrumentationRegistry.getInstrumentation()
            .context.assets
            .open("items_fake_data.json")
        val jsonString = jsonInputStream.bufferedReader().use { it.readText() }

        val itemObjs: List<ItemObj> = Json.decodeFromString(jsonString)

        itemObjs.map { it.toItemDoc() }
            .forEach {
                firestore.collection("items").document(it.id!!)
                    .set(it)
                    .await()
            }

        assertTrue(true)
    }

    @Test
    fun insert_items_basic_fake_data() = runBlocking {
        val jsonInputStream = InstrumentationRegistry.getInstrumentation()
            .context.assets
            .open("items_fake_data.json")
        val jsonString = jsonInputStream.bufferedReader().use { it.readText() }

        val itemObjs: List<ItemObj> = Json.decodeFromString(jsonString)

        itemObjs.map { it.toItemBasicDoc() }
            .forEach {
                firestore.collection("items_basic").document(it.id!!)
                    .set(it)
                    .await()
            }

        assertTrue(true)
    }
}

@Serializable
private data class ItemObj(
    val id: String? = null,
    val title: String? = null,
    val description: String? = null,
    val seller_id: String? = null,
    val catalog_id: String? = null,
    val price: Double? = null,
    val count: Int? = null,
    val image_url: String? = null
) {
    fun toItemDoc(): ItemDetailEntity {
        return ItemDetailEntity(id, title, description, seller_id, catalog_id, price, count, image_url)
    }

    fun toItemBasicDoc(): ItemBasicEntity {
        return ItemBasicEntity(id, title, description, catalog_id, price)
    }
}

@Serializable
private data class LocationObj(
    val lat: Double,
    val long: Double
)

@Serializable
private data class CatalogObj(
    val id: String,
    val name: String
)

@Serializable
private data class SellerObj(
    val id: String,
    val name: String,
    val description: String,
    val location: LocationObj,
    val address: String,
    val email: String,
    val phone_num: String,
    val image_url: String,
    val rating: Double,
    val catalogs: List<CatalogObj>,
    val min_spend: Double,
    val open_time: String,
    val close_time: String,
    val type: Int
) {
    fun toSellerDoc(): SellerDetailEntity {
        return SellerDetailEntity(id = id, name = name, description = description, email = email,
            phone_num = phone_num, location = GeoPoint(location.lat, location.long),
            address = address, image_url = image_url, open_time = open_time, close_time = close_time,
            min_spend = min_spend, rating = rating,
            catalogs = catalogs.map { mapOf("id" to it.id, "name" to it.name) },
            type = type
        )
    }

    fun toSellerBasicDoc(): SellerBasicEntity {
        return SellerBasicEntity(id, name, description, image_url, open_time, close_time, min_spend, rating, type)
    }
}