package com.foobarust.android.emulator

import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.foobarust.android.InsertFakeDataActivity
import com.foobarust.data.common.Constants.SELLERS_COLLECTION
import com.foobarust.data.common.Constants.SELLER_SECTIONS_SUB_COLLECTION
import com.foobarust.data.models.seller.GeolocationDto
import com.foobarust.data.models.seller.SellerSectionBasicDto
import com.foobarust.data.models.seller.SellerSectionDetailDto
import com.google.firebase.Timestamp
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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Created by kevin on 12/22/20
 */

@HiltAndroidTest
class InsertSellerSectionFakeData {

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
    fun insert_seller_sections_fake_data() = runBlocking(Dispatchers.IO) {
        val serializedList: List<SellerSectionSerialized> = Json.decodeFromString(
            decodeSectionJson()
        )

        serializedList
            .mapIndexed { index, sectionSerialized ->
                sectionSerialized.toSellerSectionDetailEntity(index)
            }
            .forEach {
                firestore.document(
                    "${SELLERS_COLLECTION}/${it.sellerId}/${SELLER_SECTIONS_SUB_COLLECTION}/${it.id}"
                ).set(it).await()
            }
    }

    /*
    @Test
    fun insert_seller_sections_basic_fake_data() = runBlocking(Dispatchers.IO) {
        val serializedList: List<SellerSectionSerialized> = Json.decodeFromString(
            decodeSectionJson()
        )

        serializedList
            .mapIndexed { index, sectionSerialized ->
                sectionSerialized.toSellerSectionBasicEntity(index)
            }
            .forEach {
                firestore.document(
                    "${SELLERS_COLLECTION}/${it.sellerId}/${SELLER_SECTIONS_BASIC_SUB_COLLECTION}/${it.id}"
                ).set(it).await()
            }
    }

     */

    private fun decodeSectionJson(): String {
        val jsonInputStream = InstrumentationRegistry.getInstrumentation()
            .context.assets
            .open("seller_sections_fake_data.json")

        return jsonInputStream.bufferedReader().use { it.readText() }
    }
}

@Serializable
private data class SellerSectionSerialized(
    val id: String,
    val title: String,
    val title_zh: String? = null,
    val group_id: String,
    val seller_id: String,
    val seller_name: String,
    val seller_name_zh: String? = null,
    val delivery_cost: Double,
    val delivery_time: String,
    val delivery_location: GeolocationSerialized,
    val description: String,
    val description_zh: String? = null,
    val cutoff_time: String,
    val max_users: Int,
    val joined_users_count: Int,
    val joined_users_ids: List<String> = emptyList(),
    val image_url: String? = null,
    val state: String,
    val available: Boolean
) {
    fun toSellerSectionDetailEntity(index: Int): SellerSectionDetailDto {
        val deliveryLocation = GeolocationDto(
            address = delivery_location.address,
            addressZh = delivery_location.address_zh,
            geoPoint = GeoPoint(delivery_location.geopoint.lat, delivery_location.geopoint.long)
        )

        return SellerSectionDetailDto(
            id = id,
            title = title,
            titleZh = title_zh,
            groupId = group_id,
            sellerId = seller_id,
            sellerName = seller_name,
            sellerNameZh = seller_name_zh,
            description = description,
            descriptionZh = description_zh,
            deliveryCost = delivery_cost,
            deliveryTime = parseTimestamp(delivery_time, index),
            deliveryLocation = deliveryLocation,
            cutoffTime = parseTimestamp(cutoff_time, index),
            maxUsers = max_users,
            joinedUsersCount = joined_users_count,
            joinedUsersIds = joined_users_ids,
            imageUrl = image_url,
            state = state,
            available = available
        )
    }

    fun toSellerSectionBasicEntity(index: Int): SellerSectionBasicDto {
        return SellerSectionBasicDto(
            id = id,
            title = title,
            titleZh = title_zh,
            sellerId = seller_id,
            sellerName = seller_name,
            sellerNameZh = seller_name_zh,
            deliveryTime = parseTimestamp(delivery_time, index),
            cutoffTime = parseTimestamp(cutoff_time, index),
            maxUsers = max_users,
            joinedUsersCount = joined_users_count,
            imageUrl = image_url,
            state = state,
            available = available
        )
    }

    private fun parseTimestamp(time: String, index: Int): Timestamp {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
        formatter.timeZone = TimeZone.getTimeZone("Asia/Hong_Kong")

        val inputCalendar = Calendar.getInstance().apply {
            timeZone = TimeZone.getTimeZone("Asia/Hong_Kong")
            setTime(formatter.parse(time)!!)
        }

        val newCalendar = Calendar.getInstance().apply {
            timeZone = TimeZone.getTimeZone("Asia/Hong_Kong")
            set(Calendar.HOUR_OF_DAY, inputCalendar.get(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, inputCalendar.get(Calendar.MINUTE))
            add(Calendar.DATE, index)
        }

        return Timestamp(newCalendar.time)
    }
}
