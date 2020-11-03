package com.foobarust.android.emulator

import androidx.test.ext.junit.rules.activityScenarioRule
import com.foobarust.android.InsertFakeDataActivity
import com.foobarust.data.common.Constants.USERS_COLLECTION
import com.foobarust.data.models.UserDetailEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

/**
 * Created by kevin on 10/12/20
 */

@HiltAndroidTest
class InsertUserFakeData {

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
    fun insert_users_fake_data() = runBlocking(Dispatchers.IO) {
        firestore.collection(USERS_COLLECTION).document(firebaseAuth.currentUser!!.uid)
            .set(UserDetailEntity(
                name = "Kevin Hon",
                email = "kthon@connect.ust.hk",
                username = "kthon",
                phoneNum = "67681436",
                photoUrl = "https://firebasestorage.googleapis.com/v0/b/foobar-group-delivery-app.appspot.com/o/user_photos%2FfFFdrdmz9zeyw7rWNjhrJaXnVOh2?alt=media&token=3dad277a-bd1a-402a-87e0-d557bc5dee07",
            ))
            .await()

        assertTrue(true)
    }
}