package com.foobarust.data.common

/**
 * Created by kevin on 9/25/20
 */

object Constants {

    // Common
    const val UPDATED_AT_FIELD = "updated_at"

    // SharedPreferences
    const val PREFS_NAME = "foobarust"

    // Users
    const val USERS_COLLECTION = "users"
    const val USERS_DELIVERY_COLLECTION = "users_delivery"
    const val USERS_PUBLIC_COLLECTION = "users_public"

    const val USER_PHOTOS_STORAGE_FOLDER = "user_photos"

    const val USER_USERNAME_FIELD = "username"
    const val USER_EMAIL_FIELD = "email"
    const val USER_NAME_FIELD = "name"
    const val USER_PHOTO_URL_FIELD = "photo_url"
    const val USER_PHONE_NUM_FIELD = "phone_num"
    const val USER_ALLOW_ORDER_FIELD = "allow_order"
    const val USER_ROLES_FIELD = "user"

    // Sellers
    const val SELLERS_COLLECTION = "sellers"
    const val SELLERS_BASIC_COLLECTION = "sellers_basic"

    const val SELLER_ID_FIELD = "id"
    const val SELLER_NAME_FIELD = "name"
    const val SELLER_DESCRIPTION_FIELD = "description"
    const val SELLER_IMGAE_URL_FIELD = "image_url"
    const val SELLER_OPENING_HOURS_FIELD = "opening_hours"
    const val SELLER_MIN_SPEND_FIELD = "min_spend"
    const val SELLER_RATING_FIELD = "rating"
    const val SELLER_TYPE_FIELD = "type"
    const val SELLER_EMAIL_FIELD = "email"
    const val SELLER_PHONE_NUM_FIELD = "phone_num"
    const val SELLER_LOCATION_FIELD = "location"
    const val SELLER_LOCATION_ADDRESS_FIELD = "address"
    const val SELLER_LOCATION_GEOPOINT_FIELD = "geopoint"
    const val SELLER_CATALOGS_FIELD = "catalogs"
    const val SELLER_ONLINE_FIELD = "online"
    const val SELLER_NOTICE_FIELD = "notice"

    // Catalog
    const val SELLER_CATALOG_ID_FIELD = "id"
    const val SELLER_CATALOG_NAME_FIELD = "name"
    const val SELLER_CATALOG_AVAILABLE_FIELD = "available"
    const val SELLER_CATALOG_START_TIME_FIELD = "start_time"
    const val SELLER_CATALOG_END_TIME_FIELD = "end_time"

    // Advertise
    const val ADVERTISES_COLLECTION = "advertises"
    const val ADVERTISES_BASIC_COLLECTION = "advertises_basic"

    const val ADVERTISE_ID_FIELD = "id"
    const val ADVERTISE_SELLER_ID_FIELD = "seller_id"
    const val ADVERTISE_SELLER_NAME_FIELD = "seller_name"
    const val ADVERTISE_TITLE_FIELD = "title"
    const val ADVERTISE_CONTENT_FIELD = "content"
    const val ADVERTISE_TYPE_FIELD = "type"
    const val ADVERTISE_CREATED_AT_FIELD = "created_at"
    const val ADVERTISE_IMAGE_URL_FIELD = "image_url"

    const val SUGGESTS_BASIC_COLLECTION = "suggests_basic"
    const val SUGGESTS_BASIC_ID_FIELD = "id"
    const val SUGGESTS_BASIC_ITEM_ID_FIELD = "item_id"
    const val SUGGESTS_BASIC_ITEM_TITLE_FIELD = "item_title"
    const val SUGGESTS_BASIC_SELLER_NAME_FIELD = "seller_name"
    const val SUGGESTS_BASIC_IMAGE_URL_FIELD = "image_url"


    // Item
    const val SELLER_ITEMS_COLLECTION = "items"
    const val SELLER_ITEMS_BASIC_COLLECTION = "items_basic"

    const val SELLER_ITEMS_ID_FIELD = "id"
    const val SELLER_ITEMS_TITLE_FIELD = "title"
    const val SELLER_ITEMS_DESCRIPTION_FIELD = "description"
    const val SELLER_ITEMS_SELLER_ID_FIELD = "seller_id"
    const val SELLER_ITEMS_CATALOG_ID_FIELD = "catalog_id"
    const val SELLER_ITEMS_PRICE_FIELD = "price"
    const val SELLER_ITEMS_COUNT_FIELD = "count"
    const val SELLER_ITEMS_IMAGE_URL_FIELD = "image_url"
    const val SELLER_ITEMS_CHOICES_FIELD = "choices"
    const val SELLER_ITEMS_EXTRA_ITEMS_FIELD = "extra_items"
    const val SELLER_ITEMS_AVAILABLE_FIELD = "available"

    const val ITEMS_CHOICE_ID_FIELD = "id"
    const val ITEMS_CHOICE_TITLE_FIELD = "title"
    const val ITEMS_CHOICE_EXTRA_PRICE_FIELD = "extra_price"

    const val EXTRA_ITEM_ID_FIELD = "id"
    const val EXTRA_ITEM_TITLE_FIELD = "title"
    const val EXTRA_ITEM_PRICE_FIELD = "price"
}