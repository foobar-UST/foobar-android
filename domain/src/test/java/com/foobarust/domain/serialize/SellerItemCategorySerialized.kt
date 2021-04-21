package com.foobarust.domain.serialize

import com.foobarust.domain.models.explore.ItemCategory
import kotlinx.serialization.Serializable

/**
 * Created by kevin on 4/20/21
 */

@Serializable
internal data class ItemCategorySerialized(
    val id: String,
    val tag: String,
    val title: String,
    val title_zh: String,
    val image_url: String? = null
)

internal fun ItemCategorySerialized.toItemCategory(): ItemCategory {
    return ItemCategory(
        id = id, tag = tag, title = title, titleZh = title_zh, imageUrl = image_url
    )
}