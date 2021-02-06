package com.foobarust.android.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

/**
 * Created by kevin on 1/10/21
 */

/**
 * Get file extensions from uri
 * e.g. ".jpg", ".png"
 * @return file extension
 */
fun Uri.getFileExtension(context: Context): String? {
    return let {
        context.contentResolver.query(
            it, null, null, null, null
        )
    }?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()

        val fileName = cursor.getString(nameIndex)
        val dotIndex = fileName.lastIndexOf('.')

        fileName.substring(dotIndex)
    }
}