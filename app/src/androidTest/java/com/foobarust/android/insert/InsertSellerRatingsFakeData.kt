package com.foobarust.android.insert

import androidx.test.ext.junit.rules.activityScenarioRule
import com.foobarust.android.InsertFakeDataActivity
import com.foobarust.data.constants.Constants.SELLERS_COLLECTION
import com.foobarust.data.constants.Constants.SELLER_RATINGS_SUB_COLLECTION
import com.foobarust.data.constants.Constants.SELLER_RATING_CREATED_AT_FIELD
import com.foobarust.data.constants.Constants.SELLER_RATING_DELIVERY_RATING_FIELD
import com.foobarust.data.constants.Constants.SELLER_RATING_ID_FIELD
import com.foobarust.data.constants.Constants.SELLER_RATING_ORDER_ID_FIELD
import com.foobarust.data.constants.Constants.SELLER_RATING_ORDER_RATING_FIELD
import com.foobarust.data.constants.Constants.SELLER_RATING_USERNAME_FIELD
import com.foobarust.data.constants.Constants.SELLER_RATING_USER_PHOTO_URL_FIELD
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*
import javax.inject.Inject
import kotlin.random.Random

/**
 * Created by kevin on 3/3/21
 */

@HiltAndroidTest
class InsertSellerRatingsFakeData {

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
    fun insert_on_campus_ratings_fake_data() = runBlocking(Dispatchers.IO) {
        val uuids = listOf(
            "ee301ae7-4832-4fbe-99b5-0a6a6a752ade",
            "13a40ac2-1100-459d-be70-33724e0da25e",
            "132331e6-9e98-4ade-bf13-f21f9a18643d"
        )

        repeat(3) {
            val newRatingId = uuids[it]
            val newRating = mapOf(
                SELLER_RATING_ID_FIELD to newRatingId,
                SELLER_RATING_USERNAME_FIELD to "kthon",
                SELLER_RATING_USER_PHOTO_URL_FIELD to "https://images.pexels.com/photos/207962/pexels-photo-207962.jpeg?cs=srgb&dl=pexels-pixabay-207962.jpg&fm=jpg",
                SELLER_RATING_ORDER_ID_FIELD to UUID.randomUUID().toString(),
                SELLER_RATING_ORDER_RATING_FIELD to generateRandomOrderRating(),
                SELLER_RATING_CREATED_AT_FIELD to Timestamp.now()
            )

            val sellerId = "5f71cda20110962a4b772122"

            firestore.document(
                "$SELLERS_COLLECTION/$sellerId/$SELLER_RATINGS_SUB_COLLECTION/$newRatingId"
            )
                .set(newRating)
                .await()
        }
    }

    @Test
    fun insert_off_campus_rating_fake_data() = runBlocking(Dispatchers.IO) {
        val uuids = listOf(
            "8182bdf5-697b-40d7-a9c1-208a07f0f3b3",
            "f4bc7be8-5a65-49c0-b954-620fa51e21b8",
            "5aab8b90-ee1c-4847-ada2-3dc9e8d7aaa1"
        )

        repeat(3) {
            val newRatingId = uuids[it]
            val newRating = mapOf(
                SELLER_RATING_ID_FIELD to newRatingId,
                SELLER_RATING_USERNAME_FIELD to firebaseAuth.currentUser!!.uid,
                SELLER_RATING_USER_PHOTO_URL_FIELD to "https://images.pexels.com/photos/207962/pexels-photo-207962.jpeg?cs=srgb&dl=pexels-pixabay-207962.jpg&fm=jpg",
                SELLER_RATING_ORDER_ID_FIELD to UUID.randomUUID().toString(),
                SELLER_RATING_ORDER_RATING_FIELD to generateRandomOrderRating(),
                SELLER_RATING_DELIVERY_RATING_FIELD to generateRandomDeliveryRating(),
                SELLER_RATING_CREATED_AT_FIELD to Timestamp.now()
            )

            val sellerId = "kZr4pBjju7gQniYGS0kN"

            firestore.document(
                "$SELLERS_COLLECTION/$sellerId/$SELLER_RATINGS_SUB_COLLECTION/$newRatingId"
            )
                .set(newRating)
                .await()
        }
    }

    private fun generateRandomOrderRating(): Int {
        return Random.nextInt(1, 6)
    }

    private fun generateRandomDeliveryRating(): Boolean {
        return Random.nextBoolean()
    }
}