package com.foobarust.android.utils

import android.app.Activity
import android.widget.Toast
import androidx.fragment.app.Fragment

/**
 * Created by kevin on 8/14/20
 */

fun Activity.showShortToast(message: String?) {
    message?.let {
        Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
    }
}

fun Activity.showLongToast(message: String?) {
    message?.let {
        Toast.makeText(this, it, Toast.LENGTH_LONG).show()
    }
}

fun Fragment.showShortToast(message: String?) {
    message?.let {
        Toast.makeText(this.context, it, Toast.LENGTH_SHORT).show()
    }
}

fun Fragment.showLongToast(message: String?) {
    message?.let {
        Toast.makeText(this.context, it, Toast.LENGTH_LONG).show()
    }
}