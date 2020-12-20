package com.foobarust.android.emulator

import androidx.test.ext.junit.rules.activityScenarioRule
import com.foobarust.android.InsertFakeDataActivity
import com.foobarust.data.common.Constants.USERS_COLLECTION
import com.foobarust.data.models.user.UserDetailEntity
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
        val userDetail = UserDetailEntity(
            username = "kthon",
            email = "kthon@connect.ust.hk"
        )

        firestore.document("$USERS_COLLECTION/${firebaseAuth.currentUser!!.uid}")
            .set(userDetail)
            .await()

        assertTrue(true)
    }
}