package com.foobarust.android.utils

import android.util.Log
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.absoluteValue

/**
 * Created by kevin on 10/17/20
 */

abstract class AppBarStateChangedListener : AppBarLayout.OnOffsetChangedListener {

    private var currentState: State = State.IDLE

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        Log.d("AppBarStateChanged", "$verticalOffset")
        when {
            verticalOffset == 0 -> {
                if (currentState != State.EXPANDED) {
                    onStateChanged(appBarLayout, State.EXPANDED)
                }
                currentState = State.EXPANDED
            }
            verticalOffset.absoluteValue >= appBarLayout.totalScrollRange -> {
                if (currentState != State.COLLAPSED) {
                    onStateChanged(appBarLayout, State.COLLAPSED)
                }
                currentState = State.COLLAPSED
            }
            else -> {
                if (currentState != State.IDLE) {
                    onStateChanged(appBarLayout, State.IDLE)
                }
                currentState = State.IDLE
            }
        }
    }

    abstract fun onStateChanged(appBarLayout: AppBarLayout, state: State)

    enum class State {
        EXPANDED,
        COLLAPSED,
        IDLE
    }
}