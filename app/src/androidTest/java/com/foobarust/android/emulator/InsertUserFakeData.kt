package com.foobarust.android.emulator

import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
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
    fun insert_current_user_fake_data() = runBlocking(Dispatchers.IO) {
        val userDetail = UserDetailEntity(
            id = firebaseAuth.currentUser!!.uid,
            username = "kthon",
            email = "kthon@connect.ust.hk"
        )

        firestore.document("$USERS_COLLECTION/${firebaseAuth.currentUser!!.uid}")
            .set(userDetail)
            .await()

        assertTrue(true)
    }

    @Test
    fun insert_other_users_fake_data() = runBlocking(Dispatchers.IO) {
        val serializedList: List<UserSerialized> = Json.decodeFromString(decodeUsersJson())
        serializedList
            .map { it.toUserDetailEntity() }
            .forEach {
                firestore.document("$USERS_COLLECTION/${it.id}")
                    .set(it).await()
        }
    }

    private fun decodeUsersJson(): String {
        val jsonInputStream = InstrumentationRegistry.getInstrumentation()
            .context.assets
            .open("users_fake_data.json")

        return jsonInputStream.bufferedReader().use { it.readText() }
    }
}

@Serializable
private data class UserSerialized(
    val id: String,
    val username: String,
    val email: String,
    val name: String? = null,
    val photo_num: String? = null,
    val photo_url: String? = null,
    val roles: List<String>
) {
    fun toUserDetailEntity(): UserDetailEntity {
        return UserDetailEntity(
            id = id,
            name = name,
            username = username,
            email = email,
            phoneNum = photo_num,
            photoUrl = photo_url,
            roles = roles
        )
    }
}