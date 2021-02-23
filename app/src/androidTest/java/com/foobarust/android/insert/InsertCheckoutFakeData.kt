package com.foobarust.android.insert

import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.foobarust.android.InsertFakeDataActivity
import com.foobarust.data.constants.Constants.PAYMENT_METHODS_COLLECTION
import com.foobarust.data.models.checkout.PaymentMethodDto
import com.google.firebase.firestore.FirebaseFirestore
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
import javax.inject.Inject

/**
 * Created by kevin on 1/9/21
 */

@HiltAndroidTest
class InsertCheckoutFakeData {

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
    fun insert_payment_methods_fake_data() = runBlocking(Dispatchers.IO) {
        val serializedList: List<PaymentMethodSerialized> = Json.decodeFromString(
            decodeJson(file = "payment_methods_fake_data.json")
        )

        serializedList.forEach {
            val methodId = it.id
            val entity = it.toPaymentMethodEntity()

            firestore.document("$PAYMENT_METHODS_COLLECTION/$methodId")
                .set(entity)
                .await()
        }
    }

    private fun decodeJson(file: String): String {
        val jsonInputStream = InstrumentationRegistry.getInstrumentation()
            .context.assets
            .open(file)

        return jsonInputStream.bufferedReader().use { it.readText() }
    }
}

@Serializable
private data class PaymentMethodSerialized(
    val id: String,
    val identifier: String,
    val enabled: Boolean
) {
    fun toPaymentMethodEntity(): PaymentMethodDto {
        return PaymentMethodDto(
            id = id,
            identifier = identifier,
            enabled = enabled
        )
    }
}