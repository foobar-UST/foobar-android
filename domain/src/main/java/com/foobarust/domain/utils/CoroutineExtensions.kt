package com.foobarust.domain.utils

import kotlinx.coroutines.Job

/**
 * Created by kevin on 12/30/20
 */

/**
 * Cancel the Job if it's active.
 */
fun Job?.cancelIfActive() {
    if (this?.isActive == true) {
        cancel()
    }
}