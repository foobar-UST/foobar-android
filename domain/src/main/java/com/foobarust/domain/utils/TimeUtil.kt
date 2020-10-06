package com.foobarust.domain.utils

import java.util.*

/**
 * Created by kevin on 10/4/20
 */

object TimeUtil {

    private const val TIME_FORMAT = "\\d{2}:\\d{2}"

    fun isCurrentTimeWithinRange(startTime: String, endTime: String): Boolean {
        val current = getCurrentCalendar()
        val start = getTargetTimeCalendar(startTime)
        val end = getTargetTimeCalendar(endTime)

        return current.after(start) && current.before(end)
    }

    private fun getCurrentCalendar(): Calendar {
        return Calendar.getInstance(
            TimeZone.getTimeZone("Asia/Hong_Kong")
        )
    }

    private fun getTargetTimeCalendar(targetTime: String): Calendar {
        if (!targetTime.matches(Regex(TIME_FORMAT))) {
            throw IllegalArgumentException("Invalid time format.")
        }

        val time = targetTime.split(':')

        return Calendar.getInstance(
            TimeZone.getTimeZone("Asia/Hong_Kong")
        ).apply {
            set(Calendar.HOUR_OF_DAY, time[0].toInt())
            set(Calendar.MINUTE, time[1].toInt())
        }
    }
}