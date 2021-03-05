package com.foobarust.android.insert

import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.foobarust.android.InsertFakeDataActivity
import com.foobarust.data.constants.Constants.ITEM_CATEGORIES_COLLECTION
import com.foobarust.data.models.explore.ItemCategoryDto
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
class InsertItemCategoriesFakeData {

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
    fun insert_item_categories_fake_data() = runBlocking(Dispatchers.IO) {
        val serializedList: List<ItemCategorySerialized> = Json.decodeFromString(
            decodeJson(file = "item_categories_fake_data.json")
        )

        serializedList.forEach {
            val categoryId = it.id
            val entity = it.toItemCategoryDto()

            firestore.document("$ITEM_CATEGORIES_COLLECTION/$categoryId")
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
private data class ItemCategorySerialized(
    val id: String,
    val tag: String,
    val title: String,
    val title_zh: String,
    val image_url: String? = null
) {
    fun toItemCategoryDto(): ItemCategoryDto {
        return ItemCategoryDto(
            id = id,
            tag = tag,
            title = title,
            titleZh = title_zh,
            imageUrl = image_url
        )
    }
}