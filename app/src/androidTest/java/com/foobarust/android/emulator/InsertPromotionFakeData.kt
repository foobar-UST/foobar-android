package com.foobarust.android.emulator

import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.foobarust.android.InsertFakeDataActivity
import com.foobarust.data.common.Constants.ADVERTISES_BASIC_COLLECTION
import com.foobarust.data.common.Constants.SUGGESTS_BASIC_COLLECTION
import com.foobarust.data.common.Constants.USERS_COLLECTION
import com.foobarust.data.models.promotion.AdvertiseBasicEntity
import com.foobarust.data.models.promotion.SuggestBasicEntity
import com.google.firebase.auth.FirebaseAuth
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
class InsertPromotionFakeData {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    @get:Rule
    val activityScenarioRule = activityScenarioRule<InsertFakeDataActivity>()

    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    @Inject
    lateinit var firestore: FirebaseFirestore

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun insert_advertises_basic_test_data() = runBlocking(Dispatchers.IO) {
        val jsonInputStream = InstrumentationRegistry.getInstrumentation()
            .context.assets
            .open("advertises_basic_fake_data.json")
        val jsonString = jsonInputStream.bufferedReader().use { it.readText() }

        val advertiseBasicSerializedList: List<AdvertiseBasicSerialized> = Json.decodeFromString(jsonString)

        advertiseBasicSerializedList.map { it.toAdvertiseBasic() }
            .forEach {
                firestore.collection(ADVERTISES_BASIC_COLLECTION).document(it.id!!)
                    .set(it)
                    .await()
            }

        assertTrue(true)
    }

    @Test
    fun insert_suggests_basic_test_data() = runBlocking(Dispatchers.IO) {
        val jsonInputStream = InstrumentationRegistry.getInstrumentation()
            .context.assets
            .open("suggests_basic_fake_data.json")
        val jsonString = jsonInputStream.bufferedReader().use { it.readText() }

        val suggestBasicSerializedList: List<SuggestBasicSerialized> = Json.decodeFromString(jsonString)

        suggestBasicSerializedList.map { it.toSuggestBasic() }
            .forEach {
                firestore.collection(USERS_COLLECTION).document(firebaseAuth.currentUser!!.uid)
                    .collection(SUGGESTS_BASIC_COLLECTION).document(it.id!!)
                    .set(it)
                    .await()
            }

        assertTrue(true)
    }
}


@Serializable
private data class AdvertiseBasicSerialized(
    val id: String,
    val url: String,
    val image_url: String
) {
    fun toAdvertiseBasic(): AdvertiseBasicEntity {
        return AdvertiseBasicEntity(
            id = id,
            url = url,
            imageUrl = image_url
        )
    }
}

@Serializable
private data class SuggestBasicSerialized(
    val id: String,
    val item_id: String,
    val item_title: String,
    val seller_name: String,
    val image_url: String
) {
    fun toSuggestBasic(): SuggestBasicEntity {
        return SuggestBasicEntity(
            id = id,
            itemId = item_id,
            itemTitle = item_title,
            sellerName = seller_name,
            imageUrl = image_url
        )
    }
}