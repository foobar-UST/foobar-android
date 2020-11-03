package com.foobarust.android.emulator

import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.foobarust.android.InsertFakeDataActivity
import com.foobarust.data.common.Constants.SELLERS_BASIC_COLLECTION
import com.foobarust.data.common.Constants.SELLERS_COLLECTION
import com.foobarust.data.models.SellerBasicEntity
import com.foobarust.data.models.SellerCatalogEntity
import com.foobarust.data.models.SellerDetailEntity
import com.foobarust.data.models.SellerLocationEntity
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
        val jsonInputStream = InstrumentationRegistry.getInstrumentation()
            .context.assets
            .open("sellers_fake_data.json")
        val jsonString = jsonInputStream.bufferedReader().use { it.readText() }

        val sellerSerializedList: List<SellerSerialized> = Json.decodeFromString(jsonString)

        sellerSerializedList.map { it.toSellerDoc() }
            .forEach {
                firestore.collection(SELLERS_COLLECTION).document(it.id!!)
                    .set(it)
                    .await()
            }

        assertTrue(true)
    }

    @Test
    fun insert_sellers_basic_fake_data() = runBlocking(Dispatchers.IO) {
        val jsonInputStream = InstrumentationRegistry.getInstrumentation()
            .context.assets
            .open("sellers_fake_data.json")
        val jsonString = jsonInputStream.bufferedReader().use { it.readText() }

        val sellerSerializedList: List<SellerSerialized> = Json.decodeFromString(jsonString)

        sellerSerializedList.map { it.toSellerBasicDoc() }
            .forEach {
                firestore.collection(SELLERS_BASIC_COLLECTION).document(it.id!!)
                    .set(it)
                    .await()
            }

        assertTrue(true)
    }
}

@Serializable
private data class SellerSerialized(
    val id: String,
    val name: String,
    val description: String,
    val location: LocationSerialized,
    val email: String,
    val phone_num: String,
    val image_url: String?,
    val rating: Double,
    val catalogs: List<CatalogSerialized>,
    val min_spend: Double,
    val opening_hours: String,
    val type: Int,
    val online: Boolean,
    val notice: String? = null
) {
    fun toSellerDoc(): SellerDetailEntity {
        val location = SellerLocationEntity(
            address = this.location.address,
            geoPoint = GeoPoint(this.location.geopoint.lat, this.location.geopoint.long)
        )

        return SellerDetailEntity(id = id,
            name = name, description = description, email = email,
            phone_num = phone_num,
            location = location,
            image_url = image_url,
            min_spend = min_spend, rating = rating,
            catalogs = catalogs.map {
                SellerCatalogEntity(
                    id = it.id, name = it.name,
                    available = it.available, startTime = it.start_time, endTime = it.end_time
                )
            },
            type = type,
            online = online,
            openingHours = opening_hours
        )
    }

    fun toSellerBasicDoc(): SellerBasicEntity {
        return SellerBasicEntity(id = id,
            name = name, imageUrl = image_url,
            description = description, rating = rating,
            type = type, online = online, minSpend = min_spend)
    }
}

@Serializable
private data class LocationSerialized(
    val address: String,
    val geopoint: GeoPointSerialized
)

@Serializable
private data class GeoPointSerialized(
    val lat: Double,
    val long: Double
)

@Serializable
private data class CatalogSerialized(
    val id: String,
    val name: String,
    val available: Boolean,
    val start_time: String? = null,
    val end_time: String? = null
)

