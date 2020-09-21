package com.foobarust.android.utils

import android.app.Activity
import android.content.Intent
import com.foobarust.android.R
import kotlin.reflect.KClass

/**
 * Created by kevin on 9/6/20
 */

fun Activity.navigateTo(
    destination: KClass<*>,
    fadeAnim: Boolean = false,
    finishEnd: Boolean = false
) {
    val intent = Intent(this, destination.java)
    startActivity(intent)

    if (fadeAnim) {
        overridePendingTransition(
            R.anim.nav_default_enter_anim,
            R.anim.nav_default_exit_anim
        )
    }

    if (finishEnd) finish()
}