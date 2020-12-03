package com.foobarust.data.models.seller

import com.foobarust.data.common.Constants.CATALOG_SCHEDULE_END_TIME_FIELD
import com.foobarust.data.common.Constants.CATALOG_SCHEDULE_START_TIME_FIELD
import com.google.firebase.firestore.PropertyName

/**
 * Created by kevin on 11/30/20
 */
data class CatalogScheduleEntity(
    @JvmField
    @PropertyName(CATALOG_SCHEDULE_START_TIME_FIELD)
    val startTime: String? = null,

    @JvmField
    @PropertyName(CATALOG_SCHEDULE_END_TIME_FIELD)
    val endTime: String? = null
)