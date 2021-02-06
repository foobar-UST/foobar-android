package com.foobarust.android.utils

import android.content.Context
import javax.inject.Inject

/**
 * Created by kevin on 2/4/21
 */

class ResourceIdentifier @Inject constructor(private val context: Context) {

    /**
     * Get string resource by id name (R.id.[name])
     */
    fun getString(name: String, args: Array<out String>? = null): String {
        val identifier = context.resources.getIdentifier(
            name, "string", context.packageName
        )
        return if (!args.isNullOrEmpty()) {
            context.getString(identifier, *args)
        } else {
            context.getString(identifier)
        }
    }
}