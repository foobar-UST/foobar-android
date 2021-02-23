package com.foobarust.android.insert

import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.foobarust.android.InsertFakeDataActivity
import com.foobarust.data.constants.Constants.SELLERS_COLLECTION
import com.foobarust.data.constants.Constants.SELLER_ADVERTISES_SUB_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
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
    fun insert_advertises_test_data() = runBlocking(Dispatchers.IO) {
        val jsonInputStream = InstrumentationRegistry.getInstrumentation()
            .context.assets
            .open("advertises_fake_data.json")
        val jsonString = jsonInputStream.bufferedReader().use { it.readText() }

        val serializedList: List<AdvertiseDetailSerialized> = Json.decodeFromString(jsonString)

        serializedList.forEach {
            firestore.document(
                "$SELLERS_COLLECTION/${it.seller_id}/$SELLER_ADVERTISES_SUB_COLLECTION/0"
            )
                .set(mapOf(
                    "seller_id" to it.seller_id,
                    "title" to it.title,
                    "title_zh" to it.title_zh,
                    "content" to it.content,
                    "content_zh" to it.content_zh,
                    "image_url" to it.image_url,
                    "url" to it.url,
                    "seller_type" to it.seller_type,
                    "created_at" to FieldValue.serverTimestamp()
                ))
                .await()
        }

        assertTrue(true)
    }
}


@Serializable
private data class AdvertiseDetailSerialized(
    val seller_id: String,
    val title: String,
    val title_zh: String? = null,
    val content: String,
    val content_zh: String? = null,
    val image_url: String? = null,
    val seller_type: Int,
    val url: String
)