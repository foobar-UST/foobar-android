package com.foobarust.data.models

/**
 * Created by kevin on 10/3/20
 *
 * Represent a document structure in '/users/suggests_basic' sub-collection.
 */

// TODO: refactor suggest basic
data class SuggestBasicEntity(
    val id: String? = null,
    val item_title: String? = null,
    val seller_name: String? = null,
    val image_url: String? = null
)