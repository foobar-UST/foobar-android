package com.foobarust.android.utils

import com.foobarust.android.utils.AppBarLayoutState.*
import com.google.android.material.appbar.AppBarLayout
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlin.math.absoluteValue

/**
 * Created by kevin on 1/27/21
 */

/**
 * Flow emitting scroll state of [AppBarLayout].
 */
fun AppBarLayout.state(): Flow<AppBarLayoutState> = channelFlow {
    var currentState = IDLE
    val listener = AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
        when {
            verticalOffset == 0 -> {
                if (currentState != EXPANDED) {
                    channel.offer(EXPANDED)
                }
                currentState = EXPANDED
            }
            verticalOffset.absoluteValue >= appBarLayout.totalScrollRange -> {
                if (currentState != COLLAPSED) {
                    channel.offer(COLLAPSED)
                }
                currentState = COLLAPSED
            }
            else -> {
                if (currentState != IDLE) {
                    channel.offer(IDLE)
                }
                currentState = IDLE
            }
        }
    }

    addOnOffsetChangedListener(listener)

    awaitClose { removeOnOffsetChangedListener(listener) }
}

enum class AppBarLayoutState {
    IDLE,
    EXPANDED,
    COLLAPSED
}