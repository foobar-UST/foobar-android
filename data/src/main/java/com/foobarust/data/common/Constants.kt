package com.foobarust.data.common

/**
 * Created by kevin on 9/25/20
 */

object Constants {

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
    const val USER_DATA_COMPLETED_FIELD = "data_completed"
    const val USER_ROLES_FIELD = "roles"
    const val USER_UPDATED_AT_FIELD = "update_at"

    // Cart
    const val USER_CART_ITEMS_SUB_COLLECTION = "cart_items"

    const val USER_CART_ITEMS_ID_FIELD = "id"
    const val USER_CART_ITEMS_ITEM_ID_FIELD = "item_id"
    const val USER_CART_ITEMS_ITEM_TITLE_FIELD = "item_title"
    const val USER_CART_ITEMS_ITEM_TITLE_ZH_FIELD = "item_title_zh"
    const val USER_CART_ITEMS_ITEM_PRICE_FIELD = "item_price"
    const val USER_CART_ITEMS_ITEM_IMAGE_URL_FIELD = "item_image_url"
    const val USER_CART_ITEMS_AMOUNTS_FIELD = "amounts"
    const val USER_CART_ITEMS_TOTAL_PRICE_FIELD = "total_price"
    const val USER_CART_ITEMS_NOTES_FIELD = "notes"
    const val USER_CART_ITEMS_UPDATED_AT_FIELD = "updated_at"

    // Sellers
    const val SELLERS_COLLECTION = "sellers"
    const val SELLERS_BASIC_COLLECTION = "sellers_basic"
    const val SELLERS_CATALOGS_SUB_COLLECTION = "catalogs"

    const val SELLER_ID_FIELD = "id"
    const val SELLER_NAME_FIELD = "name"
    const val SELLER_NAME_ZH_FIELD = "name_zh"
    const val SELLER_DESCRIPTION_FIELD = "description"
    const val SELLER_DESCRIPTION_ZH_FIELD = "description_zh"
    const val SELLER_IMGAE_URL_FIELD = "image_url"
    const val SELLER_OPENING_HOURS_FIELD = "opening_hours"
    const val SELLER_MIN_SPEND_FIELD = "min_spend"
    const val SELLER_RATING_FIELD = "rating"
    const val SELLER_RATING_COUNT_FIELD = "rating_count"
    const val SELLER_TYPE_FIELD = "type"
    const val SELLER_WEBSITE_FIELD = "website"
    const val SELLER_PHONE_NUM_FIELD = "phone_num"
    const val SELLER_LOCATION_FIELD = "location"
    const val SELLER_ONLINE_FIELD = "online"
    const val SELLER_NOTICE_FIELD = "notice"
    const val SELLER_TAGS_FIELD = "tags"

    // Seller Location
    const val SELLER_LOCATION_ADDRESS_FIELD = "address"
    const val SELLER_LOCATION_ADDRESS_ZH_FIELD = "address_zh"
    const val SELLER_LOCATION_GEOPOINT_FIELD = "geopoint"

    // Seller Catalog
    const val SELLER_CATALOG_ID_FIELD = "id"
    const val SELLER_CATALOG_TITLE_FIELD = "title"
    const val SELLER_CATALOG_TITLE_ZH_FIELD = "title_zh"
    const val SELLER_CATALOG_AVAILABLE_FIELD = "available"
    const val SELLER_CATALOG_UPDATED_AT_FIELD = "updated_at"

    // Catalog Schedule
    const val CATALOG_SCHEDULE_START_TIME_FIELD = "start_time"
    const val CATALOG_SCHEDULE_END_TIME_FIELD = "end_time"

    // Advertise
    const val ADVERTISES_COLLECTION = "advertises"
    const val ADVERTISES_BASIC_COLLECTION = "advertises_basic"

    const val ADVERTISE_ID_FIELD = "id"
    const val ADVERTISE_URL_FIELD = "url"
    const val ADVERTISE_IMAGE_URL_FIELD = "image_url"

    const val SUGGESTS_BASIC_COLLECTION = "suggests_basic"
    const val SUGGESTS_BASIC_ID_FIELD = "id"
    const val SUGGESTS_BASIC_ITEM_ID_FIELD = "item_id"
    const val SUGGESTS_BASIC_ITEM_TITLE_FIELD = "item_title"
    const val SUGGESTS_BASIC_SELLER_NAME_FIELD = "seller_name"
    const val SUGGESTS_BASIC_IMAGE_URL_FIELD = "image_url"

    // Seller Items
    const val SELLER_ITEMS_SUB_COLLECTION = "items"
    const val SELLER_ITEMS_BASIC_SUB_COLLECTION = "items_basic"

    const val SELLER_ITEM_ID_FIELD = "id"
    const val SELLER_ITEM_TITLE_FIELD = "title"
    const val SELLER_ITEM_TITLE_ZH_FIELD = "title_zh"
    const val SELLER_ITEM_CATALOG_ID_FIELD = "catalog_id"
    const val SELLER_ITEM_DESCRIPTION_FIELD = "description"
    const val SELLER_ITEM_DESCRIPTION_ZH_FIELD = "description_zh"
    const val SELLER_ITEM_IMAGE_URL_FIELD = "image_url"
    const val SELLER_ITEM_PRICE_FIELD = "price"
    const val SELLER_ITEM_COUNT_FIELD = "count"
    const val SELLER_ITEM_AVAILABLE_FIELD = "available"
    const val SELLER_ITEM_UPDATED_AT_FIELD = "updated_at"
}