package com.foobarust.data.common

/**
 * Created by kevin on 9/25/20
 */

object Constants {
    // Shared Preferences
    const val PREFS_NAME = "foobarust"

    // Users
    const val USERS_COLLECTION = "users"
    const val USERS_DELIVERY_COLLECTION = "users_delivery"
    const val USERS_PUBLIC_COLLECTION = "users_public"

    const val USER_PHOTOS_STORAGE_FOLDER = "user_photos"

    const val USER_ID_FIELD = "id"
    const val USER_USERNAME_FIELD = "username"
    const val USER_EMAIL_FIELD = "email"
    const val USER_NAME_FIELD = "name"
    const val USER_PHOTO_URL_FIELD = "photo_url"
    const val USER_PHONE_NUM_FIELD = "phone_num"
    const val USER_ROLES_FIELD = "roles"
    const val USER_UPDATED_AT_FIELD = "updated_at"
    const val USER_CREATED_REST_FIELD = "createdRest"

    // Cart
    const val USER_CARTS_COLLECTION = "user_carts"
    const val USER_CART_ITEMS_SUB_COLLECTION = "cart_items"

    const val USER_CART_SELLER_ID_FIELD = "seller_id"
    const val USER_CART_SELLER_TYPE_FIELD = "seller_type"
    const val USER_CART_SELLER_SECTION_ID_FIELD = "section_id"
    const val USER_CART_ITEMS_COUNT_FIELD = "items_count"
    const val USER_CART_SUBTOTAL_COST_FIELD = "subtotal_cost"
    const val USER_CART_DELIVERY_COST_FIELD = "delivery_cost"
    const val USER_CART_TOTAL_COST_FIELD = "total_cost"
    const val USER_CART_SYNC_REQUIRED_FIELD = "sync_required"
    const val USER_CART_UPDATED_AT_FIELD = "updated_at"

    const val USER_CART_ITEMS_ID_FIELD = "id"
    const val USER_CART_ITEMS_ITEM_ID_FIELD = "item_id"
    const val USER_CART_ITEMS_ITEM_SELLER_ID_FIELD = "item_seller_id"
    const val USER_CART_ITEMS_ITEM_SECTION_ID_FIELD = "item_section_id"
    const val USER_CART_ITEMS_ITEM_TITLE_FIELD = "item_title"
    const val USER_CART_ITEMS_ITEM_TITLE_ZH_FIELD = "item_title_zh"
    const val USER_CART_ITEMS_ITEM_PRICE_FIELD = "item_price"
    const val USER_CART_ITEMS_ITEM_IMAGE_URL_FIELD = "item_image_url"
    const val USER_CART_ITEMS_AMOUNTS_FIELD = "amounts"
    const val USER_CART_ITEMS_TOTAL_PRICE_FIELD = "total_price"
    const val USER_CART_ITEMS_AVAILABLE_FIELD = "available"
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
    const val SELLER_IMAGE_URL_FIELD = "image_url"
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
    const val SELLER_BY_USER_ID = "by_user_id"
    const val SELLER_DELIVERY_COST = "delivery_cost"

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

    // Seller Section
    const val SELLER_SECTIONS_SUB_COLLECTION = "sections"
    const val SELLER_SECTIONS_BASIC_SUB_COLLECTION = "sections_basic"

    const val SELLER_SECTION_ID_FIELD = "id"
    const val SELLER_SECTION_TITLE_FIELD = "title"
    const val SELLER_SECTION_TITLE_ZH_FIELD = "title_zh"
    const val SELLER_SECTION_GROUP_ID_FIELD = "group_id"
    const val SELLER_SECTION_SELLER_ID_FIELD = "seller_id"
    const val SELLER_SECTION_SELLER_NAME_FIELD = "seller_name"
    const val SELLER_SECTION_SELLER_NAME_ZH_FIELD = "seller_name_zh"
    const val SELLER_SECTION_DELIVERY_TIME_FIELD = "delivery_time"
    const val SELLER_SECTION_DELIVERY_LOCATION_FIELD = "delivery_location"
    const val SELLER_SECTION_DELIVERY_LOCATION__ZH_FIELD = "delivery_location_zh"
    const val SELLER_SECTION_CUTOFF_TIME_FIELD = "cutoff_time"
    const val SELLER_SECTION_DESCRIPTION_FIELD = "description"
    const val SELLER_SECTION_DESCRIPTION_ZH_FIELD = "description_zh"
    const val SELLER_SECTION_MAX_USERS_FIELD = "max_users"
    const val SELLER_SECTION_JOINED_USERS_COUNT_FIELD = "joined_users_count"
    const val SELLER_SECTION_JOINED_USERS_IDS_FIELD = "joined_users_ids"
    const val SELLER_SECTION_IMAGE_URL_FIELD = "image_url"
    const val SELLER_SECTION_STATE_FIELD = "state"
    const val SELLER_SECTION_AVAILABLE_FIELD = "available"
    const val SELLER_SECTION_UPDATED_AT_FIELD = "updated_at"

    const val SELLER_SECTION_STATE_AVAILABLE = "available"
    const val SELLER_SECTION_STATE_PENDING = "pending"
    const val SELLER_SECTION_STATE_PREPARING = "preparing"
    const val SELLER_SECTION_STATE_SHIPPED = "shipped"
    const val SELLER_SECTION_STATE_DELIVERED = "delivered"

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
    const val SELLER_ITEM_SELLER_ID_FIELD = "seller_id"
    const val SELLER_ITEM_DESCRIPTION_FIELD = "description"
    const val SELLER_ITEM_DESCRIPTION_ZH_FIELD = "description_zh"
    const val SELLER_ITEM_IMAGE_URL_FIELD = "image_url"
    const val SELLER_ITEM_PRICE_FIELD = "price"
    const val SELLER_ITEM_COUNT_FIELD = "count"
    const val SELLER_ITEM_AVAILABLE_FIELD = "available"
    const val SELLER_ITEM_UPDATED_AT_FIELD = "updated_at"

    // Payment Methods
    const val PAYMENT_METHODS_COLLECTION = "payment_methods"

    const val PAYMENT_METHOD_ID_FIELD = "id"
    const val PAYMENT_METHOD_IDENTIFIER_FIELD = "identifier"
    const val PAYMENT_METHOD_ENABLED_FIELD = "enabled"


    // Cloud Functions APIs
    const val CF_REQUEST_URL = "https://us-central1-foobar-group-delivery-app.cloudfunctions.net/api/"
    const val CF_AUTH_HEADER = "Authorization"

    // Generic Responses
    const val CF_SUCCESS_RESPONSE_DATA_OBJECT = "data"
    const val CF_ERROR_RESPONSE_ERROR_OBJECT = "error"
    const val CF_ERROR_RESPONSE_CODE_FIELD = "code"
    const val CF_ERROR_RESPONSE_MESSAGE_FIELD = "message"

    // Google Map Directions APIs
    const val GM_DIR_URL = "https://maps.googleapis.com/maps/api/"
    const val GM_DIR_KEY = "key"
    const val GM_DIR_ORIGIN = "origin"
    const val GM_DIR_DEST = "destination"

    const val ADD_USER_CART_ITEM_REQUEST_SELLER_ID = "seller_id"
    const val ADD_USER_CART_ITEM_REQUEST_SECTION_ID = "section_id"
    const val ADD_USER_CART_ITEM_REQUEST_ITEM_ID = "item_id"
    const val ADD_USER_CART_ITEM_REQUEST_AMOUNTS = "amounts"

    const val UPDATE_USER_CART_ITEM_REQUEST_CART_ITEM_ID = "cart_item_id"
    const val UPDATE_USER_CART_ITEM_REQUEST_AMOUNTS = "amounts"
}