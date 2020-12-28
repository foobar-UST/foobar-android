package com.foobarust.domain.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by kevin on 12/21/20
 */

object TimeUtils {

    /**
     * Return 12-hour time string (e.g. 12:30 PM)
     * @param date the date to format
     * @return 12-hour time string
     */
    fun get12HourString(date: Date): String {
        val formatter = SimpleDateFormat("hh:mm a", Locale.US)
        formatter.timeZone = TimeZone.getTimeZone("Asia/Hong_Kong")
        return formatter.format(date)
    }
}