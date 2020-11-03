package com.foobarust.domain.models

import java.util.*

/**
 * Created by kevin on 10/4/20
 */

data class SellerCatalog(
    val id: String,
    val name: String,
    val available: Boolean,
    val startTime: String?,
    val endTime: String?
)

fun SellerCatalog.purchasable(): Boolean {
    // For items that sell all day, check its availability
    if (startTime == null || endTime == null) return available

    // Parse start time and end time
    val startHoursMins = startTime.split(':')
    val endHoursMins = endTime.split(':')

    val localTimeZone = TimeZone.getTimeZone("Asia/Hong_Kong")

    val currentCal = Calendar.getInstance(localTimeZone)
    val startTimeCal = Calendar.getInstance(localTimeZone).apply {
        set(Calendar.HOUR_OF_DAY, startHoursMins[0].toInt())
        set(Calendar.MINUTE, startHoursMins[1].toInt())
    }
    val endTimeCal = Calendar.getInstance(localTimeZone).apply {
        set(Calendar.HOUR_OF_DAY, endHoursMins[0].toInt())
        set(Calendar.MINUTE, endHoursMins[1].toInt())
    }

    return available && currentCal.after(startTimeCal) && currentCal.before(endTimeCal)
}

fun SellerCatalog.getFormattedTitle(): String {
    if (startTime == null || endTime == null) return name

    return "$name\n($startTime - $endTime)"
}