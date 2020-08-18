package com.foobarust.android.utils

import android.app.Activity
import android.widget.Toast
import androidx.fragment.app.Fragment

/**
 * Created by kevin on 8/14/20
 *
 * Toast extension methods for activity and fragments
 */
fun Activity.shortToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Activity.longToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Fragment.shortToast(message: String?) {
    Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.longToast(message: String?) {
    Toast.makeText(this.context, message, Toast.LENGTH_LONG).show()
}
