package com.foobarust.domain.utils

import java.text.SimpleDateFormat
import java.util.*

fun Date.format(format: String): String {
    return SimpleDateFormat(format, Locale.US).apply {
        timeZone = TimeZone.getTimeZone("Asia/Hong_Kong")
    }.format(this)
}

fun Date.isSameDay(compareTo: Date): Boolean {
    val cal1 = Calendar.getInstance().apply { time = this@isSameDay }
    val cal2 = Calendar.getInstance().apply { time = compareTo }

    return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
        cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
        cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

fun Date.getTimeBy12Hour(): String {
    return SimpleDateFormat("hh:mm a", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("Asia/Hong_Kong")
    }.format(this)
}