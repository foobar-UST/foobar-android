package com.foobarust.data.utils

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Created by kevin on 10/5/20
 */

class DateUtil @Inject constructor(
    private val dateFormat: SimpleDateFormat
) {

    fun convertDateStringToTimestamp(date: String): Timestamp {
        return Timestamp(dateFormat.parse(date)!!)
    }

    fun convertTimestampToDateString(timestamp: Timestamp): String {
        return dateFormat.format(timestamp.toDate())
    }

    fun getCurrentDateString(): String {
        return dateFormat.format(Date())
    }
}