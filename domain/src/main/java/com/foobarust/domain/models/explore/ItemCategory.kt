package com.foobarust.domain.models.explore

/**
 * Created by kevin on 2/26/21
 */

data class ItemCategory(
    val id: String,
    val tag: String,
    val title: String,
    val titleZh: String?,
    val imageUrl: String?
)

fun ItemCategory.getNormalizedTitle(): String {
    return if (titleZh != null) "$title $titleZh" else title
}