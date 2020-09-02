package com.foobarust.android.utils

import android.app.Activity
import android.widget.Toast
import androidx.fragment.app.Fragment

/**
 * Created by kevin on 8/14/20
 *
 * Toast extension methods for activity and fragments
 */
fun Activity.showShortToast(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Activity.showLongToast(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Fragment.showShortToast(message: String?) {
    Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.showLongToast(message: String?) {
    Toast.makeText(this.context, message, Toast.LENGTH_LONG).show()
}
