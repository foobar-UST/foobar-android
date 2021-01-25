package com.foobarust.data.db

import androidx.room.TypeConverter
import java.util.*

/**
 * Created by kevin on 1/23/21
 */

object RoomConverters {
    @JvmStatic
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @JvmStatic
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
