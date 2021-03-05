package com.foobarust.data.constants

/**
 * Created by kevin on 9/25/20
 */

object Constants {
    const val APP_DB_NAME = "foobar_db"
    const val PREFS_NAME = "foobarust"

    // Users
    const val USERS_CACHE_ENTITY = "users"
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
    const val USER_DEVICE_IDS_FIELD = "deviceIds"
    const val USER_UPDATED_AT_FIELD = "updated_at"
    const val USER_CREATED_REST_FIELD = "createdRest"

    // User Cart
    const val USER_CARTS_COLLECTION = "user_carts"

    const val USER_CART_USER_ID_FIELD = "user_id"
    const val USER_CART_TITLE_FIELD = "title"
    const val USER_CART_TITLE_ZH_FIELD = "title_zh"
    const val USER_CART_SELLER_ID_FIELD = "seller_id"
    const val USER_CART_SELLER_NAME_FIELD = "seller_name"
    const val USER_CART_SELLER_NAME_ZH_FIELD = "seller_name_zh"
    const val USER_CART_SELLER_TYPE_FIELD = "seller_type"
    const val USER_CART_SECTION_ID_FIELD = "section_id"
    const val USER_CART_SECTION_TITLE_FIELD = "section_title"
    const val USER_CART_SECTION_TITLE_ZH_FIELD = "section_title_zh"
    const val USER_CART_DELIVERY_TIME_FIELD = "delivery_time"
    const val USER_CART_IMAGE_URL_FIELD = "image_url"
    const val USER_CART_PICKUP_LOCATION_FIELD = "pickup_location"
    const val USER_CART_ITEMS_COUNT_FIELD = "items_count"
    const val USER_CART_DELIVERY_COST_FIELD = "delivery_cost"
    const val USER_CART_SUBTOTAL_COST_FIELD = "subtotal_cost"
    const val USER_CART_TOTAL_COST_FIELD = "total_cost"
    const val USER_CART_SYNC_REQUIRED_FIELD = "sync_required"
    const val USER_CART_UPDATED_AT_FIELD = "updated_at"

    // Cart items
    const val USER_CART_ITEMS_SUB_COLLECTION = "cart_items"

    const val USER_CART_ITEMS_ID_FIELD = "id"
    const val USER_CART_ITEMS_USER_ID_FIELD = "user_id"
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
    const val SELLER_ORDER_RATING_FIELD = "order_rating"
    const val SELLER_DELIVERY_RATING_FIELD = "delivery_rating"
    const val SELLER_RATING_COUNT_FIELD = "rating_count"
    const val SELLER_TYPE_FIELD = "type"
    const val SELLER_WEBSITE_FIELD = "website"
    const val SELLER_PHONE_NUM_FIELD = "phone_num"
    const val SELLER_LOCATION_FIELD = "location"
    const val SELLER_ONLINE_FIELD = "online"
    const val SELLER_NOTICE_FIELD = "notice"
    const val SELLER_TAGS_FIELD = "tags"
    const val SELLER_BY_USER_ID = "by_user_id"

    // Geo Location
    const val GEO_LOCATION_ADDRESS_FIELD = "address"
    const val GEO_LOCATION_ADDRESS_ZH_FIELD = "address_zh"
    const val GEO_LOCATION_GEOPOINT_FIELD = "geopoint"

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
    const val SELLER_SECTION_DELIVERY_COST_FIELD = "delivery_cost"
    const val SELLER_SECTION_DELIVERY_TIME_FIELD = "delivery_time"
    const val SELLER_SECTION_DELIVERY_LOCATION_FIELD = "delivery_location"
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
    const val SELLER_SECTION_STATE_PROCESSING = "processing"
    const val SELLER_SECTION_STATE_PREPARING = "preparing"
    const val SELLER_SECTION_STATE_SHIPPED = "shipped"
    const val SELLER_SECTION_STATE_DELIVERED = "delivered"

    // Seller Rating
    const val SELLER_RATINGS_SUB_COLLECTION = "ratings"
    const val SELLER_RATINGS_BASIC_SUB_COLLECTION = "ratings_basic"

    const val SELLER_RATING_ID_FIELD = "id"
    const val SELLER_RATING_USERNAME_FIELD = "username"
    const val SELLER_RATING_USER_PHOTO_URL_FIELD = "user_photo_url"
    const val SELLER_RATING_ORDER_ID_FIELD = "order_id"
    const val SELLER_RATING_ORDER_RATING_FIELD = "order_rating"
    const val SELLER_RATING_DELIVERY_RATING_FIELD = "delivery_rating"
    const val SELLER_RATING_CREATED_AT_FIELD = "created_at"

    const val SELLER_RATING_COUNT_EXCELLENT_FIELD = "excellent"
    const val SELLER_RATING_COUNT_VERY_GOOD_FIELD = "very_good"
    const val SELLER_RATING_COUNT_GOOD_FIELD = "good"
    const val SELLER_RATING_COUNT_FAIR_FIELD = "fair"
    const val SELLER_RATING_COUNT_POOR_FIELD = "poor"

    // Advertise
    const val SELLER_ADVERTISES_SUB_COLLECTION = "advertises"
    const val ADVERTISES_BASIC_COLLECTION = "advertises_basic"

    const val ADVERTISE_ID_FIELD = "id"
    const val ADVERTISE_URL_FIELD = "url"
    const val ADVERTISE_IMAGE_URL_FIELD = "image_url"
    const val ADVERTISE_CREATED_AT_FIELD = "created_at"
    const val ADVERTISE_SELLER_TYPE_FIELD = "seller_type"
    const val ADVERTISE_RANDOM_FIELD = "random"

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

    // Order
    const val ORDERS_BASIC_ENTITY = "orders_basic"
    const val ORDERS_ENTITY = "orders"
    const val ORDERS_COLLECTION = "orders"
    const val ORDERS_BASIC_COLLECTION = "orders_basic"
    const val ORDER_ITEMS_ENTITY = "order_items"

    const val ORDER_ID_FIELD = "id"
    const val ORDER_TITLE_FIELD = "title"
    const val ORDER_TITLE_ZH_FIELD = "title_zh"
    const val ORDER_USER_ID_FIELD = "user_id"
    const val ORDER_SELLER_ID_FIELD = "seller_id"
    const val ORDER_SELLER_NAME_FIELD = "seller_name"
    const val ORDER_SELLER_NAME_ZH_FIELD = "seller_name_zh"
    const val ORDER_SECTION_ID_FIELD = "section_id"
    const val ORDER_SECTION_TITLE_FIELD = "section_title"
    const val ORDER_SECTION_TITLE_ZH_FIELD = "section_title_zh"
    const val ORDER_DELIVERER_ID_FIELD = "deliverer_id"
    const val ORDER_IDENTIFIER_FIELD = "identifier"
    const val ORDER_IMAGE_URL_FIELD = "image_url"
    const val ORDER_TYPE_FIELD = "type"
    const val ORDER_ORDER_ITEMS_FIELD = "order_items"
    const val ORDER_ORDER_ITEMS_COUNT_FIELD = "order_items_count"
    const val ORDER_STATE_FIELD = "state"
    const val ORDER_IS_PAID_FIELD = "is_paid"
    const val ORDER_PAYMENT_METHOD_FIELD = "payment_method"
    const val ORDER_MESSAGE_FIELD = "message"
    const val ORDER_DELIVERY_LOCATION_FIELD = "delivery_location"
    const val ORDER_SUBTOTAL_COST_FIELD = "subtotal_cost"
    const val ORDER_DELIVERY_COST_FIELD = "delivery_cost"
    const val ORDER_TOTAL_COST_FIELD = "total_cost"
    const val ORDER_CREATED_AT_FIELD = "created_at"
    const val ORDER_UPDATED_AT_FIELD = "updated_at"

    const val ORDER_BASIC_DELIVERY_ADDRESS_FIELD = "delivery_address"
    const val ORDER_BASIC_DELIVERY_ADDRESS_ZH_FIELD = "delivery_address_zh"

    const val ORDER_DELIVERY_LOCATION_LATITUDE_FIELD = "delivery_location_latitude"
    const val ORDER_DELIVERY_LOCATION_LONGITUDE_FIELD = "delivery_location_longitude"

    // Order Item
    const val ORDER_ITEM_ID_FIELD = "id"
    const val ORDER_ITEM_ITEM_ID_FIELD = "item_id"
    const val ORDER_ITEM_ITEM_SELLER_ID_FIELD = "item_seller_id"
    const val ORDER_ITEM_ITEM_TITLE_FIELD = "item_title"
    const val ORDER_ITEM_ITEM_TITLE_ZH_FIELD = "item_title_zh"
    const val ORDER_ITEM_ITEM_PRICE_FIELD = "item_price"
    const val ORDER_ITEM_ITEM_IMAGE_URL_FIELD = "item_image_url"
    const val ORDER_ITEM_AMOUNTS_FIELD = "amounts"
    const val ORDER_ITEM_TOTAL_PRICE_FIELD = "total_price"
    const val ORDER_ITEM_ORDER_ID_FIELD = "order_id"

    // Order States
    const val ORDER_STATE_PROCESSING = "processing"
    const val ORDER_STATE_PREPARING = "preparing"
    const val ORDER_STATE_IN_TRANSIT = "in_transit"
    const val ORDER_STATE_READY_FOR_PICK_UP = "ready_for_pick_up"
    const val ORDER_STATE_DELIVERED = "delivered"
    const val ORDER_STATE_ARCHIVED = "archived"
    const val ORDER_STATE_CANCELLED = "cancelled"

    // Payment Methods
    const val PAYMENT_METHODS_COLLECTION = "payment_methods"

    const val PAYMENT_METHOD_ID_FIELD = "id"
    const val PAYMENT_METHOD_IDENTIFIER_FIELD = "identifier"
    const val PAYMENT_METHOD_ENABLED_FIELD = "enabled"

    // Item Categories
    const val ITEM_CATEGORIES_COLLECTION = "item_categories"

    const val ITEM_CATEGORY_ID_FIELD = "id"
    const val ITEM_CATEGORY_TAG_FIELD = "tag"
    const val ITEM_CATEGORY_TITLE_FIELD = "title"
    const val ITEM_CATEGORY_TITLE_ZH_FIELD = "title_zh"
    const val ITEM_CATEGORY_IMAGE_URL_FIELD = "image_url"

    // Cloud Functions APIs
    const val REMOTE_REQUEST_URL = "https://asia-east2-foobar-group-delivery-app.cloudfunctions.net/api/"
    const val REMOTE_AUTH_HEADER = "Authorization"

    // Generic Responses
    const val REMOTE_SUCCESS_RESPONSE_DATA_OBJECT = "data"
    const val REMOTE_ERROR_RESPONSE_ERROR_OBJECT = "error"
    const val REMOTE_ERROR_RESPONSE_CODE_FIELD = "code"
    const val REMOTE_ERROR_RESPONSE_MESSAGE_FIELD = "message"

    // Google Maps APIs
    const val MAPS_API_URL = "https://maps.googleapis.com/maps/api/"
    const val MAPS_DIRECTIONS_END_POINT = "directions/json"
    const val MAPS_DIRECTIONS_PARAM_KEY = "key"
    const val MAPS_DIRECTIONS_PARAM_ORIGIN = "origin"
    const val MAPS_DIRECTIONS_PARAM_DEST = "destination"
    const val MAPS_STATIC_MAP_END_POINT = "staticmap"
    const val MAPS_STATIC_MAP_PARAM_KEY = "key"
    const val MAPS_STATIC_MAP_PARAM_AUTO_SCALE = "autoscale"
    const val MAPS_STATIC_MAP_PARAM_SIZE = "size"
    const val MAPS_STATIC_MAP_PARAM_FORMAT = "format"
    const val MAPS_STATIC_MAP_PARAM_VISUAL_REFRESH = "visual_refresh"
    const val MAPS_STATIC_MAP_PARAM_MARKERS = "markers"

    const val UPDATE_USER_DETAIL_REQUEST_NAME = "name"
    const val UPDATE_USER_DETAIL_REQUEST_PHONE_NUM = "phone_num"

    const val ADD_USER_CART_ITEM_REQUEST_SECTION_ID = "section_id"
    const val ADD_USER_CART_ITEM_REQUEST_ITEM_ID = "item_id"
    const val ADD_USER_CART_ITEM_REQUEST_AMOUNTS = "amounts"

    const val PLACE_ORDER_REQUEST_MESSAGE = "message"
    const val PLACE_ORDER_REQUEST_PAYMENT_METHOD = "payment_method"
    const val PLACE_ORDER_RESPONSE_ID = "order_id"
    const val PLACE_ORDER_RESPONSE_IDENTIFIER = "order_identifier"

    const val UPDATE_USER_CART_ITEM_REQUEST_CART_ITEM_ID = "cart_item_id"
    const val UPDATE_USER_CART_ITEM_REQUEST_AMOUNTS = "amounts"

    const val INSERT_DEVICE_TOKEN_REQUEST_TOKEN = "token"
    const val LINK_DEVICE_TOKEN_REQUEST_TOKEN = "token"
    const val UNLINK_DEVICE_TOKEN_REQUEST_TOKEN = "token"

    const val SEARCH_SELLERS_REQUEST_SEARCH_QUERY = "query"

    const val SUBMIT_ORDER_RATING_ORDER_ID = "order_id"
    const val SUBMIT_ORDER_RATING_ORDER_RATING = "order_rating"
    const val SUBMIT_ORDER_RATING_DELIVERY_RATING = "delivery_rating"
}