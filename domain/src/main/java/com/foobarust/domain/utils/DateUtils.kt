package com.foobarust.domain.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    /**
     * Checks if two dates are on the same day ignoring time.
     * @param date1  the first date
     * @param date2  the second date
     * @return true if they represent the same day
     */
    fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance()
        cal1.time = date1
        val cal2 = Calendar.getInstance()
        cal2.time = date2
        return isSameDay(cal1, cal2)
    }

    /**
     * Checks if two calendars represent the same day ignoring time.
     * @param cal1  the first calendar
     * @param cal2  the second calendar
     * @return true if they represent the same day
     */
    fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
            cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    /**
     * Checks if a date is today.
     * @param date the date
     * @return true if the date is today.
     */
    fun isToday(date: Date): Boolean {
        return isSameDay(date, Calendar.getInstance().time)
    }

    /**
     * Checks if a calendar date is today.
     * @param cal the calendar
     * @return true if cal date is today
     */
    fun isToday(cal: Calendar): Boolean {
        return isSameDay(cal, Calendar.getInstance())
    }

    /**
     * Checks if the first date is before the second date ignoring time.
     * @param date1 the first date
     * @param date2 the second date
     * @return true if the first date day is before the second date day.
     */
    fun isBeforeDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance()
        cal1.time = date1
        val cal2 = Calendar.getInstance()
        cal2.time = date2
        return isBeforeDay(cal1, cal2)
    }

    /**
     * Checks if the first calendar date is before the second calendar date ignoring time.
     * @param cal1 the first calendar
     * @param cal2 the second calendar
     * @return true if cal1 date is before cal2 date ignoring time.
     */
    fun isBeforeDay(cal1: Calendar, cal2: Calendar): Boolean {
        if (cal1.get(Calendar.ERA) < cal2.get(Calendar.ERA)) return true
        if (cal1.get(Calendar.ERA) > cal2.get(Calendar.ERA)) return false
        if (cal1.get(Calendar.YEAR) < cal2.get(Calendar.YEAR)) return true
        if (cal1.get(Calendar.YEAR) > cal2.get(Calendar.YEAR)) return false
        return cal1.get(Calendar.DAY_OF_YEAR) < cal2.get(Calendar.DAY_OF_YEAR)
    }

    /**
     * Checks if the first date is after the second date ignoring time.
     * @param date1 the first date
     * @param date2 the second date
     * @return true if the first date day is after the second date day.
     */
    fun isAfterDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance()
        cal1.time = date1
        val cal2 = Calendar.getInstance()
        cal2.time = date2
        return isAfterDay(cal1, cal2)
    }

    /**
     * Checks if the first calendar date is after the second calendar date ignoring time.
     * @param cal1 the first calendar
     * @param cal2 the second calendar
     * @return true if cal1 date is after cal2 date ignoring time.
     */
    fun isAfterDay(cal1: Calendar, cal2: Calendar): Boolean {
        if (cal1.get(Calendar.ERA) < cal2.get(Calendar.ERA)) return false
        if (cal1.get(Calendar.ERA) > cal2.get(Calendar.ERA)) return true
        if (cal1.get(Calendar.YEAR) < cal2.get(Calendar.YEAR)) return false
        if (cal1.get(Calendar.YEAR) > cal2.get(Calendar.YEAR)) return true
        return cal1.get(Calendar.DAY_OF_YEAR) > cal2.get(Calendar.DAY_OF_YEAR)
    }

    /**
     * Checks if a date is after today and within a number of days in the future.
     * @param date the date to check
     * @param days the number of days.
     * @return true if the date day is after today and within days in the future.
     */
    fun isWithinDaysFuture(date: Date, days: Int): Boolean {
        val cal = Calendar.getInstance()
        cal.time = date
        return isWithinDaysFuture(cal, days)
    }

    /**
     * Checks if a calendar date is after today and within a number of days in the future.
     * @param cal the calendar
     * @param days the number of days.
     * @return true if the calendar date day is after today and within days in the future.
     */
    fun isWithinDaysFuture(cal: Calendar, days: Int): Boolean {
        val today = Calendar.getInstance()
        val future = Calendar.getInstance()
        future.add(Calendar.DAY_OF_YEAR, days)
        return isAfterDay(cal, today) && !isAfterDay(cal, future)
    }

    /**
     * Returns the given date with the time set to the start of the day.
     * @param date the date
     * @return the start time of the date
     */
    fun getStart(date: Date): Date {
        return clearTime(date)
    }

    /**
     * Returns the given date with the time set to the start of the day.
     * @param date the date
     * @return the start time of the date
     */
    fun clearTime(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    /**
     * Determines whether or not a date has any time values.
     * @param date The date.
     * @return true if the date is not null and any of the date's hour, minute,
     * seconds or millisecond values are greater than zero.
     */
    fun hasTime(date: Date): Boolean {
        val calendar = Calendar.getInstance()
        calendar.time = date

        if (calendar.get(Calendar.HOUR_OF_DAY) > 0) return true
        if (calendar.get(Calendar.MINUTE) > 0) return true
        if (calendar.get(Calendar.SECOND) > 0) return true
        if (calendar.get(Calendar.MILLISECOND) > 0) return true

        return false
    }

    /**
     * Returns the given date with time set to the end of the day.
     * @param date the date
     * @return the end time of the date
     */
    fun getEnd(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.time
    }

    /**
     * Returns the maximum of two dates. A null date is treated as being less
     * than any non-null date.
     * @param date1 the first date
     * @param date2 the second date
     * @return the maximum date
     */
    fun max(date1: Date, date2: Date): Date {
        return if (date1.after(date2)) date1 else date2
    }

    /**
     * Returns the minimum of two dates. A null date is treated as being greater
     * than any non-null date.
     * @param date1 the first date
     * @param date2 the second date
     * @return the minimum date
     */
    fun min(date1: Date, date2: Date): Date {
        return if (date1.before(date2)) date1 else date2
    }

    /**
     * Return a formatted date string.
     * @param date the date
     * @param format date format
     * @return the date string
     */
    fun getDateString(date: Date, format: String): String {
        val formatter = SimpleDateFormat(format, Locale.US)
        formatter.timeZone = TimeZone.getTimeZone("Asia/Hong_Kong")
        return formatter.format(date)
    }
}